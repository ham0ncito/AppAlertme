package com.example.appalertme

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class fragment_registrarse : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_registrarse, container, false)
        return rootView
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var email = view.findViewById<EditText>(R.id.editTextEmail)
        var contrasena = view.findViewById<EditText>(R.id.editPassword)
        var vercontrasena = view.findViewById<EditText>(R.id.editPassword2)
        var nombreUsuario = view.findViewById<EditText>(R.id.editTextUsername)
        var telefono = view.findViewById<EditText>(R.id.editTextPhone)
        var nombre = view.findViewById<EditText>(R.id.editTextFirstName)
        var apellido = view.findViewById<EditText>(R.id.editTextLastName)
        var fecha = view.findViewById<EditText>(R.id.editTextBirthDate)
        var texto = view.findViewById<TextView>(R.id.texto)
        val registrarse = view.findViewById<Button>(R.id.btnRegister)

        registrarse.setOnClickListener {
            val emailText = email?.editableText?.toString() ?: ""
            val contrasenaText = contrasena?.editableText?.toString() ?: ""
            val nombreUsuarioText = nombreUsuario?.editableText?.toString() ?: ""
            val telefonoText = telefono?.editableText?.toString() ?: ""
            val nombreText = nombre?.editableText?.toString() ?: ""
            val apellidoText = apellido?.editableText?.toString() ?: ""
            val fechaText = fecha?.editableText?.toString() ?: ""
            val vercontrasenaText = vercontrasena?.editableText?.toString() ?: ""
            validateAndSignUp(emailText, contrasenaText,vercontrasenaText, nombreText, telefonoText, apellidoText, fechaText, nombreUsuarioText)
        }
    }
    private fun isValidName(name: String): Boolean {
        val pattern = Regex(".*(.)\\1\\1.*")
        return !pattern.matches(name)

    }

    private fun isStrongPassword(password: String): Boolean {
        val pattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        return pattern.matches(password)
    }

    private fun areFieldsNotEmpty(vararg fields: String): Boolean {
        return fields.all { it.isNotEmpty() }
    }
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = Regex("^[2-9]\\d{7}$")
        return pattern.matches(phoneNumber)
    }
    private fun isValidEmail(email: String): Boolean {
        val pattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        return pattern.matches(email)
    }
    private fun validateAndSignUp(
        email: String,
        password: String,
        confirmPassword: String,
        nombre: String,
        telefono: String,
        apellido: String,
        fecha: String,
        nombreUsuario: String
    ) {
        if (!areFieldsNotEmpty(email,password, confirmPassword,nombre,telefono,apellido,fecha,nombreUsuario
            )) {
            Toast.makeText(requireContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Por favor ingresa un correo electrónico válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidName(nombre) || !isValidName(apellido)) {
            Toast.makeText(
                requireContext(),
                "Nombre y apellido no deben tener caracteres repetidos de manera consecutiva.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!isValidPhoneNumber(telefono)) {
            Toast.makeText(
                requireContext(),
                "El número de teléfono debe tener 8 dígitos y empezar con 2, 3, 8 o 9.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!isStrongPassword(password)) {
            Toast.makeText(
                requireContext(),
                "La contraseña debe tener al menos 8 caracteres, incluyendo al menos una mayúscula, una minúscula, un número y un caracter especial",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

         signUp(email, password, nombre, telefono, apellido, fecha, nombreUsuario)
    }


    private fun signUp(email: String, password: String, nombre: String, telefono: String, apellido: String, fecha: String, nombreUsuario: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { emailTask ->
                if (emailTask.isSuccessful) {
                    val signInMethods = emailTask.result?.signInMethods ?: emptyList()
                    if (signInMethods.isNotEmpty()) {
                        Toast.makeText(requireContext(), "Correo electrónico $email ya está en uso", Toast.LENGTH_SHORT).show()
                    } else {
                        val usuarioRef = usersRef.orderByChild("nombreUsuario").equalTo(nombreUsuario)
                        usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(requireContext(), "Nombre de Usuario $nombreUsuario ya existe. Prueba Otro", Toast.LENGTH_SHORT).show()
                                } else {
                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val userId = task.result?.user?.uid
                                                userId?.let {
                                                    val userMap = hashMapOf(
                                                        "nombre" to nombre,
                                                        "apellido" to apellido,
                                                        "telefono" to telefono,
                                                        "nombreUsuario" to nombreUsuario,
                                                        "correo" to email,
                                                        "fechaNacimiento" to fecha
                                                    )
                                                    usersRef.child(userId).setValue(userMap)
                                                        .addOnCompleteListener { userCreationTask ->
                                                            if (userCreationTask.isSuccessful) {
                                                                Toast.makeText(requireContext(), "$nombreUsuario gracias por registrarte", Toast.LENGTH_SHORT).show()
                                                                requireActivity().supportFragmentManager.popBackStack()
                                                            } else {
                                                                Toast.makeText(requireContext(), "Error de registro", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                }
                                            } else {
                                                // Manejar error de autenticación aquí
                                                Toast.makeText(requireContext(), "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                                Toast.makeText(requireContext(), "Error de base de datos", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {

                    Toast.makeText(requireContext(), "Error de autenticación: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


}
