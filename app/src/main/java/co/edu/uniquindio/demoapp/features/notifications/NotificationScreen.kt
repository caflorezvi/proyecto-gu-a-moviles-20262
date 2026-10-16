package co.edu.uniquindio.demoapp.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.demoapp.core.component.relativeTime
import co.edu.uniquindio.demoapp.domain.model.Notification

// Ícono y colores con los que se muestra cada tipo de notificación
private data class NotificationStyle(
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)

@Composable
private fun notificationStyleOf(type: String): NotificationStyle {
    val colors = MaterialTheme.colorScheme

    return when (type) {
        "comment" -> NotificationStyle(Icons.Filled.ChatBubble, colors.secondaryContainer, colors.onSecondaryContainer)
        "verified" -> NotificationStyle(Icons.Filled.Verified, colors.primaryContainer, colors.onPrimaryContainer)
        "rejected" -> NotificationStyle(Icons.Filled.Block, colors.errorContainer, colors.onErrorContainer)
        "severity" -> NotificationStyle(Icons.Filled.Warning, Color(0xFFFFDBC8), Color(0xFF7A2E00))
        "badge" -> NotificationStyle(Icons.Filled.WorkspacePremium, colors.secondaryContainer, colors.onSecondaryContainer)
        "level" -> NotificationStyle(Icons.AutoMirrored.Filled.TrendingUp, colors.tertiaryContainer, colors.onTertiaryContainer)
        "resolved" -> NotificationStyle(Icons.Filled.TaskAlt, colors.secondaryContainer, colors.onSecondaryContainer)
        else -> NotificationStyle(Icons.Filled.Notifications, colors.surfaceVariant, colors.onSurfaceVariant)
    }
}

@Composable
fun NotificationScreen(
    padding: PaddingValues,
    onNavigateToReportDetail: (String) -> Unit,
    notificationViewModel: NotificationViewModel = hiltViewModel()
){
    // Obtener la lista de notificaciones desde el ViewModel
    val notifications by notificationViewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notificaciones",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(
                onClick = { notificationViewModel.markAllAsRead() },
                // Solo tiene sentido si queda alguna notificación sin leer
                enabled = notifications.any { !it.read }
            ) {
                Text(text = "Marcar todo leído")
            }
        }

        if (notifications.isEmpty()) {
            Text(
                text = "No tienes notificaciones.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Iterar sobre la lista de notificaciones y crear un NotificationItem para cada una
            items(notifications, key = { it.id }) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        // Al abrir la notificación se marca como leída y, si tiene un reporte asociado, se navega a su detalle
                        notificationViewModel.markAsRead(notification.id)
                        notification.reportId?.let { onNavigateToReportDetail(it) }
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
){
    val style = notificationStyleOf(notification.type)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Las notificaciones sin leer se resaltan con un fondo diferente
            .background(if (notification.read) Color.Transparent else MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(style.containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = style.contentColor
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (notification.read) FontWeight.Normal else FontWeight.Medium
            )
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = relativeTime(notification.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // Punto que indica que la notificación no se ha leído
        if (!notification.read) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
