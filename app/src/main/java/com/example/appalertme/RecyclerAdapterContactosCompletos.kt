package com.example.appalertme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapterContactosCompletos(private val contactosCompletosList: List<Contacto>) : RecyclerView.Adapter<RecyclerAdapterContactosCompletos.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacinto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contacto = contactosCompletosList[position]
        holder.bind(contacto)
    }

    override fun getItemCount(): Int {
        return contactosCompletosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(contacto: Contacto) {
            val nombreApellidoTextView = itemView.findViewById<TextView>(R.id.nameText)
            val telefonoTextView = itemView.findViewById<TextView>(R.id.infoText)
            val correoTextView = itemView.findViewById<TextView>(R.id.moreInfoText)
            nombreApellidoTextView.text = contacto.name
            telefonoTextView.text = contacto.phone
            correoTextView.text = contacto.email
        }
    }
}
