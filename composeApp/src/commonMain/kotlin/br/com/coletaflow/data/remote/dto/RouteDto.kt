package br.com.coletaflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    val id: String,
    val tenantId: String? = null,
    val driverId: String,
    val status: String,
    val trackingToken: String? = null,
    val startedAt: String? = null,
    val finishedAt: String? = null,
    val stops: List<RouteStopDto> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class RouteStopDto(
    val id: String,
    val routeId: String,
    val donorRequestId: String,
    val sequence: Int,
    val arrivedAt: String? = null,
    val collectedAt: String? = null,
    val donorRequest: DonorRequestSummaryDto? = null,
)

@Serializable
data class DonorRequestSummaryDto(
    val id: String,
    val trackingCode: String,
    val donorName: String,
    val donorWhatsapp: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: String,
    val materialDescription: String? = null,
)

@Serializable
data class LocationPayloadDto(
    val lat: Double,
    val lng: Double,
    val accuracy: Double? = null,
    val speed: Double? = null,
    val heading: Int? = null,
    val battery: Int? = null,
)

@Serializable
data class StartRouteResponseDto(
    val routeId: String,
    val trackingToken: String,
    val message: String,
)

@Serializable
data class WeightRegistrationDto(
    val grossWeightKg: Double,
    val netWeightKg: Double,
    val tareKg: Double? = null,
    val notes: String? = null,
)
