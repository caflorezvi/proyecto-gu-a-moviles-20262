package co.edu.uniquindio.demoapp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.demoapp.domain.model.UserSession
import co.edu.uniquindio.demoapp.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.demoapp.domain.usecase.auth.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import jakarta.inject.Inject

// Define los posibles estados de la sesión
sealed interface SessionState {
    data object Loading : SessionState
    data object NotAuthenticated : SessionState
    data class Authenticated(val session: UserSession) : SessionState
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase
) : ViewModel() {

    // Flujo que representa el estado de la sesión
    val sessionState: StateFlow<SessionState> = observeSessionUseCase()
        .map { session -> // Mapea la sesión a un estado de sesión
            if (session != null) {
                SessionState.Authenticated(session)
            } else {
                SessionState.NotAuthenticated
            }
        }
        .stateIn( // Convierte el flujo en un StateFlow para que pueda ser observado
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionState.Loading
        )
}