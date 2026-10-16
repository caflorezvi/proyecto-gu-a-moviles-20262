package co.edu.uniquindio.demoapp.domain.usecase.user

import co.edu.uniquindio.demoapp.domain.model.User
import co.edu.uniquindio.demoapp.domain.repository.UserRepository
import jakarta.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): User? = userRepository.findById(userId)
}