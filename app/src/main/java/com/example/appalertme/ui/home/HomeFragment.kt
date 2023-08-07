package com.example.appalertme.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appalertme.AddContactoActivity
import com.example.appalertme.R
import com.example.appalertme.SolicitudContacto
import com.example.appalertme.Usuario
import com.example.appalertme.eliminarContacto
import com.example.appalertme.recicladorSolicitud
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val nombre = sharedPref.getString("nombre", "")
        val apellido = sharedPref.getString("apellido", "")
        val texto = view?.findViewById<TextView>(R.id.textViewTitulo)
        if (texto != null) {
            texto.text = "Bievenido\n$nombre\n$apellido"
        }
        if (email != null) {
            solicitudesActivas(email)
        }

        val aggContacto = view?.findViewById<Button>(R.id.btnAgregarContacto)
        val eliContacto = view?.findViewById<Button>(R.id.btnEliminarContacto)

        if (aggContacto != null) {
            aggContacto.setOnClickListener{
                val intent = Intent(requireContext(), AddContactoActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        }

        if (eliContacto != null) {
            eliContacto.setOnClickListener{
                val intent = Intent(requireContext(), eliminarContacto::class.java)
                startActivity(intent)
            }
        }
        val roundedImageView = view?.findViewById<ImageView>(R.id.roundedImageView)
        if (roundedImageView != null) {
            roundedImageView.setOnClickListener {
                val intent = Intent(requireContext(), Usuario::class.java)
                startActivity(intent)
            }
        }
    }

    private fun displayContactRequests(usernames: List<String>) {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerViewSolicitus)
        val layoutManager = LinearLayoutManager(requireContext())
        val contactRequests = usernames.map { SolicitudContacto(it) }
        val adapter = recicladorSolicitud(contactRequests)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun solicitudesActivas(userEmail: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val usernames = mutableListOf<String>()
        val query = databaseReference.child("contact_requests")
            .orderByChild("correo")
            .equalTo(userEmail)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val username = userSnapshot.child("remitente").getValue(String::class.java)
                    if (username != null) {
                        usernames.add(username)
                    }
                }
                displayContactRequests(usernames)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error en la consulta de solicitudes", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
