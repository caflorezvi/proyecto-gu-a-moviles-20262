package co.edu.uniquindio.demoapp.domain.usecase.auth

import co.edu.uniquindio.demoapp.domain.model.UserSession
import co.edu.uniquindio.demoapp.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import jakarta.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<UserSession?> = sessionRepository.session
}