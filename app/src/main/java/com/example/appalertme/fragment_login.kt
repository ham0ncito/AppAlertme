import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import com.example.appalertme.MainActivity
import com.example.appalertme.R
import com.example.appalertme.fragment_registrarse
import com.example.appalertme.pruebas
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class fragment_login : Fragment(R.layout.fragment_login) {
    companion object {
        const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val botonRegistrarse = view.findViewById<Button>(R.id.btnGoToRegister)
        val botonFacebook = view.findViewById<Button>(R.id.btnSignInFacebook)
        val botonIniciarSesion = view.findViewById<Button>(R.id.btnLogin)
        val botonGoogle = view.findViewById<Button>(R.id.btnLoginGoogle)
        val username = view.findViewById<EditText>(R.id.editTextUsername)
        val password = view.findViewById<EditText>(R.id.editTextPassword)
        val recuperar = view.findViewById<Button>(R.id.btnRecuperar)
        botonRegistrarse.setOnClickListener {
            botonRegistrarse.isEnabled= false
            requireActivity().supportFragmentManager.commit {
                replace(R.id.contenedorFragmentos, fragment_registrarse())
                addToBackStack(null)

            }
        }
        val textViewLink = view.findViewById<TextView>(R.id.PreguntasFrecuentes)
        textViewLink.setOnClickListener {
            val url = "https://www.unicah.edu"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        botonGoogle.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "En Mantenimiento mientras Google valida el app",
                Toast.LENGTH_SHORT
            ).show()

            val googleSignInOptions = GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.googleId))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        botonFacebook.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "En Mantenimiento mientras Facebook da credenciales",
                Toast.LENGTH_SHORT
            ).show()
        }
recuperar.setOnClickListener {
    requireActivity().supportFragmentManager.commit {
        replace(R.id.contenedorFragmentos, fragment_recuperar())
        addToBackStack(null)

    }
}
        botonIniciarSesion.setOnClickListener {
            botonIniciarSesion.isEnabled=false
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()
            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                botonIniciarSesion.isEnabled=true
                Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                signInWithEmailAndPassword(usernameText, passwordText, botonIniciarSesion)
            }
        }

    }

    private fun signInWithEmailAndPassword(email: String, password: String,boton : Button) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("email", email)
                    editor.apply()

                    startActivity(intent)
                } else {
                    boton.isEnabled = true
                    Toast.makeText(requireContext(), "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity()) { authTask ->
                        if (authTask.isSuccessful) {
                            val user = auth.currentUser
                            //autenticación exitosa
                        } else {
                            //  error de autenticación
                            Toast.makeText(requireContext(), "Error en el inicio de sesión de Google", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                // Manejar el error de autenticación de Google
                Toast.makeText(requireContext(), "Error Google: $e.message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
