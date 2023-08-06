package com.example.appalertme.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appalertme.AddContactoActivity
import com.example.appalertme.R
import com.example.appalertme.Usuario
import com.example.appalertme.eliminarContacto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val texto = view?.findViewById<TextView>(R.id.textViewTitulo)

        val databaseReference = FirebaseDatabase.getInstance().reference
        val query = databaseReference.child("users").orderByChild("correo").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val nombre = userSnapshot.child("nombre").getValue(String::class.java)
                        val correo = userSnapshot.child("correo").getValue(String::class.java)
                        val apellido = userSnapshot.child("apellido").getValue(String::class.java)
                        val user = userSnapshot.child("nombreUsuario").getValue(String::class.java)
                        val fecha = userSnapshot.child("fechaNacimiento").getValue(String::class.java)
                        val telefono  = userSnapshot.child("telefono").getValue(String::class.java)
                        if (texto != null) {
                            texto.text = "Bienvenido \n$nombre \n$apellido"
                        }
                        val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()

                        val correoExistente = sharedPref.getString("email", null)
                        val nombreExistente = sharedPref.getString("nombre", null)
                        val apellidoExistente = sharedPref.getString("apellido", null)
                        val usuarioExistente = sharedPref.getString("usuario", null)
                        val fechaExistente = sharedPref.getString("fecha", null)
                        val telefonoExistente = sharedPref.getString("telefono", null)

                        if (correoExistente.isNullOrEmpty()) {
                            editor.putString("email", correo)
                        }
                        if (nombreExistente.isNullOrEmpty()) {
                            editor.putString("nombre", nombre)
                        }
                        if (apellidoExistente.isNullOrEmpty()) {
                            editor.putString("apellido", apellido)
                        }
                        if (usuarioExistente.isNullOrEmpty()) {
                            editor.putString("usuario", user)
                        }
                        if (fechaExistente.isNullOrEmpty()) {
                            editor.putString("fecha", fecha)
                        }
                        if (telefonoExistente.isNullOrEmpty()) {
                            editor.putString("telefono", telefono)
                        }
                        editor.apply()

                    }
                } else {
                    Toast.makeText(requireContext(), "Error de búsqueda", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(requireContext(), "Error $error", Toast.LENGTH_SHORT).show()
            }
        })


        val aggContacto = view?.findViewById<Button>(R.id.btnAgregarContacto)
        val eliContacto = view?.findViewById<Button>(R.id.btnEliminarContacto)

        if (aggContacto != null) {
            aggContacto.setOnClickListener{
                val intent = Intent(requireContext(), AddContactoActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        }

        if (eliContacto != null) {
            eliContacto.setOnClickListener{
                val intent = Intent(requireContext(), eliminarContacto::class.java)
                startActivity(intent)
            }
        }
        val roundedImageView = view?.findViewById<ImageView>(R.id.roundedImageView)
        if (roundedImageView != null) {
            roundedImageView.setOnClickListener {
                val intent = Intent(requireContext(), Usuario::class.java)
                startActivity(intent)
            }
        }

    }
}

