package co.edu.uniquindio.demoapp.features.report.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.demoapp.core.util.RequestResult
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.User
import co.edu.uniquindio.demoapp.domain.usecase.report.GetReportByIdUseCase
import co.edu.uniquindio.demoapp.domain.usecase.user.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import jakarta.inject.Inject

// El estado de la pantalla de detalle: el reporte, su autor y el resultado de la búsqueda
data class ReportDetailUiState(
    val report: Report? = null,
    val author: User? = null,
    val result: RequestResult? = null
)

@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    private val getReportByIdUseCase: GetReportByIdUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    fun findById(reportId: String) {
        // El caso de uso es suspend, por eso lo llamamos dentro de viewModelScope
        viewModelScope.launch {

            val foundReport = getReportByIdUseCase(reportId)

            // Con el reporte se busca también a su autor, para mostrar el nombre en el detalle
            val foundAuthor = foundReport?.let { getUserByIdUseCase(it.ownerId) }

            // El reporte, el autor y el resultado se publican juntos, en una sola actualización
            _uiState.update {
                it.copy(
                    report = foundReport,
                    author = foundAuthor,
                    result = if (foundReport != null) {
                        RequestResult.Success("Reporte encontrado")
                    } else {
                        RequestResult.Failure("El reporte no existe")
                    }
                )
            }
        }
    }

}