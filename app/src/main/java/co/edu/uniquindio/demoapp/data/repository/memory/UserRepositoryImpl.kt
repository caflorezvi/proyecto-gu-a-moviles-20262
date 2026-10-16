package co.edu.uniquindio.demoapp.data.repository.memory

import co.edu.uniquindio.demoapp.domain.model.User
import co.edu.uniquindio.demoapp.domain.repository.UserRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import co.edu.uniquindio.demoapp.domain.model.UserRole

@Singleton
class UserRepositoryImpl @Inject constructor(): UserRepository {

    // Lista de usuarios en memoria con algunos datos de ejemplo
    private val users = mutableListOf(
        User(
            id = "1",
            name = "Juan",
            city = "Ciudad 1",
            address = "Calle 123",
            email = "juan@email.com",
            password = "111111",
            profilePictureUrl = "https://m.media-amazon.com/images/I/41g6jROgo0L.png"
        ),
        User(
            id = "2",
            name = "Maria",
            city = "Pereira",
            address = "Calle 456",
            email = "maria@email.com",
            password = "222222",
            profilePictureUrl = "https://picsum.photos/200?random=2"
        ),
        User(
            id = "3",
            name = "Carlos",
            city = "Armenia",
            address = "Calle 789",
            email = "carlos@email.com",
            password = "333333",
            profilePictureUrl = "https://picsum.photos/200?random=3",
            role = UserRole.ADMIN
        )
    )

    override suspend fun save(user: User) {
        users.add(user)
    }

    override suspend fun findByEmail(email: String): User?{
        return users.firstOrNull { it.email == email }
    }

    override suspend fun findById(id: String): User? {
        return users.firstOrNull { it.id == id }
    }

    override suspend fun login(email: String, password: String): User? {
        return users.firstOrNull { it.email == email && it.password == password }
    }
}