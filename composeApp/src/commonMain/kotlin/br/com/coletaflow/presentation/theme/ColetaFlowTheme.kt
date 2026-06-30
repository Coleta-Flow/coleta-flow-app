package br.com.coletaflow.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColetaFlowColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = MintLight,
    onPrimaryContainer = EmeraldDeep,
    secondary = Slate700,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    error = StatusDanger,
    onError = Color.White,
)

@Composable
fun ColetaFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColetaFlowColorScheme,
        content = content,
    )
}
