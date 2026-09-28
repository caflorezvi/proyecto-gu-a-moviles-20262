package co.edu.uniquindio.demoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.uniquindio.demoapp.core.theme.DemoAppTheme
import co.edu.uniquindio.demoapp.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoAppTheme {
                // La navegación de la aplicación se maneja aquí
                AppNavigation()
            }
        }
    }
}
