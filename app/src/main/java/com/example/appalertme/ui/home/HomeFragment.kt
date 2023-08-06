package com.example.appalertme.ui.home

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
        val roundedImageView = view.findViewById<ImageView>(R.id.roundedImageView)
        roundedImageView.setOnClickListener {
            val intent = Intent(requireContext(), Usuario::class.java)
            startActivity(intent)
    }}
}