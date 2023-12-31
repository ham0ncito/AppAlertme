package com.example.appalertme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class Usuario : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
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
        val usuario = sharedPref.getString("nombreUsuario", "")
        val fecha = sharedPref.getString("fechaNacimiento", "")
        val telefono = sharedPref.getString("telefono", "")

        emailTextView.text = email
        nombreTextView.text = nombre
        telefonoTextView.text = telefono
        apellidoTextView.text = apellido
        fechaTextView.text = fecha
        nombreUsuarioTextView.text = usuario
        titulo.text="$nombre $apellido"
        val botonCambiarCorreo = findViewById<Button>(R.id.cambiarCorreo)
        botonCambiarCorreo.setOnClickListener {
            val intent = Intent(this, Correo::class.java)
            intent.putExtra("clave", email)
            startActivity(intent)
        }
val cerrarSesion = findViewById<Button>(R.id.cerrarSesion)
        cerrarSesion.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signOut()

            val delayMillis = 1000L
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@Usuario, Contenedor::class.java)
                val sharedPref = this@Usuario.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }, delayMillis)
        }
        val botonCambiarContra = findViewById<Button>(R.id.cambiarContraseña)
        botonCambiarContra.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            if (email != null) {
                mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Correo para cambiar la contraseña enviado a: $email", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al enviar el correo de recuperación", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }
}