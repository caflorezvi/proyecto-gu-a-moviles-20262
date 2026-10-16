package co.edu.uniquindio.demoapp.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// Elementos visuales de los reportes que comparten varias pantallas (lista, detalle y perfil)

// Categorías del prototipo: cada una tiene su ícono y su par de colores (fondo / contenido)
data class ReportCategory(
    val label: String,
    val icon: ImageVector,
    val contentColor: Color,
    val containerColor: Color
)

val reportCategories = listOf(
    ReportCategory("Seguridad", Icons.Filled.LocalPolice, Color(0xFF3B4DA8), Color(0xFFDFE0FF)),
    ReportCategory("Emergencias médicas", Icons.Filled.Emergency, Color(0xFFA0294A), Color(0xFFFFD9DF)),
    ReportCategory("Infraestructura", Icons.Filled.Construction, Color(0xFF6E4E00), Color(0xFFFFDEA6)),
    ReportCategory("Mascotas", Icons.Filled.Pets, Color(0xFF7A4A1E), Color(0xFFFFDCC2)),
    ReportCategory("Comunidad", Icons.Filled.Recycling, Color(0xFF1F6B35), Color(0xFFBDF0C4))
)

// Busca la categoría de un reporte según su tipo. Si no coincide con ninguna, se usa una genérica
@Composable
fun reportCategoryOf(type: String): ReportCategory {
    return reportCategories.firstOrNull { it.label == type }
        ?: ReportCategory(
            label = type,
            icon = Icons.Filled.Category,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
}

// Texto, ícono y colores con los que se muestra cada estado de un reporte
data class ReportStatusStyle(
    val label: String,
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)

@Composable
fun reportStatusStyleOf(status: ReportStatus): ReportStatusStyle {
    val colors = MaterialTheme.colorScheme

    return when (status) {
        ReportStatus.PENDING -> ReportStatusStyle("Sin verificar", Icons.Filled.Schedule, colors.surfaceVariant, colors.onSurfaceVariant)
        ReportStatus.VERIFIED -> ReportStatusStyle("Verificado", Icons.Filled.Verified, colors.primaryContainer, colors.onPrimaryContainer)
        ReportStatus.RESOLVED -> ReportStatusStyle("Resuelto", Icons.Filled.TaskAlt, colors.primary, colors.onPrimary)
        ReportStatus.REJECTED -> ReportStatusStyle("Rechazado", Icons.Filled.Block, colors.errorContainer, colors.onErrorContainer)
        ReportStatus.DELETED -> ReportStatusStyle("Eliminado", Icons.Filled.Block, colors.errorContainer, colors.onErrorContainer)
    }
}

// La severidad depende de cuántas personas marcaron el reporte como importante
data class ReportSeverity(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val barColor: Color // Color de la barra de progreso y del marcador en el mapa
)

@Composable
fun reportSeverityOf(votes: Int): ReportSeverity {
    val colors = MaterialTheme.colorScheme

    return when {
        votes >= 30 -> ReportSeverity("Crítica", Color(0xFFBA1A1A), Color.White, Color(0xFFBA1A1A))
        votes >= 15 -> ReportSeverity("Alta", Color(0xFFFFDBC8), Color(0xFF7A2E00), Color(0xFFC25400))
        votes >= 5 -> ReportSeverity("Media", Color(0xFFFFE08A), Color(0xFF5C4300), Color(0xFFB08600))
        else -> ReportSeverity("Baja", colors.surfaceVariant, colors.onSurfaceVariant, colors.outline)
    }
}

// Chip de categoría: se usa en los filtros de la lista y en el formulario de creación
@Composable
fun CategoryChip(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    selected: Boolean,
    onClick: () -> Unit
){
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = label
            )
        },
        selected = selected,
        // Al seleccionar el chip, el ícono de la categoría se reemplaza por un check
        leadingIcon = {
            Icon(
                imageVector = if (selected) Icons.Filled.Check else icon,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize),
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else iconTint
            )
        }
    )
}

// Etiqueta en forma de píldora con ícono (opcional) y texto: se usa para categoría, estado y severidad
@Composable
fun Badge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 24.dp,
    iconSize: Dp = 14.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
){
    Row(
        modifier = modifier
            .height(height)
            .clip(CircleShape)
            .background(containerColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = contentColor
            )
        }
        Text(
            text = text,
            style = textStyle,
            color = contentColor,
            maxLines = 1
        )
    }
}

@Composable
fun StatusBadge(
    status: ReportStatus,
    showIcon: Boolean = true
){
    val style = reportStatusStyleOf(status)

    Badge(
        text = style.label,
        containerColor = style.containerColor,
        contentColor = style.contentColor,
        icon = if (showIcon) style.icon else null
    )
}

// Barra de progreso redondeada. El progreso va de 0 a 1
@Composable
fun ProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(CircleShape)
                .background(color)
        )
    }
}

// Iniciales de un nombre: "Laura Mejía" -> "LM"
fun initialsOf(name: String): String {
    return name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}

// Convierte una fecha en un texto relativo: "Hoy", "Ayer", "Hace 3 días"...
fun relativeDate(date: LocalDate): String {
    val days = ChronoUnit.DAYS.between(date, LocalDate.now())

    return when {
        days <= 0 -> "Hoy"
        days == 1L -> "Ayer"
        days < 7 -> "Hace $days días"
        days < 14 -> "Hace 1 semana"
        days < 30 -> "Hace ${days / 7} semanas"
        else -> "Hace ${days / 30} mes(es)"
    }
}

// Igual que relativeDate, pero con minutos y horas: "Hace 10 min", "Hace 4 h", "Ayer"...
fun relativeTime(dateTime: LocalDateTime): String {
    val minutes = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now())

    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "Hace $minutes min"
        minutes < 60 * 24 -> "Hace ${minutes / 60} h"
        else -> relativeDate(dateTime.toLocalDate())
    }
}
