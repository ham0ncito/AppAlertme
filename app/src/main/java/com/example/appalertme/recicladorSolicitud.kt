package com.example.appalertme

import android.content.Context
import android.view.LayoutInflater
import android.view.PixelCopy.Request
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.NonDisposableHandle.parent

class recicladorSolicitud(private val contactRequests: List<SolicitudContacto>) : RecyclerView.Adapter<recicladorSolicitud.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contactoplantilla, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = contactRequests[position]
        val sharedPref = holder.itemView.context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        holder.bind(request)
        val acceptButton = holder.itemView.findViewById<Button>(R.id.acceptButtonSoli)
        val rejectButton = holder.itemView.findViewById<Button>(R.id.deleteButtonSoli)
        val receptor = email
        val remitente = request.remitente
        acceptButton.setOnClickListener {
            val databaseReference = FirebaseDatabase.getInstance().reference
            val query = databaseReference.child("contact_requests")
                .orderByChild("correo")
                .equalTo(receptor)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val request = childSnapshot.getValue(SolicitudContacto::class.java)
                        if (request != null && request.remitente == remitente) {
                            val requestId = request.id
                            val contactosReference = databaseReference.child("contactos").push()
                            val contactosId = contactosReference.key

                            val contactosData = hashMapOf(
                                "remitente" to request.remitente,
                                "correo" to request.correo
                            )

                            contactosReference.setValue(contactosData)
                                .addOnSuccessListener {
                                    Toast.makeText(holder.itemView.context, "Error $it", Toast.LENGTH_SHORT).show()

                                    childSnapshot.ref.removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(holder.itemView.context, "Error $it", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(holder.itemView.context, "Error $it", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(holder.itemView.context, "Error $it", Toast.LENGTH_SHORT).show()
                                }
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error en la consulta
                }
            })
        }
        rejectButton.setOnClickListener {

            val databaseReference = FirebaseDatabase.getInstance().reference
            val query = databaseReference.child("contact_requests")
                .orderByChild("correo")
                .equalTo(receptor)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val request = childSnapshot.getValue(SolicitudContacto::class.java)
                        if (request != null && request.remitente == remitente) {
                            val requestId = request.id
                            val subnodoPath = "contact_requests/$requestId"
                            databaseReference.child(subnodoPath).removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(holder.itemView.context, "Solicitudad Eliminada. Espera unos minutos", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(holder.itemView.context, "Error $it", Toast.LENGTH_SHORT).show()
                                }
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                   Toast.makeText(holder.itemView.context, "Error $error", Toast.LENGTH_SHORT).show()
                }
            })

        }





    }

    override fun getItemCount(): Int {
        return contactRequests.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(request: SolicitudContacto) {
            itemView.findViewById<TextView>(R.id.usernameTextContacto).text = request.remitente


        }
    }
}
