package br.com.coletaflow.features.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.coletaflow.domain.usecases.GeofenceResult
import br.com.coletaflow.presentation.state.UiState
import br.com.coletaflow.presentation.theme.ColetaFlowColors
import br.com.coletaflow.presentation.theme.MintLight
import br.com.coletaflow.presentation.viewmodels.ActiveRouteViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ActiveRouteScreen(
    routeId: String,
    onRouteFinished: () -> Unit,
    viewModel: ActiveRouteViewModel = koinViewModel { parametersOf(routeId) },
) {
    val routeState by viewModel.route.collectAsState()
    val location by viewModel.currentLocation.collectAsState()
    val geofenceResult by viewModel.geofenceResult.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    LaunchedEffect(actionState) {
        if (actionState is UiState.Success) onRouteFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Placeholder do mapa (Mapbox integrado no androidMain)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Navigation,
                    contentDescription = null,
                    tint = ColetaFlowColors.Primary,
                    modifier = Modifier.size(56.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text("Mapa de rastreamento", color = Color(0xFF6B7280), fontSize = 13.sp)
            }
        }

        // Badge de GPS ativo
        if (location != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF064E3B).copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.GpsFixed,
                        contentDescription = null,
                        tint = ColetaFlowColors.Primary,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "GPS ativo",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        // Painel inferior
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                when (val state = routeState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = ColetaFlowColors.Primary)
                        }
                    }

                    is UiState.Error -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = ColetaFlowColors.Danger,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = state.message,
                                color = ColetaFlowColors.Danger,
                                fontSize = 14.sp,
                            )
                        }
                    }

                    is UiState.Success -> {
                        val route = state.data

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MintLight),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalShipping,
                                    contentDescription = null,
                                    tint = ColetaFlowColors.PrimaryDark,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Rota em andamento",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF111827),
                                )
                                Text(
                                    text = route.donorRequest.donorName,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280),
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Divider(color = Color(0xFFE5E7EB))
                        Spacer(Modifier.height(16.dp))

                        // Geofence feedback
                        when (val geo = geofenceResult) {
                            is GeofenceResult.WithinRange -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = ColetaFlowColors.Primary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Dentro do raio — ${geo.distanceMeters.toInt()}m do ponto",
                                        color = ColetaFlowColors.PrimaryDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                            }
                            is GeofenceResult.OutOfRange -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.WarningAmber,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Fora do raio de entrega",
                                            color = Color(0xFF92400E),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = "${geo.distanceMeters.toInt()}m de distância • Necessário: ${geo.requiredRadius.toInt()}m",
                                            color = Color(0xFFB45309),
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            }
                            null -> Unit
                        }

                        // Botão de ação principal
                        val isGeofenceOk = geofenceResult is GeofenceResult.WithinRange || geofenceResult == null
                        val isActing = actionState is UiState.Loading

                        if (route.status == "PLANNED" || route.status == "ASSIGNED") {
                            Button(
                                onClick = { viewModel.startRoute() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                enabled = !isActing,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            ) {
                                if (isActing) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp),
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.LocalShipping,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp),
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "Iniciar Rota",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        if (route.status != "PLANNED" && route.status != "ASSIGNED") {
                        Button(
                            onClick = { viewModel.confirmArrival() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = isGeofenceOk && !isActing,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColetaFlowColors.Primary),
                        ) {
                            if (isActing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp),
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Confirmar chegada",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}
