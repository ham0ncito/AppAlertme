package com.example.appalertme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Correo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correo)
        val valor = intent.getStringExtra("clave")
        val texto = findViewById<TextView>(R.id.txtCorreoAnterior)
        texto.text = valor
        val editTextNewEmail = findViewById<EditText>(R.id.editTextNewEmail)
        val editTextPassword = findViewById <EditText>(R.id.editTextPassword)
        val btnChangeEmail = findViewById<Button>(R.id.btnChangeEmail)
        btnChangeEmail.setOnClickListener {
            val newEmail = editTextNewEmail.text.toString().trim()
            val password = editTextPassword.text.toString()

            val user = FirebaseAuth.getInstance().currentUser
            val credential = valor?.let { it1 -> EmailAuthProvider.getCredential(it1, password) }

            if (user != null && credential != null) {
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updateEmail(newEmail).addOnCompleteListener { updateEmailTask ->
                            if (updateEmailTask.isSuccessful) {
                                user.sendEmailVerification().addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        val databaseReference = FirebaseDatabase.getInstance().reference
                                        val emailUpdates = hashMapOf<String, Any>("correo" to newEmail)
                                        databaseReference.child("users").child(user.uid).updateChildren(emailUpdates)
                                            .addOnCompleteListener { databaseUpdateTask ->
                                                if (databaseUpdateTask.isSuccessful) {
                                                    Toast.makeText(this@Correo, "Cambio de Correo Exitoso", Toast.LENGTH_SHORT).show()
                                                    // Cerrar sesión y redirigir
                                                    val auth = FirebaseAuth.getInstance()
                                                    auth.signOut()
                                                    Toast.makeText(this@Correo, "Por tu seguridad, reinicia sesión", Toast.LENGTH_SHORT).show()
                                                    val delayMillis = 5000L
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        val intent = Intent(this@Correo, Contenedor::class.java)
                                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                                        startActivity(intent)
                                                        finish()
                                                    }, delayMillis)
                                                } else {
                                                    Toast.makeText(this@Correo, "Error durante la actualización en la base de datos", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(this@Correo, "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this@Correo, "Error durante la actualización del correo en Firebase Authentication", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@Correo, "Error de autenticación", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}