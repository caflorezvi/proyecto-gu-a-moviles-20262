package co.edu.uniquindio.demoapp.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Elementos visuales que comparten las pantallas de bienvenida, inicio de sesión y registro

// Logo, nombre y lema de la aplicación
@Composable
fun AppHeader(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Text(
            text = "Alerta Vecinal",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Reporta lo que pasa en tu barrio y ayuda a que se resuelva.",
            modifier = Modifier.widthIn(max = 280.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Pestañas para pasar del inicio de sesión al registro y viceversa
@Composable
fun AuthTabs(
    isLogin: Boolean, // true si la pantalla actual es la de inicio de sesión
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
){
    PrimaryTabRow(
        selectedTabIndex = if (isLogin) 0 else 1,
        containerColor = Color.Transparent
    ) {
        Tab(
            selected = isLogin,
            // Solo se navega si la pestaña no es la de la pantalla actual
            onClick = { if (!isLogin) onNavigateToLogin() },
            text = {
                Text(text = "Iniciar sesión")
            },
            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Tab(
            selected = !isLogin,
            onClick = { if (isLogin) onNavigateToRegister() },
            text = {
                Text(text = "Crear cuenta")
            },
            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Campo de contraseña con un botón para mostrar u ocultar el texto
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null
){
    // Estado interno para controlar si la contraseña está visible o no
    var visible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        label = {
            Text(text = label)
        },
        trailingIcon = {
            IconButton(
                onClick = { visible = !visible }
            ) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        isError = error != null,
        supportingText = error?.let {
            { Text(text = it) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}
