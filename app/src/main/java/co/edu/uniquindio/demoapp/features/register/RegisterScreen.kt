package co.edu.uniquindio.demoapp.features.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face4
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniquindio.demoapp.core.component.DropdownMenu
import co.edu.uniquindio.demoapp.core.util.RequestResult

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(state.registrationResult) {
        when (val result = state.registrationResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetRegistrationResult()
                // Opcional: navegar a login o limpiar el formulario
                // viewModel.resetForm()
            }
            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetRegistrationResult()
            }
            is RequestResult.Loading, null -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = state.registrationResult is RequestResult.Failure
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
                .padding(horizontal = 30.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 24.dp)
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
                label = { Text(text = "Email") },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                visualTransformation = PasswordVisualTransformation(),
                label = { Text(text = "Contraseña") },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                visualTransformation = PasswordVisualTransformation(),
                label = { Text(text = "Confirmar Contraseña") },
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError?.let { { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                onClick = { viewModel.register() },
                enabled = state.isFormValid && state.registrationResult !is RequestResult.Loading,
                content = {
                    if (state.registrationResult is RequestResult.Loading) {
                        Text(text = "Registrando...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Face4,
                            contentDescription = "Icono del boton de registro"
                        )
                        Text(text = "Registrarse")
                    }
                }
            )
        }
    }
}
