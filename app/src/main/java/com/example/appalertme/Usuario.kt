package com.example.appalertme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.TextView

class Usuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)
        val emailTextView = findViewById<TextView>(R.id.activityDatosEmail)
        val nombreTextView = findViewById<TextView>(R.id.activityDatosNombre)
        val telefonoTextView = findViewById<TextView>(R.id.activityDatosTelefono)
        val apellidoTextView = findViewById<TextView>(R.id.activityDatosApellido)
        val fechaTextView = findViewById<TextView>(R.id.activityDatosFecha)
        val titulo = findViewById<TextView>(R.id.titleUsuario)
        val nombreUsuarioTextView = findViewById<TextView>(R.id.activityDatosNombreUsuario)

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val nombre = sharedPref.getString("nombre", "")
        val apellido = sharedPref.getString("apellido", "")
        val usuario = sharedPref.getString("usuario", "")
        val fecha = sharedPref.getString("fecha", "")
        val telefono = sharedPref.getString("telefono", "")

        emailTextView.text = email
        nombreTextView.text = nombre
        telefonoTextView.text = telefono
        apellidoTextView.text = apellido
        fechaTextView.text = fecha
        nombreUsuarioTextView.text = usuario
titulo.text="$nombre $apellido"


    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
}