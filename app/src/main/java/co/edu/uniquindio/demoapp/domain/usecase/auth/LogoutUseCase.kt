package co.edu.uniquindio.demoapp.domain.usecase.auth

import co.edu.uniquindio.demoapp.domain.repository.SessionRepository
import jakarta.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        sessionRepository.clearSession()
    }
}