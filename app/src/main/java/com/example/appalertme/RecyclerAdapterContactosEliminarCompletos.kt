package com.example.appalertme

import android.app.Activity
import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecyclerAdapterContactosEliminarCompletos(private val contactosCompletosList: List<Contacto>, private val currentUserEmail: String) : RecyclerView.Adapter<RecyclerAdapterContactosEliminarCompletos.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contactoeliminar, parent, false)
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
        init {
            val btnEliminar = itemView.findViewById<Button>(R.id.btnEliminar)
            btnEliminar.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val contacto = contactosCompletosList[position]
                    val builder = AlertDialog.Builder(itemView.context)
                    builder.setTitle("Eliminar Contacto")
                        .setMessage("¿Estás seguro de que deseas eliminar a este contacto?")
                        .setPositiveButton("Eliminar") { _, _ ->
                            eliminarContactoFuncion(contacto)

                            //recargamos el activity
                            val intent = Intent(itemView.context, eliminarContacto::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            itemView.context.startActivity(intent)
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }
        }

        fun bind(contacto: Contacto) {
            val nombreApellidoTextView = itemView.findViewById<TextView>(R.id.nameText)
            val telefonoTextView = itemView.findViewById<TextView>(R.id.infoText)
            val correoTextView = itemView.findViewById<TextView>(R.id.moreInfoText)
            nombreApellidoTextView.text = contacto.name
            telefonoTextView.text = contacto.phone
            correoTextView.text = contacto.email
        }

        private fun eliminarContactoFuncion(contacto: Contacto) {

            if (currentUserEmail != null) {
                val databaseReference = FirebaseDatabase.getInstance().reference
                val contactosReference = databaseReference.child("contactos")

                val databaseReference2 = FirebaseDatabase.getInstance().reference
                val contactosReference2 = databaseReference2.child("contactos")

                // Consultar el nodo de contactos para encontrar el registro a eliminar del usuario actual
                contactosReference.orderByChild("remitente").equalTo(currentUserEmail).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (contactoSnapshot in snapshot.children) {
                            val correoContacto = contactoSnapshot.child("correo").getValue(String::class.java)
                            if (correoContacto == contacto.email.toString()) {
                                // Eliminar el registro correspondiente
                                contactoSnapshot.ref.removeValue()
                                Toast.makeText(itemView.context, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(itemView.context, "Error al eliminar el contacto: $error", Toast.LENGTH_SHORT).show()
                    }
                })

                contactosReference2.orderByChild("remitente").equalTo(contacto.email.toString()).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (contactoSnapshot in snapshot.children) {
                            val correoContacto = contactoSnapshot.child("correo").getValue(String::class.java)
                            if (correoContacto == currentUserEmail) {
                                // Eliminar el registro correspondiente
                                contactoSnapshot.ref.removeValue()
                                Toast.makeText(itemView.context, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(itemView.context, "Error al eliminar el contacto: $error", Toast.LENGTH_SHORT).show()
                    }
                })

                // Consultar el nodo de contactos para encontrar el registro a eliminar del otro usuario

            }
        }
    }

}
