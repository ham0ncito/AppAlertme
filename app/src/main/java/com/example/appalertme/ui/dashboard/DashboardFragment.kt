package com.example.appalertme.ui.dashboard

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appalertme.Contacto
import com.example.appalertme.R
import com.example.appalertme.databinding.FragmentDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val linearButtonLayout = layoutInflater.inflate(R.layout.linear_button, null) as LinearLayout

        val button1 = linearButtonLayout.findViewById<Button>(R.id.botonalert1)
        val button3 = linearButtonLayout.findViewById<Button>(R.id.botonalert3)
        val button4 = linearButtonLayout.findViewById<Button>(R.id.botonalert4)
        val button2 = linearButtonLayout.findViewById<Button>(R.id.botonalert2)
        val buttons = listOf(button1, button2, button3, button4)
        button1.setOnClickListener {
            if (button1.isEnabled) {
                disableButtonsFor15Minutes(requireContext(),"EMERGENCIA", "HUBO UN PROBLEMA, LLAMENME", buttons)
            }
        }

        button2.setOnClickListener {
            if (button3.isEnabled) {
                disableButtonsFor15Minutes(requireContext(),"TODO BIEN", "ME LA ESTOY PASANDO BOMBA", buttons)
            }
        }
        button3.setOnClickListener {
            if (button3.isEnabled) {
                disableButtonsFor15Minutes(requireContext(),"AYUDA", "CONTACTENSE CONMIGO", buttons)
            }
        }
        button4.setOnClickListener {
            if (button4.isEnabled) {
                disableButtonsFor15Minutes(requireContext(),"TODO BIEN", "NADA MALO HA PASADO",buttons)
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun disableButtonsFor15Minutes(contexto: Context, titulo: String, mensaje: String, buttons: List<Button>) {
        for (button in buttons) {
            button.isEnabled = false
        }
        Handler().postDelayed({
            sendLocalNotification(requireContext(), "ESTOY BIEN", "TODO BIEN GRACIAS A DIOS")
            for (button in buttons) {
                button.isEnabled = true
            }
        }, 15 * 60 * 1000) // 15 minutos en milisegundos
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendLocalNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val channelDescription = "Description for My Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "1")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)

        notificationManager.notify( 1, builder.build())
    }
}