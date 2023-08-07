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

class AddContactoActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var btnSendRequest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacto)
        val correoUsuario = intent.getStringExtra("email")
        editTextUsername = findViewById(R.id.contactoEditTextUsername)
        editTextEmail = findViewById(R.id.contactoEditTextEmail)
        btnSendRequest = findViewById(R.id.btnSendRequest)
        btnSendRequest.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString()

            if (username.isNotEmpty()  && email.isNotEmpty()) {
                if (correoUsuario != null) {
                    checkUserAndEmailExists(username, email,correoUsuario)
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserAndEmailExists(username: String, email: String, CorreoUsuario: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val query = databaseReference.child("users")
            .orderByChild("nombreUsuario")
            .equalTo(username)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@AddContactoActivity, "Usuario encontrado", Toast.LENGTH_SHORT).show()
                    var userEmailFound = false
                    for (userSnapshot in snapshot.children) {
                        val userEmail = userSnapshot.child("correo").getValue(String::class.java)
                        if (userEmail == email) {

                            userEmailFound = true

                        }
                    }

                    if (userEmailFound) {

                        Toast.makeText(this@AddContactoActivity, "Nombre de usuario y correo electrónico encontrados", Toast.LENGTH_SHORT).show()
                        sendContactRequest(username, email, CorreoUsuario)
                    } else {

                        Toast.makeText(this@AddContactoActivity, "Correo electrónico no encontrado para el usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {

                    Toast.makeText(this@AddContactoActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendContactRequest(username: String, email: String, CorreoUsuario: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val requestId = databaseReference.child("contact_requests").push().key
        val request = SolicitudContacto(CorreoUsuario, username, "pendiente",email,requestId.toString())
        if (requestId != null) {
            databaseReference.child("contact_requests").child(requestId).setValue(request)
                .addOnSuccessListener {
                    Toast.makeText(this@AddContactoActivity, "Solicitud de contacto enviada", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@AddContactoActivity, "Error al enviar la solicitud de contacto", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this@AddContactoActivity, "Error al generar la clave de solicitud", Toast.LENGTH_SHORT).show()
        }
    }
}
