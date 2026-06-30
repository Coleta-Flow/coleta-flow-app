package br.com.coletaflow.features.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.coletaflow.domain.entities.Route
import br.com.coletaflow.presentation.state.UiState
import br.com.coletaflow.presentation.theme.ColetaFlowColors
import br.com.coletaflow.presentation.theme.EmeraldDeep
import br.com.coletaflow.presentation.theme.MintLight
import br.com.coletaflow.presentation.viewmodels.RoutesViewModel
import org.koin.compose.viewmodel.koinViewModel

private val statusColors = mapOf(
    "ASSIGNED" to (Color(0xFF0EA5E9) to Color(0xFFE0F2FE)),
    "IN_PROGRESS" to (ColetaFlowColors.Primary to Color(0xFFD1FAE5)),
    "PLANNED" to (Color(0xFF6B7280) to Color(0xFFF3F4F6)),
    "FINISHED" to (Color(0xFF6B7280) to Color(0xFFF3F4F6)),
    "CANCELLED" to (Color(0xFFEF4444) to Color(0xFFFEE2E2)),
)

private val statusLabels = mapOf(
    "ASSIGNED" to "Atribuída",
    "IN_PROGRESS" to "Em andamento",
    "PLANNED" to "Planejada",
    "FINISHED" to "Concluída",
    "CANCELLED" to "Cancelada",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesListScreen(
    onRouteClick: (String) -> Unit,
    viewModel: RoutesViewModel = koinViewModel(),
) {
    val routesState by viewModel.routes.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ColetaFlowColors.Primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalShipping,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Minhas Rotas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
            },
            actions = {
                IconButton(onClick = viewModel::loadRoutes) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Atualizar",
                        tint = EmeraldDeep,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = EmeraldDeep,
            ),
        )

        when (val state = routesState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColetaFlowColors.Primary)
                }
            }

            is UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.CloudOff,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(56.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Falha ao carregar rotas",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            fontSize = 16.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp,
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = viewModel::loadRoutes,
                            colors = ButtonDefaults.buttonColors(containerColor = ColetaFlowColors.Primary),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Route,
                                contentDescription = null,
                                tint = Color(0xFFD1D5DB),
                                modifier = Modifier.size(72.dp),
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Nenhuma rota atribuída",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151),
                                fontSize = 16.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Aguarde a atribuição de uma nova rota.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 13.sp,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.data) { route ->
                            RouteCard(route = route, onClick = { onRouteClick(route.id) })
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun RouteCard(route: Route, onClick: () -> Unit) {
    val (textColor, bgColor) = statusColors[route.status] ?: (Color(0xFF6B7280) to Color(0xFFF3F4F6))
    val label = statusLabels[route.status] ?: route.status

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MintLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalShipping,
                    contentDescription = null,
                    tint = ColetaFlowColors.PrimaryDark,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.donorRequest.donorName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF111827),
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = route.donorRequest.city,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = route.createdAt.take(10),
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                    )
                }

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgColor)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
