package co.edu.uniquindio.demoapp.features.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import co.edu.uniquindio.demoapp.core.component.ConfirmAlertDialog
import co.edu.uniquindio.demoapp.core.component.PasswordTextField
import co.edu.uniquindio.demoapp.core.component.DropdownMenu
import co.edu.uniquindio.demoapp.core.util.RequestResult

@Composable
fun RegisterScreen(
    onNavigateToBack: () -> Unit, // Función para navegar hacia atrás
    onNavigateToLogin: () -> Unit = {}, // Función para navegar a la pantalla de Login
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // Estado para gestionar los snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    // Se observa el estado completo de la pantalla
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Intercepta el botón "atrás" mientras el diálogo de salida no esté visible
    BackHandler(enabled = !state.showExitDialog) {
        viewModel.onBackClick()
    }

    // Efecto para mostrar el snackbar cuando hay resultado
    LaunchedEffect(state.registerResult) {
        when (val result = state.registerResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetRegisterResult() // Limpiar para que el mensaje no se repita
            }
            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetRegisterResult() // Limpiar para que el mensaje no se repita
            }
            // Mientras carga, o si aún no se ha intentado el registro, no hay nada que mostrar
            is RequestResult.Loading, null -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = state.registerResult is RequestResult.Failure
                Snackbar(
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 20.dp)
        ) {
            AppHeader(
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            AuthTabs(
                isLogin = false,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToRegister = {}
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(text = "Nombre completo") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            DropdownMenu(
                value = state.city,
                onValueChange = viewModel::onCityChange,
                label = "Ciudad",
                supportingText = state.cityError,
                icon = Icons.Default.Home,
                list = viewModel.cities,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text(text = "Dirección") },
                isError = state.addressError != null,
                supportingText = state.addressError?.let { { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(text = "Correo electrónico") },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(text = it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            PasswordTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña",
                error = state.passwordError
            )

            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirmar contraseña",
                error = state.confirmPasswordError
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp),
                onClick = viewModel::onRegisterClick, // Se avisa al ViewModel que el usuario quiere registrarse
                // Se deshabilita mientras carga para evitar doble envío
                enabled = state.isFormValid && state.registerResult !is RequestResult.Loading,
                content = {
                    if (state.registerResult is RequestResult.Loading) {
                        Text(text = "Creando cuenta...")
                    } else {
                        Text(text = "Crear cuenta")
                    }
                }
            )
        }
    }

    if (state.showExitDialog) {
        ConfirmAlertDialog(
            title = "¿Está seguro de salir?",
            message = "Si sale ahora, se perderán los datos que ha ingresado.",
            onConfirm = {
                viewModel.onConfirmExit() // Limpiar el formulario y ocultar el diálogo
                onNavigateToBack() // Navegar hacia atrás
            },
            onDismiss = viewModel::onDismissExitDialog,
            confirmText = "Salir"
        )
    }

    if (state.showConfirmDialog) {
        ConfirmAlertDialog(
            title = "¿Está seguro de enviar los datos?",
            message = "Está a punto de crear una cuenta con el email ${state.email}.",
            onConfirm = viewModel::onConfirmRegister,
            onDismiss = viewModel::onDismissConfirmDialog
        )
    }
}
