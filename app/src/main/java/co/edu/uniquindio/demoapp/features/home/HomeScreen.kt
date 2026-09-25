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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.demoapp.R
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio disponible
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically), // Espacio entre elementos y centrado vertical
        horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRk1gcO1PFMqXWJZKzLXI14go6SEAABoW27J90jzTcQSomity3YRgjYHA4&s=10") // URL de la imagen
                .crossfade(true) // Efecto de desvanecimiento al cargar
                .error(R.drawable.avatar) // Imagen de error
                .build(),
            contentDescription = "Welcome Image"
        )
        Text(
            text = "Pantalla de bienvenida"
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally), // Espacio entre botones y centrado horizontal
            verticalAlignment = Alignment.CenterVertically // Centrado vertical
        ) {
            Button(
                onClick = {
                    onNavigateToLogin()
                }
            ) {
                Text(text = "Iniciar sesión")
            }
            Button(
                onClick = {
                    onNavigateToRegister()
                }
            ) {
                Text(text = "Crear una cuenta")
            }
        }

    }
}