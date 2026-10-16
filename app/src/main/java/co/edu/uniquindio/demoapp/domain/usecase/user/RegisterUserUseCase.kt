package co.edu.uniquindio.demoapp.domain.usecase.user

import co.edu.uniquindio.demoapp.domain.model.User
import co.edu.uniquindio.demoapp.domain.repository.UserRepository
import jakarta.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        if( userRepository.findByEmail(user.email) != null  ){
            throw IllegalArgumentException("El usuario ya existe")
        }
        userRepository.save(user)
    }
}