package co.edu.uniquindio.demoapp.data.repository.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.uniquindio.demoapp.domain.model.UserRole
import co.edu.uniquindio.demoapp.domain.model.UserSession
import co.edu.uniquindio.demoapp.domain.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import jakarta.inject.Inject
import jakarta.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SessionRepository {
    // Claves para las preferencias
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val ROLE = stringPreferencesKey("role")
    }

    // Flujo para observar los datos de la sesión
    override val session: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        // Intenta obtener los datos de la sesión desde Data Store
        val userId = prefs[Keys.USER_ID]
        val roleStr = prefs[Keys.ROLE]

        // Si alguno de los datos es nulo, retorna null
        if (userId.isNullOrBlank() || roleStr.isNullOrBlank()) {
            null
        } else {
            // Retorna un objeto UserSession con los datos obtenidos si existen
            UserSession(
                userId = userId,
                role = UserRole.valueOf(roleStr)
            )
        }
    }

    override suspend fun saveSession(userId: String, role: UserRole) {
        // Guarda los datos de la sesión en Data Store (clave-valor)
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.ROLE] = role.name
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}