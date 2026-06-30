package br.com.coletaflow.data.local

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class PendingEvent {
    abstract val timestamp: String
    abstract val routeId: String

    @Serializable
    data class LocationSnapshot(
        override val routeId: String,
        val lat: Double,
        val lng: Double,
        override val timestamp: String,
    ) : PendingEvent()

    @Serializable
    data class RouteStarted(override val routeId: String, override val timestamp: String) : PendingEvent()

    @Serializable
    data class DriverArrived(override val routeId: String, override val timestamp: String) : PendingEvent()

    @Serializable
    data class MaterialCollected(override val routeId: String, override val timestamp: String) : PendingEvent()

    @Serializable
    data class DeliveryConfirmed(
        override val routeId: String,
        val collectionPointId: String,
        override val timestamp: String,
    ) : PendingEvent()

    @Serializable
    data class WeightRegistered(
        override val routeId: String,
        val donorRequestId: String,
        val weightKg: Double,
        override val timestamp: String,
    ) : PendingEvent()
}

interface OfflineQueue {
    suspend fun enqueue(event: PendingEvent)
    suspend fun dequeueAll(): List<PendingEvent>
    suspend fun clear()
    suspend fun size(): Int
}
