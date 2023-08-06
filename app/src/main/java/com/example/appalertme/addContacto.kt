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
        editTextName = findViewById(R.id.contactoEditTextName)
        editTextPhone = findViewById(R.id.contactoEditTextPhone)
        editTextEmail = findViewById(R.id.contactoEditTextEmail)
        btnSendRequest = findViewById(R.id.btnSendRequest)

        btnSendRequest.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val name = editTextName.text.toString()
            val phone = editTextPhone.text.toString()
            val email = editTextEmail.text.toString()

            if (username.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()) {
                if (correoUsuario != null) {
                    checkUserExists(username, name, phone, email, correoUsuario)
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserExists(username: String, email: String, name: String, phone: String, CorreoUsuario: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference

        val query = databaseReference.child("users")
            .orderByChild("nombreUsuario")
            .equalTo(username)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Nombre de usuario encontrado
                    Toast.makeText(this@AddContactoActivity, "Usuario encontrado", Toast.LENGTH_SHORT).show()
                    for (userSnapshot in snapshot.children) {
                        val userEmail = userSnapshot.child("correo").getValue(String::class.java)
                        if (userEmail == email) {
                            // Correo electrónico encontrado
                            Toast.makeText(this@AddContactoActivity, "Correo electrónico encontrado", Toast.LENGTH_SHORT).show()
                            sendContactRequest(username, name, phone, email, CorreoUsuario)
                        } else {
                            // Correo electrónico no encontrado
                            Toast.makeText(this@AddContactoActivity, "Correo electrónico no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Nombre de usuario no encontrado
                    Toast.makeText(this@AddContactoActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error de base de datos
            }
        })
    }


    private fun sendContactRequest(username: String, name: String, phone: String, email: String, CorreoUsuario: String) {
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().reference
        val request = Solicitud(CorreoUsuario, email, "pendiente")
        val requestId = databaseReference.child("users").child(email).child("contact_requests").push().key
        if (requestId != null) {
            databaseReference.child("users").child(email).child("contact_requests").child(requestId).setValue(request)
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
