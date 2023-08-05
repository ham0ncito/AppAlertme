package com.example.appalertme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import fragment_login

class Contenedor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contenedor)
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.contenedorFragmentos, fragment_login() )
            .commit()
    }

}