package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.RouteViewModel
import com.example.ui.RutaSeguraApp
import com.example.ui.theme.MyApplicationTheme

/**
 * Actividad principal de RUTA SEGURA para estudiantes del Instituto INDEL.
 * Punto de entrada de la aplicación en Android.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilita el diseño de borde a borde para aprovechar toda la pantalla del teléfono
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: RouteViewModel = viewModel()
                    RutaSeguraApp(viewModel = viewModel)
                }
            }
        }
    }
}
