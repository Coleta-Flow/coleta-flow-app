package br.com.coletaflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.coletaflow.app.ColetaFlowApp
import br.com.coletaflow.presentation.theme.ColetaFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColetaFlowTheme {
                ColetaFlowApp()
            }
        }
    }
}
