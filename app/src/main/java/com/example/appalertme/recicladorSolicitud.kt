package com.example.appalertme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
            itemView.findViewById<TextView>(R.id.usernameTextContacto).text = request.remitente
            val remitente = request.remitente
            val acceptButton = itemView.findViewById<Button>(R.id.acceptButtonSoli)
            val rejectButton = itemView.findViewById<Button>(R.id.deleteButtonSoli)
            acceptButton.setOnClickListener {

            }
            rejectButton.setOnClickListener {
                Toast.makeText(itemView.context, "$remitente", Toast.LENGTH_SHORT).show()
                val receptor = request.correo
                Toast.makeText(itemView.context, "$receptor", Toast.LENGTH_SHORT).show()
                val databaseReference = FirebaseDatabase.getInstance().reference
               // databaseReference.child("contact_requests").child("$remitente-$receptor").removeValue()
                Toast.makeText(itemView.context, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
