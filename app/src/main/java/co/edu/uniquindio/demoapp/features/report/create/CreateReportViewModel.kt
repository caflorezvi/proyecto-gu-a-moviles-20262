package co.edu.uniquindio.demoapp.features.report.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.demoapp.core.component.reportCategories
import co.edu.uniquindio.demoapp.core.util.RequestResult
import co.edu.uniquindio.demoapp.domain.model.Location
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import co.edu.uniquindio.demoapp.domain.usecase.report.CreateReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

// Todo el estado de la pantalla en un solo objeto inmutable
data class CreateReportUiState(
    val category: String = reportCategories.first().label,
    val title: String = "",
    val description: String = "",
    val photoUrl: String = "", // URI de la foto elegida en la galería (vacío si no se ha elegido)
    val location: Location = DEFAULT_LOCATION,
    val titleError: String? = null,
    val createdReportId: String? = null, // ID del reporte recién creado, para navegar a su detalle
    val createResult: RequestResult? = null // Estado del intento de creación
) {
    // Propiedad calculada: la pantalla no tiene que repetir esta lógica
    val isFormValid: Boolean
        get() = title.isNotBlank() && titleError == null
}

// TODO: reemplazar por la ubicación real del dispositivo (o la elegida en el mapa)
private val DEFAULT_LOCATION = Location(latitude = 4.5339, longitude = -75.6811)

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val createReportUseCase: CreateReportUseCase
) : ViewModel() {

    // Un único flujo con el estado completo de la pantalla
    private val _uiState = MutableStateFlow(CreateReportUiState())
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { state ->
            state.copy(
                title = newTitle,
                titleError = validateTitle(newTitle)
            )
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onPhotoChange(newPhotoUrl: String) {
        _uiState.update { it.copy(photoUrl = newPhotoUrl) }
    }

    fun createReport() {

        val currentState = _uiState.value
        if (!currentState.isFormValid) return

        // El caso de uso es suspend, por eso lo invocamos dentro de viewModelScope
        viewModelScope.launch {

            _uiState.update { it.copy(createResult = RequestResult.Loading) }

            val newReport = Report(
                id = UUID.randomUUID().toString(), // Genera un ID único para el nuevo reporte
                title = currentState.title.trim(),
                description = currentState.description.trim().ifBlank { "Sin descripción." },
                location = currentState.location,
                status = ReportStatus.PENDING, // Todo reporte nuevo queda pendiente de verificación
                type = currentState.category,
                photoUrl = currentState.photoUrl,
                ownerId = CURRENT_USER_ID,
                date = LocalDate.now()
            )
            createReportUseCase(newReport)

            _uiState.update {
                it.copy(
                    createdReportId = newReport.id,
                    createResult = RequestResult.Success("Reporte enviado · pendiente de verificación")
                )
            }
        }
    }

    private fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "El título es obligatorio"
            title.trim().length < 5 -> "El título debe tener al menos 5 caracteres"
            else -> null
        }
    }

    companion object {
        // TODO: reemplazar por el ID del usuario que inició sesión cuando la app maneje la sesión
        private const val CURRENT_USER_ID = "1"
    }

}
