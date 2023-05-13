package com.ivan.work.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var txtLayoutEmail: TextInputLayout
    private lateinit var txtLayoutPassword : TextInputLayout
    private lateinit var btnConnect: MaterialButton
    private lateinit var txtCreateAccount: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        supportActionBar?.hide()

        initialize()

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    private fun initialize(){
        txtLayoutEmail = findViewById(R.id.txt_layout_email)
        txtLayoutPassword = findViewById(R.id.txt_layout_password)
        btnConnect = findViewById(R.id.btn_connect)
        txtCreateAccount = findViewById(R.id.txt_create_account)
    }

    override fun onStart() {
        super.onStart()

        txtCreateAccount.setOnClickListener {
            Intent(this@AuthenticationActivity,RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

        btnConnect.setOnClickListener {
            val email = txtLayoutEmail.editText?.text.toString()
            val password = txtLayoutPassword.editText?.text.toString()

            initErrors()

            if(email.isEmpty() || password.isEmpty()){
                if(email.isEmpty()){
                    txtLayoutEmail.error = "Email is required!"
                    txtLayoutEmail.isErrorEnabled = true
                }
                if(password.isEmpty()){
                    txtLayoutPassword.error = "Password is required!"
                    txtLayoutPassword.isErrorEnabled = true
                }
            } else{
                signin(email,password)
            }

        }

    }

    private fun signin(email:String, password:String){
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                Intent(this@AuthenticationActivity,HomeActivity::class.java).also {
                    startActivity(it)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this@AuthenticationActivity, "Authentication failed!",Toast.LENGTH_LONG).show()
            }
    }

    private fun initErrors(){
        txtLayoutEmail.isErrorEnabled = false
        txtLayoutPassword.isErrorEnabled = false
    }




}