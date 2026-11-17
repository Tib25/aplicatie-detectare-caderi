package com.example.falldetectionapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

//Defineste activitatea pentru ecranul de pornire.
class SplashActivity : AppCompatActivity() {

    //Functie executata la crearea Splash Screen-ului.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Incarca designul ecranului de pornire.
        setContentView(R.layout.activity_splash)

        //Asteapta 2 secunde inainte de a trece la ecranul principal.
        Handler(Looper.getMainLooper()).postDelayed({

            //Pregateste deschiderea ecranului principal.
            val intent = Intent(this, MainActivity::class.java)

            //Deschide ecranul principal.
            startActivity(intent)

            //Inchide Splash Screen-ul.
            finish()

        }, 2000)
    }
}