package co.edu.uniquindio.demoapp.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.demoapp.R

@Composable
fun HomeScreen(
    ciudad: String,
    temperatura: String
){
    Column(
        modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio disponible
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically), // Espacio entre elementos y centrado vertical
        horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
    ) {
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = "Welcome Image"
        )
        Text(
            text = "Pantalla de bienvenida. Su ubicación es $ciudad y la temperatura actual es $temperatura"
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally), // Espacio entre botones y centrado horizontal
            verticalAlignment = Alignment.CenterVertically // Centrado vertical
        ) {
            Button(
                onClick = {
                    // Acción al hacer clic en el botón de inicio de sesión
                }
            ) {
                Text(text = "Iniciar sesión")
            }
            Button(
                onClick = {
                    // Acción al hacer clic en el botón de registro
                }
            ) {
                Text(text = "Crear una cuenta")
            }
        }

    }
}