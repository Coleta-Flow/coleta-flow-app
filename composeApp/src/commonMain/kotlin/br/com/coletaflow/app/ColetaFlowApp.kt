package br.com.coletaflow.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.coletaflow.features.auth.LoginScreen
import br.com.coletaflow.features.delivery.RegisterWeightScreen
import br.com.coletaflow.features.routes.RoutesListScreen
import br.com.coletaflow.features.tracking.ActiveRouteScreen
import br.com.coletaflow.presentation.navigation.Screen

@Composable
fun ColetaFlowApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.RoutesList.route) },
            )
        }

        composable(Screen.RoutesList.route) {
            RoutesListScreen(
                onRouteClick = { routeId ->
                    navController.navigate("routes/$routeId/active")
                },
            )
        }

        composable(
            route = "routes/{routeId}/active",
            arguments = listOf(navArgument("routeId") { type = NavType.StringType }),
        ) { backStack ->
            val routeId = backStack.arguments?.getString("routeId") ?: return@composable
            ActiveRouteScreen(
                routeId = routeId,
                onRouteFinished = { navController.navigate(Screen.RoutesList.route) },
            )
        }

        composable(
            route = "routes/{routeId}/weight",
            arguments = listOf(navArgument("routeId") { type = NavType.StringType }),
        ) { backStack ->
            val routeId = backStack.arguments?.getString("routeId") ?: return@composable
            RegisterWeightScreen(
                routeId = routeId,
                onWeightRegistered = { navController.popBackStack() },
            )
        }
    }
}
