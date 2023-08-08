package com.example.appalertme.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.math.log

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val texto = view?.findViewById<TextView>(R.id.textViewTitulo)
        val searchBox = view.findViewById<EditText>(R.id.editTextBuscar)

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable) {
                Log.d("Searchbox","SearchBox : " + p0.toString())
                val recyclerViewContactos = requireView().findViewById<RecyclerView>(R.id.recyclerViewContactos)
                val databaseReference = FirebaseDatabase.getInstance().reference
                val db = FirebaseFirestore.getInstance()

                val contactosCompletosList = mutableListOf<Contacto>()
                val usersReference = databaseReference.child("users")
                usersReference.orderByChild("nombre").equalTo(p0.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnapshot in snapshot.children){
                            Log.d("Usuario","Nombrein: "+p0.toString())
                            val nombre = userSnapshot.child("nombre").getValue(String::class.java)
                            Log.d("Nombre","Nombre: "+ nombre)
                            val correo = userSnapshot.child("correo").getValue(String::class.java)
                            val apellido = userSnapshot.child("apellido").getValue(String::class.java)
                            val nombreUsuario = userSnapshot.child("nombreUsuario").getValue(String::class.java)
                            val telefono = userSnapshot.child("telefono").getValue(String::class.java)
                            val contactoCompleto = Contacto("$nombre $apellido", "$telefono", "$correo", "$nombreUsuario")
                            contactosCompletosList.add(contactoCompleto)

                            // Check if all inner queries are completed

                            val adapter = RecyclerAdapterContactosCompletos(contactosCompletosList)
                            recyclerViewContactos.layoutManager = LinearLayoutManager(requireContext())
                            recyclerViewContactos.adapter = adapter

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        })

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
saveTokenToFirestore()
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
                intent.putExtra("email", email)
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
    @SuppressLint("RestrictedApi")
    private fun cargarYMostrarContactosAmigos(correoUsuario: String) {
        val recyclerViewContactos = requireView().findViewById<RecyclerView>(R.id.recyclerViewContactos)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val db = FirebaseFirestore.getInstance()
        val contactosReference = databaseReference.child("contactos")
        val query = contactosReference.orderByChild("remitente").equalTo(correoUsuario)

        val usersReference = databaseReference.child("users")
        Log.d("MyApp", "Correo Usuario : ${correoUsuario}")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contactosAmigosList = mutableListOf<String>()
                for (contactoSnapshot in snapshot.children) {
                    var contacto = contactoSnapshot.child("correo").getValue(String::class.java)
                    contacto?.let { contactosAmigosList.add(it) }
                    Log.d("Contacto","EMail: "+ contacto)
                }


                val contactosCompletosList = mutableListOf<Contacto>() // Counter for completed inner queries

                for (contactoAmigo in contactosAmigosList) {
                    var correoContacto = contactoAmigo
                    Log.d("CorreoContacto","Correo: "+correoContacto)

                    usersReference.orderByChild("correo").equalTo(correoContacto).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userSnapshot in snapshot.children){
                                Log.d("CorreoContacto","Correoin: "+correoContacto)
                                val nombre = userSnapshot.child("nombre").getValue(String::class.java)
                                Log.d("Nombre","Nombre: "+ nombre)
                                val apellido = userSnapshot.child("apellido").getValue(String::class.java)
                                val nombreUsuario = userSnapshot.child("nombreUsuario").getValue(String::class.java)
                                val telefono = userSnapshot.child("telefono").getValue(String::class.java)
                                val contactoCompleto = Contacto("$nombre $apellido", "$telefono", "$correoContacto", "$nombreUsuario")
                                contactosCompletosList.add(contactoCompleto)

                                // Check if all inner queries are completed

                                val adapter = RecyclerAdapterContactosCompletos(contactosCompletosList)
                                recyclerViewContactos.layoutManager = LinearLayoutManager(requireContext())
                                recyclerViewContactos.adapter = adapter

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
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
    private fun saveTokenToFirestore() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val db = FirebaseFirestore.getInstance()
                val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val email = sharedPref.getString("email", "")
                val usuarioId = "$email"
                val usuarioRef = db.collection("tokens").document(usuarioId)

                usuarioRef.get().addOnCompleteListener { userTask ->
                    if (userTask.isSuccessful) {
                        val document = userTask.result
                        if (document != null && document.exists()) {
                            val existingToken = document.getString("token")
                            if (existingToken != token) {
                                usuarioRef.update("token", token)
                                    .addOnSuccessListener {
                                    }
                                    .addOnFailureListener {
                                    }
                            }
                        } else {
                            val nuevoUsuario = hashMapOf(
                                "token" to token
                            )
                            usuarioRef.set(nuevoUsuario)
                                .addOnSuccessListener {

                                }
                                .addOnFailureListener {
                                }
                        }
                    }
                }
            }
        }
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
