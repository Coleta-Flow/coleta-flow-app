package br.com.coletaflow.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object RoutesList : Screen("routes")
    data class RouteDetail(val routeId: String = "{routeId}") : Screen("routes/{routeId}")
    data class ActiveRoute(val routeId: String = "{routeId}") : Screen("routes/{routeId}/active")
    data class ConfirmArrival(val routeId: String = "{routeId}") : Screen("routes/{routeId}/arrival")
    data class ConfirmCollection(val routeId: String = "{routeId}") : Screen("routes/{routeId}/collection")
    data class SelectDeliveryPoint(val routeId: String = "{routeId}") : Screen("routes/{routeId}/delivery-point")
    data class ConfirmDelivery(val routeId: String = "{routeId}", val pointId: String = "{pointId}") : Screen("routes/{routeId}/delivery/{pointId}")
    data class RegisterWeight(val routeId: String = "{routeId}") : Screen("routes/{routeId}/weight")
    data class FinishRoute(val routeId: String = "{routeId}") : Screen("routes/{routeId}/finish")
}
