package com.example.appalertme.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appalertme.R
import com.example.appalertme.Usuario
import com.example.appalertme.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val texto = view.findViewById<TextView>(R.id.textViewTitulo)
        texto.text = "Bienvenido $email"
        val roundedImageView = view.findViewById<ImageView>(R.id.roundedImageView)
        roundedImageView.setOnClickListener {
            val intent = Intent(requireContext(), Usuario::class.java)
            startActivity(intent)
    }}
}