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

// Todo el estado de la pantalla en un solo objeto inmutable
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
    val showConfirmDialog: Boolean = false, // Indica si el diálogo de confirmación de envío está visible
    val showExitDialog: Boolean = false, // Indica si el diálogo de confirmación de salida está visible
    val registerResult: RequestResult? = null // Estado del intento de registro
) {
    // Propiedad calculada: la pantalla no tiene que repetir esta lógica
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

    // Un único flujo con el estado completo de la pantalla
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // La lista de ciudades no cambia nunca, así que no hace parte del UI State
    val cities = listOf("Ciudad 1", "Ciudad 2", "Ciudad 3")

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            state.copy(
                name = newName,
                nameError = validateName(newName)
            )
        }
    }

    fun onCityChange(newCity: String) {
        _uiState.update { state ->
            state.copy(
                city = newCity,
                cityError = validateCity(newCity)
            )
        }
    }

    fun onAddressChange(newAddress: String) {
        _uiState.update { state ->
            state.copy(
                address = newAddress,
                addressError = validateAddress(newAddress)
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

    // La confirmación depende de dos campos: al cambiar la contraseña también se recalcula su error
    fun onPasswordChange(newPassword: String) {
        _uiState.update { state ->
            state.copy(
                password = newPassword,
                passwordError = validatePassword(newPassword),
                // Solo se valida la confirmación si el usuario ya escribió algo en ella
                confirmPasswordError = if (state.confirmPassword.isEmpty()) state.confirmPasswordError
                else validateConfirmPassword(newPassword, state.confirmPassword)
            )
        }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update { state ->
            state.copy(
                confirmPassword = newConfirmPassword,
                confirmPasswordError = validateConfirmPassword(state.password, newConfirmPassword)
            )
        }
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
        register()
    }

    // El usuario canceló o cerró el diálogo
    fun onDismissConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    // El usuario presionó el botón "atrás" del dispositivo
    fun onBackClick() {
        _uiState.update { it.copy(showExitDialog = true) }
    }

    // El usuario confirmó que quiere salir del formulario
    fun onConfirmExit() {
        // Se reinicia el estado: se borran los campos y se oculta el diálogo
        _uiState.value = RegisterUiState()
    }

    // El usuario canceló o cerró el diálogo de salida
    fun onDismissExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }

    private fun register() {

        // Si el formulario no es válido, no se hace nada
        if (!_uiState.value.isFormValid) return

        // viewModelScope es una corrutina atada al ciclo de vida del ViewModel
        viewModelScope.launch {

            // La solicitud pasa al estado de carga
            _uiState.update { it.copy(registerResult = RequestResult.Loading) }

            delay(2000) // Simula el tiempo que tardaría una consulta real

            // Se publica el resultado final de la solicitud (simulación de registro exitoso)
            _uiState.update { it.copy(registerResult = RequestResult.Success("Registro exitoso")) }
        }
    }

    // Permite limpiar el resultado después de mostrarlo en pantalla
    fun resetRegisterResult() {
        _uiState.update { it.copy(registerResult = null) }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre es obligatorio"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    private fun validateCity(city: String): String? {
        return if (city.isBlank()) "Selecciona una ciudad" else null
    }

    private fun validateAddress(address: String): String? {
        return if (address.isBlank()) "La dirección es obligatoria" else null
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

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Confirma tu contraseña"
            confirmPassword != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }
}
