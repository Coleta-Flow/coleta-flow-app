package br.com.coletaflow.domain.repositories

import br.com.coletaflow.core.result.Result
import br.com.coletaflow.domain.entities.CollectionPoint
import br.com.coletaflow.domain.entities.DriverLocation
import br.com.coletaflow.domain.entities.Route

interface RouteRepository {
    suspend fun getAssignedRoutes(): Result<List<Route>>
    suspend fun getRouteById(id: String): Result<Route>
    suspend fun startRoute(routeId: String): Result<Map<String, String>>
    suspend fun confirmArrival(routeId: String): Result<Route>
    suspend fun confirmCollection(routeId: String): Result<Route>
    suspend fun deliverToPoint(routeId: String, collectionPointId: String, lat: Double, lng: Double): Result<Route>
    suspend fun registerWeight(routeId: String, donorRequestId: String, weightKg: Double): Result<Unit>
    suspend fun finishRoute(routeId: String): Result<Route>
    suspend fun sendLocation(routeId: String, location: DriverLocation): Result<Unit>
    suspend fun getCollectionPoints(): Result<List<CollectionPoint>>
}
