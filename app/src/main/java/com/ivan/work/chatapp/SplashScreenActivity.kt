package com.ivan.work.chatapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {

    /*user1@gmail.com Monkey-D Luffy chapeauDePaille user1User1234
    user2@gmail.com Vinsmoke Sanji sanji  user2User1234
    user3@gmail.com Roronoa Zorro Zorro user3User1234
    user4@gmail.com Trafalgar law law user4User1234*/

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        Handler.createAsync(Looper.getMainLooper()).postDelayed({
            val auth = Firebase.auth
            val currentUser = auth.currentUser

            if(currentUser != null) {
                Intent(this@SplashScreenActivity, HomeActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@SplashScreenActivity, AuthenticationActivity::class.java).also {
                    startActivity(it)
                }
            }

            finish()
        }, 3000)
    }

}