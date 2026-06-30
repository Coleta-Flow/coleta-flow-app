package br.com.coletaflow.domain.entities

import kotlinx.serialization.Serializable

enum class RouteStatus {
    PLANNED, ASSIGNED, IN_PROGRESS, ARRIVED_AT_DONOR,
    COLLECTED, GOING_TO_COLLECTION_POINT, ARRIVED_AT_COLLECTION_POINT,
    DELIVERED, WEIGHED, FINISHED, CANCELLED
}

@Serializable
data class Route(
    val id: String,
    val status: String,
    val trackingToken: String? = null,
    val donorRequest: DonorRequest,
    val driver: Driver? = null,
    val createdAt: String,
)

@Serializable
data class DonorRequest(
    val id: String,
    val trackingCode: String,
    val donorName: String,
    val address: String,
    val city: String,
    val description: String,
)

@Serializable
data class Driver(
    val id: String,
    val user: UserBasic,
)

@Serializable
data class UserBasic(
    val name: String,
)

@Serializable
data class CollectionPoint(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val lat: Double,
    val lng: Double,
)
