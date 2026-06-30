package br.com.coletaflow.features.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.coletaflow.presentation.state.UiState
import br.com.coletaflow.presentation.theme.ColetaFlowColors
import br.com.coletaflow.presentation.theme.EmeraldDeep
import br.com.coletaflow.presentation.theme.MintLight
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) onLoginSuccess()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ColetaFlowColors.Primary,
        focusedLabelColor = ColetaFlowColors.Primary,
        focusedLeadingIconColor = ColetaFlowColors.Primary,
        cursorColor = ColetaFlowColors.Primary,
        unfocusedBorderColor = Color(0xFF4B5563),
        unfocusedLabelColor = Color(0xFF9CA3AF),
        unfocusedLeadingIconColor = Color(0xFF9CA3AF),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeep),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ColetaFlowColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalShipping,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ColetaFlow",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Acesso do motorista",
                color = MintLight,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Exibir senha",
                            tint = Color(0xFF9CA3AF),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login(email, password)
                    },
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
            )

            AnimatedVisibility(visible = uiState is UiState.Error) {
                Text(
                    text = (uiState as? UiState.Error)?.message.orEmpty(),
                    color = ColetaFlowColors.Danger,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = uiState !is UiState.Loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColetaFlowColors.Primary),
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp),
                    )
                } else {
                    Text(
                        text = "Entrar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        Text(
            text = "v${MaterialTheme.typography.labelSmall}",
            color = Color(0xFF4B5563),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
        )
    }
}
