package com.example.appalertme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class fragment_registrarse : Fragment(R.layout.fragment_registrarse) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = view.findViewById<EditText>(R.id.editTextEmail)
        val contrasena = view.findViewById<EditText>(R.id.editTextPassword)
        val editGenero = view.findViewById<AutoCompleteTextView>(R.id.editTextGender)
        val opciones = listOf("Hombre", "Mujer", "Otro")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, opciones)
        editGenero.setAdapter(adapter)

        val nombreUsuario = view.findViewById<EditText>(R.id.editTextUsername)
        val telefono = view.findViewById<EditText>(R.id.editTextPhone)
        val nombre = view.findViewById<EditText>(R.id.editTextFirstName)
        val apellido = view.findViewById<EditText>(R.id.editTextLastName)
val registrarse = view.findViewById<Button>(R.id.btnRegister)
        editGenero.setOnItemClickListener { _, _, position, _ ->
            val opcionSeleccionada = opciones[position]
            // If you intend to use opcionSeleccionada, make sure you use it in your code
        }

        registrarse.setOnClickListener {
            signUp(email.text.toString(), contrasena.text.toString())
        }
    }

    // ... Rest of your code ...

    private fun signUp(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                } else {

                }
            }
    }
}
