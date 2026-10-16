package co.edu.uniquindio.demoapp.domain.repository

import co.edu.uniquindio.demoapp.domain.model.UserRole
import co.edu.uniquindio.demoapp.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val session: Flow<UserSession?>
    suspend fun saveSession(userId: String, role: UserRole)
    suspend fun clearSession()
}