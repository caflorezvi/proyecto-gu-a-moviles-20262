package co.edu.uniquindio.demoapp.features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.demoapp.core.component.AppHeader
import co.edu.uniquindio.demoapp.core.component.AuthTabs
import co.edu.uniquindio.demoapp.core.component.PasswordTextField
import co.edu.uniquindio.demoapp.core.util.RequestResult

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {}, // Función para navegar a la pantalla de Registro
    viewModel: LoginViewModel = hiltViewModel()
) {

    // Estado para gestionar los snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    // Se observa el estado completo de la pantalla
    val state by viewModel.uiState.collectAsState()

    // Efecto para mostrar el snackbar cuando hay resultado
    LaunchedEffect(state.loginResult) {
        when (val result = state.loginResult) {

            is RequestResult.Success -> {
                // showSnackbar suspende mientras el mensaje está visible en pantalla
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetLoginResult() // Limpiar para que el mensaje no se repita
                viewModel.resetForm() // Borrar todos los campos del formulario
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetLoginResult() // Limpiar para que el mensaje no se repita
            }

            // Mientras carga, o si aún no se ha intentado el login, no hay nada que mostrar
            is RequestResult.Loading, null -> {}
        }
    }

    // Se envuelve el contenido dentro de un Scaffold
    Scaffold(
        snackbarHost = {
            // Mostrar el SnackbarHost para gestionar los snackbars. Un SnackbarHost es un contenedor que muestra los snackbars.
            SnackbarHost(snackbarHostState) { data ->
                val isError = state.loginResult is RequestResult.Failure
                // Mostrar el Snackbar con el estilo adecuado según si es error o éxito
                Snackbar(
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(
                        text = data.visuals.message
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar los padding del Scaffold
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp), // Padding adicional
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 20.dp)
        ) {
            AppHeader(
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            AuthTabs(
                isLogin = true,
                onNavigateToLogin = {},
                onNavigateToRegister = onNavigateToRegister
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = {
                    Text(text = "Correo electrónico")
                },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { error ->
                    { Text(text = error) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            PasswordTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña",
                error = state.passwordError
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp),
                onClick = {
                    viewModel.login()
                },
                // Se deshabilita mientras carga para evitar doble envío
                enabled = state.isFormValid && state.loginResult !is RequestResult.Loading,
                content = {
                    if (state.loginResult is RequestResult.Loading) {
                        Text(text = "Entrando...")
                    } else {
                        Text(text = "Entrar")
                    }
                }
            )

            TextButton(
                onClick = {}
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?"
                )
            }

        }

    }

}