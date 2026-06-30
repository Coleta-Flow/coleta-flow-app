package br.com.coletaflow.data.route

import br.com.coletaflow.core.result.Result
import br.com.coletaflow.data.remote.api.RoutesApi
import br.com.coletaflow.data.remote.dto.LocationPayloadDto
import br.com.coletaflow.data.remote.dto.RouteDto
import br.com.coletaflow.domain.entities.CollectionPoint
import br.com.coletaflow.domain.entities.DonorRequest
import br.com.coletaflow.domain.entities.DriverLocation
import br.com.coletaflow.domain.entities.Route
import br.com.coletaflow.domain.repositories.RouteRepository

class RouteRepositoryImpl(
    private val routesApi: RoutesApi,
) : RouteRepository {

    override suspend fun getAssignedRoutes(): Result<List<Route>> =
        try {
            val dtos = routesApi.getAssignedRoutes()
            Result.Success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao buscar rotas")
        }

    override suspend fun getRouteById(id: String): Result<Route> =
        try {
            val dto = routesApi.getRouteById(id)
            Result.Success(dto.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao buscar rota")
        }

    override suspend fun startRoute(routeId: String): Result<Map<String, String>> =
        try {
            val response = routesApi.startRoute(routeId)
            Result.Success(
                mapOf(
                    "routeId" to response.routeId,
                    "trackingToken" to response.trackingToken,
                    "message" to response.message,
                ),
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao iniciar rota")
        }

    override suspend fun confirmArrival(routeId: String): Result<Route> =
        try {
            val dto = routesApi.getRouteById(routeId)
            val firstPendingStop = dto.stops.firstOrNull { it.arrivedAt == null }?.id ?: ""
            routesApi.confirmArrivalAtDonor(routeId, firstPendingStop)
            Result.Success(routesApi.getRouteById(routeId).toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao confirmar chegada")
        }

    override suspend fun confirmCollection(routeId: String): Result<Route> =
        try {
            val dto = routesApi.getRouteById(routeId)
            val firstPendingStop = dto.stops.firstOrNull { it.collectedAt == null }?.id ?: ""
            routesApi.confirmCollection(routeId, firstPendingStop)
            Result.Success(routesApi.getRouteById(routeId).toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao confirmar coleta")
        }

    override suspend fun deliverToPoint(
        routeId: String,
        collectionPointId: String,
        lat: Double,
        lng: Double,
    ): Result<Route> =
        try {
            routesApi.deliverToPoint(routeId, lat, lng)
            Result.Success(routesApi.getRouteById(routeId).toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao registrar entrega")
        }

    override suspend fun registerWeight(
        routeId: String,
        donorRequestId: String,
        weightKg: Double,
    ): Result<Unit> =
        try {
            routesApi.registerWeight(
                routeId,
                br.com.coletaflow.data.remote.dto.WeightRegistrationDto(
                    grossWeightKg = weightKg,
                    netWeightKg = weightKg,
                ),
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao registrar peso")
        }

    override suspend fun finishRoute(routeId: String): Result<Route> =
        try {
            routesApi.finishRoute(routeId)
            Result.Success(routesApi.getRouteById(routeId).toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao finalizar rota")
        }

    override suspend fun sendLocation(routeId: String, location: DriverLocation): Result<Unit> =
        try {
            routesApi.sendLocation(
                routeId,
                LocationPayloadDto(
                    lat = location.lat,
                    lng = location.lng,
                    accuracy = location.accuracy,
                    speed = location.speed,
                    heading = location.heading,
                    battery = location.battery,
                ),
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao enviar localização")
        }

    override suspend fun getCollectionPoints(): Result<List<CollectionPoint>> =
        Result.Error("Não implementado")

    // --- Mapeamento DTO -> Domínio ---

    private fun RouteDto.toDomain(): Route {
        val firstStop = stops.firstOrNull()
        val donor = firstStop?.donorRequest
        return Route(
            id = id,
            status = status,
            trackingToken = trackingToken,
            donorRequest = DonorRequest(
                id = donor?.id ?: "",
                trackingCode = donor?.trackingCode ?: "",
                donorName = donor?.donorName ?: "",
                address = donor?.address ?: "",
                city = "",
                description = donor?.materialDescription ?: "",
            ),
            driver = null,
            createdAt = createdAt,
        )
    }
}
