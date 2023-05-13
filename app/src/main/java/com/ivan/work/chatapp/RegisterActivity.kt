package com.ivan.work.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var txtLayoutRegisterNom: TextInputLayout
    private lateinit var txtLayoutRegisterPrenom: TextInputLayout
    private lateinit var txtLayoutRegisterPseudo: TextInputLayout
    private lateinit var txtLayoutRegisterEmail: TextInputLayout
    private lateinit var txtLayoutRegisterPassword : TextInputLayout
    private lateinit var txtLayoutRegisterCfPassword : TextInputLayout
    private lateinit var btnCreateAccount: MaterialButton

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initialize()

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    private fun initialize(){
        txtLayoutRegisterNom = findViewById(R.id.txt_layout_register_nom)
        txtLayoutRegisterPrenom = findViewById(R.id.txt_layout_register_prenom)
        txtLayoutRegisterPseudo = findViewById(R.id.txt_layout_register_pseudo)
        txtLayoutRegisterEmail = findViewById(R.id.txt_layout_register_email)
        txtLayoutRegisterPassword = findViewById(R.id.txt_layout_register_password)
        txtLayoutRegisterCfPassword = findViewById(R.id.txt_layout_register_cfpassword)
        btnCreateAccount = findViewById(R.id.btn_create_account)
    }

    override fun onStart() {
        super.onStart()

        btnCreateAccount.setOnClickListener {
            val nom = txtLayoutRegisterNom.editText?.text.toString()
            val prenom = txtLayoutRegisterPrenom.editText?.text.toString()
            val pseudo = txtLayoutRegisterPseudo.editText?.text.toString()
            val email = txtLayoutRegisterEmail.editText?.text.toString()
            val password = txtLayoutRegisterPassword.editText?.text.toString()
            val cfPassword = txtLayoutRegisterCfPassword.editText?.text.toString()

            initErrors()

            if(nom.isEmpty() || prenom.isEmpty() || pseudo.isEmpty() || email.isEmpty()
                || password.isEmpty() || cfPassword.isEmpty()){
                if(nom.isEmpty()){
                    txtLayoutRegisterNom.error = "Firstname is required!"
                    txtLayoutRegisterNom.isErrorEnabled = true
                }
                if(prenom.isEmpty()){
                    txtLayoutRegisterPrenom.error = "surname is required!"
                    txtLayoutRegisterPrenom.isErrorEnabled = true
                }
                if(pseudo.isEmpty()){
                    txtLayoutRegisterPseudo.error = "Pseudo is required!"
                    txtLayoutRegisterPseudo.isErrorEnabled = true
                }
                if(email.isEmpty()){
                    txtLayoutRegisterEmail.error = "Email is required!"
                    txtLayoutRegisterEmail.isErrorEnabled = true
                }
                if(password.isEmpty()){
                    txtLayoutRegisterPassword.error = "Password is required!"
                    txtLayoutRegisterPassword.isErrorEnabled = true
                }
                if(cfPassword.isEmpty()){
                    txtLayoutRegisterCfPassword.error = "Password confirmation is required!"
                    txtLayoutRegisterCfPassword.isErrorEnabled = true
                }
            } else if(!isEmailValid(email)){
                txtLayoutRegisterEmail.error = "Email is not valid!"
                txtLayoutRegisterEmail.isErrorEnabled = true
            } else if(!isPasswordValid(password)){
                txtLayoutRegisterPassword.error = "Password must contains at least 8 characters long, contain at least one letter, at least one number, and at least one uppercase letter!"
                txtLayoutRegisterPassword.isErrorEnabled = true
            } else {
                if(cfPassword != password){
                    txtLayoutRegisterCfPassword.error = "Passwords dit not match!"
                    txtLayoutRegisterCfPassword.isErrorEnabled = true
                } else {
                    signup(nom,prenom,pseudo,email,password)
                }
            }

        }
    }

    private fun signup(nom:String, prenom:String, pseudo:String, email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val currentUser = auth.currentUser!!

                val user = hashMapOf(
                    "nom" to nom,
                    "prenom" to prenom,
                    "pseudo" to pseudo,
                    "email" to email
                )

                val db = Firebase.firestore
                db.collection("users")
                    .document(currentUser.uid)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this@RegisterActivity, "Account created successfully!", Toast.LENGTH_LONG).show()
                        Intent(this@RegisterActivity,AuthenticationActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@RegisterActivity, "Registration failed!", Toast.LENGTH_LONG).show()
                    }


            } else {
                Toast.makeText(this@RegisterActivity, "Registration failed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initErrors(){
        txtLayoutRegisterNom.isErrorEnabled = false
        txtLayoutRegisterPrenom.isErrorEnabled = false
        txtLayoutRegisterPseudo.isErrorEnabled = false
        txtLayoutRegisterEmail.isErrorEnabled = false
        txtLayoutRegisterPassword.isErrorEnabled = false
        txtLayoutRegisterCfPassword.isErrorEnabled = false
    }


    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // requires the password to be at least 8 characters long, contain at least one letter, at least one number, and at least one uppercase letter.
    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[A-Z])[A-Za-z\\d]{8,}$"
        return password.matches(passwordRegex.toRegex())
    }
}