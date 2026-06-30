package br.com.coletaflow.data.remote.api

import br.com.coletaflow.data.remote.dto.LocationPayloadDto
import br.com.coletaflow.data.remote.dto.RouteDto
import br.com.coletaflow.data.remote.dto.StartRouteResponseDto
import br.com.coletaflow.data.remote.dto.WeightRegistrationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RoutesApi(private val client: HttpClient) {

    suspend fun getAssignedRoutes(): List<RouteDto> =
        client.get("/routes/assigned").body()

    suspend fun getRouteById(routeId: String): RouteDto =
        client.get("/routes/$routeId").body()

    suspend fun startRoute(routeId: String): StartRouteResponseDto =
        client.post("/routes/$routeId/start").body()

    suspend fun sendLocation(routeId: String, payload: LocationPayloadDto) {
        client.post("/routes/$routeId/location") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
    }

    suspend fun confirmArrivalAtDonor(routeId: String, stopId: String) {
        client.patch("/routes/$routeId/stops/$stopId/arrive")
    }

    suspend fun confirmCollection(routeId: String, stopId: String) {
        client.patch("/routes/$routeId/stops/$stopId/collect")
    }

    suspend fun deliverToPoint(routeId: String, latitude: Double, longitude: Double) {
        client.post("/routes/$routeId/deliver") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("latitude" to latitude, "longitude" to longitude))
        }
    }

    suspend fun registerWeight(routeId: String, weight: WeightRegistrationDto) {
        client.post("/routes/$routeId/weight") {
            contentType(ContentType.Application.Json)
            setBody(weight)
        }
    }

    suspend fun finishRoute(routeId: String) {
        client.patch("/routes/$routeId/finish")
    }
}
