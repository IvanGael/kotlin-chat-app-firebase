package com.ivan.work.chatapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ivan.work.chatapp.models.User
import java.io.ByteArrayOutputStream
import java.util.UUID

class SettingsActivity : AppCompatActivity() {

    private lateinit var imgViewSettings: ShapeableImageView
    private lateinit var txtLayoutSettingsNom: TextInputLayout
    private lateinit var txtLayoutSettingsPrenom: TextInputLayout
    private lateinit var txtLayoutSettingsEmail: TextInputLayout
    private lateinit var btnEditAccount: MaterialButton

    private lateinit var nom:String
    private lateinit var prenom:String

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser
    private lateinit var storage: FirebaseStorage

    private var isImageChanged = false


    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initialize()

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser!!



        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            it.let {
                imgViewSettings.setImageURI(it)
                isImageChanged = true
            }
        }

        imgViewSettings.setOnClickListener {
            pickImage.launch("image/*")
        }

        if(currentUser != null){
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { result ->
                    if(result != null){
                        val user = result.toObject(User::class.java)!!
                        user?.let {
                            it.uuid = currentUser.uid
                            txtLayoutSettingsNom.editText?.setText(it.nom)
                            txtLayoutSettingsPrenom.editText?.setText(it.prenom)
                            txtLayoutSettingsEmail.editText?.setText(it.email)
                        }
                        user.picture?.let {
                            Glide.with(this)
                                .load(it)
                                .placeholder(R.drawable.man)
                                .into(imgViewSettings)
                        }

                        btnEditAccount.setOnClickListener {

                            nom = txtLayoutSettingsNom.editText?.text.toString()
                            prenom = txtLayoutSettingsPrenom.editText?.text.toString()

                            txtLayoutSettingsNom.isErrorEnabled = false
                            txtLayoutSettingsPrenom.isErrorEnabled = false

                            if(nom.isEmpty() || prenom.isEmpty()){
                                if(nom.isEmpty()){
                                    txtLayoutSettingsNom.error = "firstname is required"
                                    txtLayoutSettingsNom.isErrorEnabled = true
                                }
                                if(prenom.isEmpty()){
                                    txtLayoutSettingsPrenom.error = "Surname is required"
                                    txtLayoutSettingsPrenom.isErrorEnabled = true
                                }
                            } else {
                                if(isImageChanged){
                                    uploadToFirebase(user)
                                } else if(nom != user.nom || prenom != user.prenom){
                                    if(nom == user.nom){
                                        user.nom = nom
                                        updateUser(user)
                                    }
                                    if(prenom == user.prenom){
                                        user.prenom = prenom
                                        updateUser(user)
                                    }
                                } else if(nom != user.nom && prenom != user.prenom){
                                    user.nom = nom
                                    user.prenom = prenom
                                    updateUser(user)
                                } else {
                                    Toast.makeText(this,"Your information are already up to date!",Toast.LENGTH_LONG).show()
                                }


                            }

                        }
                    }
                }
        } else {
            Toast.makeText(this,"User not found!",Toast.LENGTH_LONG).show()
        }

    }

    private fun updateUser(user: User){
        val updatedUser = hashMapOf<String, Any>(
            "nom" to nom,
            "prenom" to prenom,
            "picture" to (user.picture ?: "")
        )

        db.collection("users").document(user.uuid).update(updatedUser)
            .addOnSuccessListener {
                Toast.makeText(this,"User updated successfully!",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Something wrong!",Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadToFirebase(user: User){
        storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${user.uuid}")
        val bitmap = (imgViewSettings.drawable as BitmapDrawable).bitmap
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArray)
        val data = byteArray.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri->
                user.picture = uri.toString()
                updateUser(user)
            }
        }
    }

    private fun initialize(){
        imgViewSettings = findViewById(R.id.img_view_settings)
        txtLayoutSettingsNom = findViewById(R.id.txt_layout_settings_nom)
        txtLayoutSettingsPrenom = findViewById(R.id.txt_layout_settings_prenom)
        txtLayoutSettingsEmail = findViewById(R.id.txt_layout_settings_email)
        btnEditAccount = findViewById(R.id.btn_edit_account)
    }
}