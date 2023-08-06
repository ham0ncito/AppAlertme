package com.example.appalertme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class addContacto : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var btnSendRequest: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacto)

            setContentView(R.layout.activity_add_contacto)
            editTextUsername = findViewById(R.id.contactoEditTextUsername)
            editTextName = findViewById(R.id.contactoEditTextName)
            editTextPhone = findViewById(R.id.contactoEditTextPhone)
            editTextEmail = findViewById(R.id.contactoEditTextEmail)
            btnSendRequest = findViewById(R.id.btnSendRequest)

            btnSendRequest.setOnClickListener {
                val username = editTextUsername.text.toString()
                val name = editTextName.text.toString()
                val phone = editTextPhone.text.toString()
                val email = editTextEmail.text.toString()
                checkUsernameExists(username, name, phone, email)
            }
        }

        private fun checkUsernameExists(username: String, name: String, phone: String, email: String) {

            val databaseReference = FirebaseDatabase.getInstance().reference
            val query = databaseReference.child("users").orderByChild("NombreUsuario").equalTo(username)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        Toast.makeText(parent, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                    } else {
                        // El nombre de usuario no existe, puedes enviar la solicitud de contacto.
                        sendContactRequest(username, name, phone, email)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error de base de datos
                }
            })
        }

        private fun sendContactRequest(username: String, name: String, phone: String, email: String) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val databaseReference = FirebaseDatabase.getInstance().reference
            val uniqueId = UUID.randomUUID().toString()
            val newContact = Contact(name, phone, email, "", username)

            val contactId =
                userId?.let { databaseReference.child("users").child(it).child("contacts").push().key }
            if (contactId != null) {
                databaseReference.child("users").child(userId).child("contacts").child(contactId).setValue(newContact)
                    .addOnSuccessListener {
                        // Solicitud de contacto enviada exitosamente
                        Toast.makeText(this, "Solicitud de contacto enviada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        // Manejar error al enviar la solicitud de contacto
                    }
            }
        }
    }

