package br.com.coletaflow.data.repositories

import br.com.coletaflow.data.local.OfflineQueue
import br.com.coletaflow.data.local.PendingEvent
import br.com.coletaflow.data.remote.api.RoutesApi
import br.com.coletaflow.data.remote.dto.LocationPayloadDto
import br.com.coletaflow.data.remote.dto.WeightRegistrationDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SyncManager(
    private val offlineQueue: OfflineQueue,
    private val routesApi: RoutesApi,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun syncPendingEvents(routeId: String) {
        scope.launch {
            val pending = offlineQueue.dequeueAll()
            pending.forEach { event ->
                try {
                    processEvent(routeId, event)
                } catch (_: Exception) {
                    // Evento já foi removido por dequeueAll; falhas silenciosas aqui
                }
            }
        }
    }

    private suspend fun processEvent(routeId: String, event: PendingEvent) {
        when (event) {
            is PendingEvent.LocationSnapshot -> {
                routesApi.sendLocation(
                    routeId,
                    LocationPayloadDto(lat = event.lat, lng = event.lng),
                )
            }
            is PendingEvent.DriverArrived -> {
                // routeId do evento — stopId não está disponível neste evento
                Unit
            }
            is PendingEvent.MaterialCollected -> {
                // stopId não está disponível neste evento
                Unit
            }
            is PendingEvent.DeliveryConfirmed -> {
                // collectionPointId disponível; coordenadas não estão no evento
                Unit
            }
            is PendingEvent.WeightRegistered -> {
                routesApi.registerWeight(
                    routeId,
                    WeightRegistrationDto(
                        grossWeightKg = event.weightKg,
                        netWeightKg = event.weightKg,
                    ),
                )
            }
            is PendingEvent.RouteStarted -> Unit // já processado ao iniciar
        }
    }
}
