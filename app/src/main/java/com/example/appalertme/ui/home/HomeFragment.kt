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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appalertme.AddContactoActivity
import com.example.appalertme.Contacto
import com.example.appalertme.R
import com.example.appalertme.RecyclerAdapterContactosCompletos
import com.example.appalertme.SolicitudContacto
import com.example.appalertme.Usuario
import com.example.appalertme.eliminarContacto
import com.example.appalertme.recicladorSolicitud
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

        if (email != null) {
            solicitudesActivas(email)
        }
        if (email != null) {
            cargarYMostrarContactosAmigos(email)
        }
        val databaseReference = FirebaseDatabase.getInstance().reference
        val query = databaseReference.child("users").orderByChild("correo").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val nombre = userSnapshot.child("nombre").getValue(String::class.java)
                        val apellido = userSnapshot.child("apellido").getValue(String::class.java)
                        val correo = userSnapshot.child("correo").getValue(String::class.java)
                        val fechaNacimiento = userSnapshot.child("fechaNacimiento").getValue(String::class.java)
                   val nombreUsuario = userSnapshot.child("nombreUsuario").getValue(String::class.java)
                   val telefono = userSnapshot.child("telefono").getValue(String::class.java)
                        val editor = sharedPref.edit()

                        if (nombre != null && !nombre.isEmpty()) {
                            editor.putString("nombre", nombre)
                        }

                        if (apellido != null && !apellido.isEmpty()) {
                            editor.putString("apellido", apellido)
                        }

                        if (correo != null && !correo.isEmpty()) {
                            editor.putString("correo", correo)
                        }

                        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                            editor.putString("fechaNacimiento", fechaNacimiento)
                        }

                        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                            editor.putString("nombreUsuario", nombreUsuario)
                        }

                        if (telefono != null && !telefono.isEmpty()) {
                            editor.putString("telefono", telefono)
                        }
                        if (texto != null) {
                            texto.text = "Bievenido\n$nombre\n$apellido"
                        }
                        editor.apply()
                    }
                } else {
                    // El correo electrónico no fue encontrado en la base de datos
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error de base de datos
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
    private fun cargarYMostrarContactosAmigos(correoUsuario: String) {
        val recyclerViewContactos = view?.findViewById<RecyclerView>(R.id.recyclerViewContactos)
        val databaseReference = FirebaseDatabase.getInstance().reference
        // Primero, consulta los contactos en la base de datos 'contactos' donde el remitente es el correo del usuario
        val contactosReference = databaseReference.child("contactos")
        val query = contactosReference.orderByChild("remitente").equalTo(correoUsuario)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contactosAmigosList = mutableListOf<Contacto>()
                for (contactoSnapshot in snapshot.children) {
                    val contacto = contactoSnapshot.getValue(Contacto::class.java)
                    if (contacto != null) {
                        contactosAmigosList.add(contacto)
                    }
                }

                val usersReference = databaseReference.child("users")
                val contactosCompletosList = mutableListOf<Contacto>()
                for (contactoAmigo in contactosAmigosList) {
                    val correoContacto = contactoAmigo.email// Utiliza el campo de correo del contacto

                    usersReference.child(correoContacto).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nombre = snapshot.child("nombre").getValue(String::class.java)
                            val apellido = snapshot.child("apellido").getValue(String::class.java)
                            val nombreUsuario = snapshot.child("nombreUsuario").getValue(String::class.java)
                            val telefono = snapshot.child("telefono").getValue(String::class.java)
                            val contactoCompleto = Contacto(
                                "$nombre $apellido",
                                "$telefono",
                                "$correoContacto",
                                "$nombreUsuario"
                            )
                            contactosCompletosList.add(contactoCompleto)
                            val adapter = RecyclerAdapterContactosCompletos(contactosCompletosList)
                            recyclerViewContactos?.layoutManager = LinearLayoutManager(requireContext())
                            recyclerViewContactos?.adapter = adapter
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Manejar error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }


    private fun displayContactRequests(usernames: List<String>) {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerViewSolicitus)
        val layoutManager = LinearLayoutManager(requireContext())
        val contactRequests = usernames.map { SolicitudContacto(it) }
        val adapter = recicladorSolicitud(contactRequests)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun solicitudesActivas(userEmail: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val usernames = mutableListOf<String>()
        val query = databaseReference.child("contact_requests")
            .orderByChild("correo")
            .equalTo(userEmail)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val username = userSnapshot.child("remitente").getValue(String::class.java)
                    if (username != null) {
                        usernames.add(username)
                    }
                }
                displayContactRequests(usernames)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error en la consulta de solicitudes", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
