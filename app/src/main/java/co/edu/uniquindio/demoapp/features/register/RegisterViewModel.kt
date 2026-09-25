package co.edu.uniquindio.demoapp.features.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.demoapp.core.util.RequestResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class RegisterUiState(
    val name: String = "",
    val city: String = "",
    val address: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val cityError: String? = null,
    val addressError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val showConfirmDialog: Boolean = false,
    var showExitDialog: Boolean = false,
    val registrationResult: RequestResult? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                city.isNotBlank() &&
                address.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                nameError == null &&
                cityError == null &&
                addressError == null &&
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null
}

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // La lista de ciudades no cambia nunca, así que no hace parte del UI State
    val cities = listOf("Ciudad 1", "Ciudad 2", "Ciudad 3")

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            state.copy(
                name = newName,
                nameError = if (newName.isBlank()) "El nombre es obligatorio" else if (newName.length < 3) "El nombre debe tener al menos 3 caracteres" else null
            )
        }
    }

    fun onCityChange(newCity: String) {
        _uiState.update { state ->
            state.copy(
                city = newCity,
                cityError = if (newCity.isBlank()) "La ciudad es obligatoria" else null
            )
        }
    }

    fun onAddressChange(newAddress: String) {
        _uiState.update { state ->
            state.copy(
                address = newAddress,
                addressError = if (newAddress.isBlank()) "La dirección es obligatoria" else null
            )
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { state ->
            state.copy(
                email = newEmail,
                emailError = validateEmail(newEmail)
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { state ->
            val passwordError = validatePassword(newPassword)
            state.copy(
                password = newPassword,
                passwordError = passwordError,
                confirmPasswordError = if (newPassword != state.confirmPassword) "Las contraseñas no coinciden" else null
            )
        }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update { state ->
            state.copy(
                confirmPassword = newConfirmPassword,
                confirmPasswordError = if (newConfirmPassword != state.password) "Las contraseñas no coinciden" else null
            )
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password.length > 15 -> "La contraseña debe tener como máximo 15 caracteres"
            else -> null
        }
    }

    fun resetRegistrationResult() {
        _uiState.update { it.copy(registrationResult = null) }
    }

    fun resetForm() {
        _uiState.value = RegisterUiState()
    }

    // El usuario presionó el botón de registro
    fun onRegisterClick() {
        // Si el formulario no es válido, no se muestra el diálogo
        if (!_uiState.value.isFormValid) return
        _uiState.update { it.copy(showConfirmDialog = true) }
    }

    // El usuario confirmó en el diálogo
    fun onConfirmRegister() {
        _uiState.update { it.copy(showConfirmDialog = false) }
        register() // Función creada en la actividad práctica de la guía anterior
    }

    // El usuario canceló o cerró el diálogo
    fun onDismissConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun onDismissExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }

    fun onExitClick() {
        _uiState.update { it.copy(showExitDialog = true) }
    }

    fun register() {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(registrationResult = RequestResult.Loading) }

            delay(2000.milliseconds)

            // Simulación de registro exitoso
            _uiState.update { it.copy(registrationResult = RequestResult.Success("Registro exitoso")) }
        }
    }
}
