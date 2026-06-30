package br.com.coletaflow.features.delivery

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.coletaflow.presentation.theme.ColetaFlowColors
import br.com.coletaflow.presentation.theme.EmeraldDeep
import br.com.coletaflow.presentation.theme.MintLight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterWeightScreen(
    routeId: String,
    onWeightRegistered: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    var grossWeight by remember { mutableStateOf("") }
    var tare by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val netWeight by remember {
        derivedStateOf {
            val gross = grossWeight.toDoubleOrNull() ?: 0.0
            val t = tare.toDoubleOrNull() ?: 0.0
            if (gross > 0.0) gross - t else null
        }
    }

    val canSubmit = grossWeight.toDoubleOrNull()?.let { it > 0.0 } == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
    ) {
        TopAppBar(
            title = { Text("Registrar pesagem", fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = EmeraldDeep,
                navigationIconContentColor = EmeraldDeep,
            ),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header informativo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MintLight)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColetaFlowColors.Primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Scale,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Pesagem dos materiais",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = EmeraldDeep,
                    )
                    Text(
                        text = "Rota #${routeId.take(8).uppercase()}",
                        fontSize = 12.sp,
                        color = ColetaFlowColors.PrimaryDark,
                    )
                }
            }

            // Campo peso bruto
            OutlinedTextField(
                value = grossWeight,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) grossWeight = it },
                label = { Text("Peso bruto (kg)") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Straighten,
                        contentDescription = null,
                        tint = ColetaFlowColors.Primary,
                    )
                },
                suffix = { Text("kg", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColetaFlowColors.Primary,
                    focusedLabelColor = ColetaFlowColors.Primary,
                    focusedLeadingIconColor = ColetaFlowColors.Primary,
                    cursorColor = ColetaFlowColors.Primary,
                ),
            )

            // Campo tara
            OutlinedTextField(
                value = tare,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) tare = it },
                label = { Text("Tara (kg)") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Scale,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                    )
                },
                suffix = { Text("kg", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColetaFlowColors.Primary,
                    focusedLabelColor = ColetaFlowColors.Primary,
                    cursorColor = ColetaFlowColors.Primary,
                ),
            )

            // Resultado automático do peso líquido
            if (netWeight != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF0FDF4))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Calculate,
                        contentDescription = null,
                        tint = ColetaFlowColors.Primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Peso líquido calculado",
                            fontSize = 12.sp,
                            color = ColetaFlowColors.PrimaryDark,
                        )
                        Text(
                            text = "%.2f kg".format(netWeight),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = EmeraldDeep,
                        )
                    }
                }
            }

            // Campo de observações
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.EditNote,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                    )
                },
                placeholder = { Text("Condição do material, ocorrências…", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColetaFlowColors.Primary,
                    focusedLabelColor = ColetaFlowColors.Primary,
                    cursorColor = ColetaFlowColors.Primary,
                ),
            )
        }

        // Botão fixo no rodapé
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Button(
                onClick = { /* viewModel.registerWeight(routeId, grossWeight, tare, notes) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = canSubmit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColetaFlowColors.Primary,
                    disabledContainerColor = Color(0xFFD1D5DB),
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Confirmar pesagem",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
