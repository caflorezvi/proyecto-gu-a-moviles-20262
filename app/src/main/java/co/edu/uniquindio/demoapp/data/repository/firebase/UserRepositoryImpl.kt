package co.edu.uniquindio.demoapp.data.repository.firebase

import co.edu.uniquindio.demoapp.domain.model.User
import co.edu.uniquindio.demoapp.domain.repository.UserRepository

class UserRepositoryImpl: UserRepository {


    override suspend fun save(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun login(
        email: String,
        password: String
    ): User? {
        TODO("Not yet implemented")
    }
}