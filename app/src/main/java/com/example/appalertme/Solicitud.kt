package com.example.appalertme

data class SolicitudContacto(
    var remitente: String = "",
    var receptor: String = "",
    var estado: String = "",
    val correo: String=""
)
