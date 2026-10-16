package co.edu.uniquindio.demoapp.domain.usecase.auth

import co.edu.uniquindio.demoapp.domain.model.User
import co.edu.uniquindio.demoapp.domain.repository.SessionRepository
import co.edu.uniquindio.demoapp.domain.repository.UserRepository
import jakarta.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String): User? {
        val user = userRepository.login(email, password)
        // Si las credenciales son válidas, guardamos la sesión del usuario
        if (user != null) {
            sessionRepository.saveSession(user.id, user.role)
        }
        return user
    }
}