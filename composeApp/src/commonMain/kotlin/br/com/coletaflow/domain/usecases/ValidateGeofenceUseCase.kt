package br.com.coletaflow.domain.usecases

import kotlin.math.*

sealed class GeofenceResult {
    data class WithinRange(val distanceMeters: Double) : GeofenceResult()
    data class OutOfRange(val distanceMeters: Double, val requiredRadius: Double) : GeofenceResult()
}

class ValidateGeofenceUseCase(
    private val radiusMeters: Double = 100.0,
) {
    operator fun invoke(
        currentLat: Double,
        currentLng: Double,
        pointLat: Double,
        pointLng: Double,
    ): GeofenceResult {
        val distance = haversineMeters(currentLat, currentLng, pointLat, pointLng)
        return if (distance <= radiusMeters) {
            GeofenceResult.WithinRange(distance)
        } else {
            GeofenceResult.OutOfRange(distance, radiusMeters)
        }
    }

    private fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6_371_000.0
        val dLat = toRad(lat2 - lat1)
        val dLng = toRad(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(toRad(lat1)) * cos(toRad(lat2)) * sin(dLng / 2).pow(2)
        return 2 * r * asin(sqrt(a))
    }

    private fun toRad(deg: Double) = deg * PI / 180
}
