package com.example.appalertme

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appalertme.ui.home.HomeFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class eliminarContacto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eliminar_contacto)

        //obtenemos el correo
        val currentUserEmail= intent.getStringExtra("email")

        cargarYMostrarContactosAmigos(currentUserEmail.toString())
    }


    @SuppressLint("RestrictedApi")
    public fun cargarYMostrarContactosAmigos(correoUsuario: String) {
        val recyclerViewContactos = findViewById<RecyclerView>(R.id.recyclerViewEliminarContactos)
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
                    Log.d("Contacto", "EMail: " + contacto)
                }

                val contactosCompletosList = mutableListOf<Contacto>()

                for (contactoAmigo in contactosAmigosList) {
                    var correoContacto = contactoAmigo
                    Log.d("CorreoContacto", "Correo: " + correoContacto)

                    usersReference.orderByChild("correo").equalTo(correoContacto)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (userSnapshot in snapshot.children) {
                                    Log.d("CorreoContacto", "Correoin: " + correoContacto)
                                    val nombre =
                                        userSnapshot.child("nombre").getValue(String::class.java)
                                    Log.d("Nombre", "Nombre: " + nombre)
                                    val apellido =
                                        userSnapshot.child("apellido").getValue(String::class.java)
                                    val nombreUsuario = userSnapshot.child("nombreUsuario")
                                        .getValue(String::class.java)
                                    val telefono =
                                        userSnapshot.child("telefono").getValue(String::class.java)
                                    val contactoCompleto = Contacto(
                                        "$nombre $apellido",
                                        "$telefono",
                                        "$correoContacto",
                                        "$nombreUsuario"
                                    )
                                    contactosCompletosList.add(contactoCompleto)
                                }
                                val adapter =
                                    RecyclerAdapterContactosEliminarCompletos(contactosCompletosList, correoUsuario)
                                recyclerViewContactos.layoutManager =
                                    LinearLayoutManager(this@eliminarContacto)
                                recyclerViewContactos.adapter = adapter
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

}