package com.example.appalertme

data class Solicitud(
    var remitente: String = "",
    var receptor: String = "",
    var estado: String = "",
    val correo: String=""
)
