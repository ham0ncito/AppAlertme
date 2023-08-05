import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
        const val RC_SIGN_IN = 200
    }

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val botonRegistrarse = view.findViewById<Button>(R.id.btnGoToRegister)
        val botonIniciarSesion = view.findViewById<Button>(R.id.btnLogin)
        val botonGoogle = view.findViewById<Button>(R.id.btnLoginGoogle)
        val username = view.findViewById<EditText>(R.id.editTextUsername)
        val password = view.findViewById<EditText>(R.id.editTextPassword)

        botonRegistrarse.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.contenedorFragmentos, fragment_registrarse())
                addToBackStack(null)

            }
        }

        botonGoogle.setOnClickListener {
            val googleSignInOptions = GoogleSignInOptions.Builder()
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
            val signInIntent = googleSignInClient.signInIntent

            signInIntent.let {
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }

        botonIniciarSesion.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()
            signInWithEmailAndPassword(usernameText, passwordText)
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.putExtra("clave", email)
                    startActivity(intent)
                } else {
                    // Handle authentication error
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
                            // Manejar la autenticación exitosa con Google (por ejemplo, navegación, mostrar mensajes, etc.)
                        } else {
                            // Manejar el error de autenticación con Google
                            Toast.makeText(requireContext(), "Error en el inicio de sesión de Google", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                // Manejar el error de autenticación de Google
                Toast.makeText(requireContext(), "Error en el inicio de sesión de Google", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
