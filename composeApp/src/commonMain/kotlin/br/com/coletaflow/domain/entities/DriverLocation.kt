package br.com.coletaflow.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class DriverLocation(
    val lat: Double,
    val lng: Double,
    val speed: Double? = null,
    val heading: Int? = null,
    val accuracy: Double? = null,
    val battery: Int? = null,
    val timestamp: String,
)
