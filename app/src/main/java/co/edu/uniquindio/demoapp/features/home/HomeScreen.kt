package co.edu.uniquindio.demoapp.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.demoapp.core.component.AppHeader

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit, // Función para navegar a la pantalla de Login
    onNavigateToRegister: () -> Unit // Función para navegar a la pantalla de Registro
){
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el espacio disponible
            .safeDrawingPadding() // Evita que el contenido quede debajo de las barras del sistema
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
    ) {
        // Los Spacer con weight centran el encabezado y dejan los botones en la parte inferior
        Spacer(modifier = Modifier.weight(1f))

        AppHeader()

        Spacer(modifier = Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre botones
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = onNavigateToLogin // Se indica qué hacer al presionar el botón, pero la lógica no se implementa aquí
            ) {
                Text(text = "Iniciar sesión")
            }
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = onNavigateToRegister // Se indica qué hacer al presionar el botón, pero la lógica no se implementa aquí
            ) {
                Text(text = "Crear cuenta")
            }
        }

    }
}
