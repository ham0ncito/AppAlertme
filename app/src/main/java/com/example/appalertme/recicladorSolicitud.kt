package com.example.appalertme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recicladorSolicitud(private val contactRequests: List<SolicitudContacto>) : RecyclerView.Adapter<recicladorSolicitud.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contactoplantilla, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = contactRequests[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int {
        return contactRequests.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(request: SolicitudContacto) {
            itemView.findViewById<TextView>(R.id.usernameTextContacto).text = request.nombreUsuario
        }
    }
}
