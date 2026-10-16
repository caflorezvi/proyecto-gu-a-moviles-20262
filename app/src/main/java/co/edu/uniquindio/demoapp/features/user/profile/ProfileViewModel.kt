package co.edu.uniquindio.demoapp.features.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import co.edu.uniquindio.demoapp.domain.model.User
import co.edu.uniquindio.demoapp.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.demoapp.domain.usecase.report.ObserveReportsUseCase
import co.edu.uniquindio.demoapp.domain.usecase.user.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import jakarta.inject.Inject

// El estado de la pantalla de perfil: el usuario, sus reportes, sus puntos y todos los reportes (para el moderador)
data class ProfileUiState(
    val user: User? = null,
    val myReports: List<Report> = emptyList(),
    val allReports: List<Report> = emptyList(),
    val points: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    observeReportsUseCase: ObserveReportsUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val user = MutableStateFlow<User?>(null)

    // El estado se recalcula cada vez que cambia el usuario o la lista de reportes
    val uiState: StateFlow<ProfileUiState> = combine(user, observeReportsUseCase()) { user, reports ->
        val myReports = reports.filter { it.ownerId == user?.id && it.status != ReportStatus.DELETED }

        ProfileUiState(
            user = user,
            myReports = myReports,
            allReports = reports,
            points = reputationPoints(myReports)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    init {
        viewModelScope.launch {
            user.value = getUserByIdUseCase(CURRENT_USER_ID)
        }
    }

    // Puntos de reputación según las reglas del prototipo:
    // +10 por crear un reporte, +20 si fue verificado y +5 si además fue resuelto
    private fun reputationPoints(reports: List<Report>): Int {
        return reports.sumOf { report ->
            when (report.status) {
                ReportStatus.VERIFIED -> 10 + 20
                ReportStatus.RESOLVED -> 10 + 20 + 5
                else -> 10
            }
        }
    }

    fun logout() {
        // Al borrarse la sesión, AppNavigation muestra automáticamente AuthNavigation
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    companion object {
        // TODO: reemplazar por el ID del usuario que inició sesión cuando la app maneje la sesión
        private const val CURRENT_USER_ID = "1"
    }

}
