package co.edu.uniquindio.demoapp.features.notifications

import androidx.lifecycle.ViewModel
import co.edu.uniquindio.demoapp.domain.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    // Usamos StateFlow para manejar la lista de notificaciones de manera reactiva
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    init {
        _notifications.value = fetchNotifications()
    }

    // El usuario abrió una notificación
    fun markAsRead(notificationId: String) {
        _notifications.update { list ->
            list.map { if (it.id == notificationId) it.copy(read = true) else it }
        }
    }

    // El usuario presionó "Marcar todo leído"
    fun markAllAsRead() {
        _notifications.update { list ->
            list.map { it.copy(read = true) }
        }
    }

    // Notificaciones de ejemplo. Más adelante deben venir de un repositorio, como los reportes
    private fun fetchNotifications(): List<Notification> {
        val now = LocalDateTime.now()

        return listOf(
            Notification(
                id = "1",
                type = "comment",
                title = "Maria comentó tu reporte",
                message = "«Hueco en la vía»",
                date = now.minusMinutes(10),
                reportId = "1"
            ),
            Notification(
                id = "2",
                type = "resolved",
                title = "Tu reporte fue marcado como resuelto · +5 pts",
                message = "«Basura acumulada»",
                date = now.minusHours(4),
                reportId = "3"
            ),
            Notification(
                id = "3",
                type = "verified",
                title = "Tu reporte fue verificado · +20 pts",
                message = "«Basura acumulada»",
                date = now.minusDays(1),
                read = true,
                reportId = "3"
            ),
            Notification(
                id = "4",
                type = "badge",
                title = "Nueva insignia: Primer reporte",
                message = "Creaste tu primer reporte en la comunidad.",
                date = now.minusDays(5),
                read = true
            )
        )
    }

}
