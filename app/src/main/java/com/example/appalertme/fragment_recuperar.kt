import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appalertme.R
import com.google.firebase.auth.FirebaseAuth

class fragment_recuperar : Fragment(R.layout.fragment_recuperar) {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recuperar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        val enviarCorreoButton = view.findViewById<Button>(R.id.btnEnviarCorreoRecuperar)
        enviarCorreoButton.setOnClickListener {
            enviarCorreo(enviarCorreoButton)
        }
    }

    private fun enviarCorreo(Boton: Button) {
        var correo = view?.findViewById<EditText>(R.id.editTextCorreo)?.text.toString()
        if (correo.isNotEmpty()) {
            Boton.isEnabled = false
            mAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Correo de recuperación enviado a: $correo", Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            requireActivity().supportFragmentManager.popBackStack()
                        }, 2000)
                    } else {
                        Boton.isEnabled = true
                        Toast.makeText(requireContext(), "Error al enviar el correo de recuperación", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
        }
    }
}
