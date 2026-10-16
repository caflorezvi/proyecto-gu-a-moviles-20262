package co.edu.uniquindio.demoapp.features.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.demoapp.core.component.Badge
import co.edu.uniquindio.demoapp.core.component.ConfirmAlertDialog
import co.edu.uniquindio.demoapp.core.component.ProgressBar
import co.edu.uniquindio.demoapp.core.component.StatusBadge
import co.edu.uniquindio.demoapp.core.component.initialsOf
import co.edu.uniquindio.demoapp.core.component.reportCategoryOf
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import co.edu.uniquindio.demoapp.domain.model.UserRole
import kotlinx.coroutines.launch

// Niveles de reputación del prototipo: cada uno se alcanza con un mínimo de puntos
private data class Level(
    val name: String,
    val minPoints: Int,
    val icon: ImageVector
)

private val levels = listOf(
    Level("Novato", 0, Icons.Filled.Eco),
    Level("Colaborador", 100, Icons.Filled.Handshake),
    Level("Guardián", 300, Icons.Filled.Shield),
    Level("Héroe Comunitario", 700, Icons.Filled.MilitaryTech)
)

private val pointRules = listOf(
    "Crear un reporte" to "+10",
    "Reporte verificado por moderador" to "+20",
    "Comentar un reporte" to "+2",
    "Recibir un voto «Es importante»" to "+1",
    "Reporte marcado como resuelto" to "+5"
)

// Tarjeta de estadística: un número grande con su descripción
private data class Stat(
    val label: String,
    val value: Int,
    val containerColor: Color,
    val contentColor: Color
)

private data class Achievement(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val earned: Boolean
)

@Composable
fun ProfileScreen(
    padding: PaddingValues, // Espacio que ocupan las barras del Scaffold de MainScreen
    snackbarHostState: SnackbarHostState, // Estado de los Snackbars del Scaffold de MainScreen
    onNavigateToReportDetail: (String) -> Unit = {}, // Función para navegar al detalle de uno de mis reportes
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    // Se observa el estado completo de la pantalla desde el ViewModel
    val state by profileViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Mientras se carga el usuario no hay nada que mostrar
    val user = state.user ?: return
    val isAdmin = user.role == UserRole.ADMIN

    // El nivel actual es el más alto cuyo mínimo de puntos ya se alcanzó
    val levelIndex = levels.indexOfLast { state.points >= it.minPoints }
    val level = levels[levelIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Header(
            name = user.name,
            email = user.email,
            roleLabel = if (isAdmin) "Moderador" else level.name,
            roleIcon = if (isAdmin) Icons.Filled.Gavel else level.icon
        )

        if (isAdmin) {
            // El moderador ve el total de reportes de la comunidad por estado
            ModeratorStats(reports = state.allReports)
        } else {
            ReputationCard(
                points = state.points,
                levelIndex = levelIndex
            )

            MyStats(reports = state.myReports)

            MyReports(
                reports = state.myReports,
                onNavigateToReportDetail = onNavigateToReportDetail
            )

            Achievements(reports = state.myReports)

            PointRules()
        }

        Options(
            isAdmin = isAdmin,
            onEditProfile = {
                // La pantalla de edición de datos personales aún no existe
                scope.launch {
                    snackbarHostState.showSnackbar("La edición de datos personales aún no está disponible")
                }
            },
            onLogout = { profileViewModel.logout() },
            onDeleteAccount = { showDeleteDialog = true }
        )
    }

    if (showDeleteDialog) {
        ConfirmAlertDialog(
            title = "¿Eliminar tu cuenta?",
            message = "Se borrarán tus datos y tus reportes. Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            onConfirm = {
                showDeleteDialog = false
                // La eliminación de la cuenta aún no está implementada en el repositorio
                scope.launch {
                    snackbarHostState.showSnackbar("La eliminación de la cuenta aún no está disponible")
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

}

@Composable
private fun Header(
    name: String,
    email: String,
    roleLabel: String,
    roleIcon: ImageVector
){
    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar con las iniciales del usuario
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initialsOf(name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Badge(
                text = roleLabel,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 6.dp),
                icon = roleIcon,
                iconSize = 16.dp
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String){
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun ReputationCard(
    points: Int,
    levelIndex: Int
){
    val level = levels[levelIndex]
    val nextLevel = levels.getOrNull(levelIndex + 1)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(text = "Reputación")

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$points",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            // Avance dentro del nivel actual, hacia el siguiente
            ProgressBar(
                progress = if (nextLevel != null) {
                    (points - level.minPoints).toFloat() / (nextLevel.minPoints - level.minPoints)
                } else {
                    1f
                },
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (nextLevel != null) {
                    "Nivel ${level.name} · faltan ${nextLevel.minPoints - points} pts para ${nextLevel.name}"
                } else {
                    "Alcanzaste el nivel máximo"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                levels.forEachIndexed { index, item ->
                    LevelStep(
                        level = item,
                        reached = index <= levelIndex,
                        current = index == levelIndex,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelStep(
    level: Level,
    reached: Boolean, // El usuario ya alcanzó este nivel
    current: Boolean, // Es el nivel actual del usuario
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // El nivel actual se resalta con un anillo alrededor del ícono
        Box(
            modifier = Modifier
                .size(46.dp)
                .border(
                    width = 3.dp,
                    color = if (current) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = level.icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = if (reached) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
            )
        }
        Text(
            text = level.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (current) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${level.minPoints} pts",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun MyStats(reports: List<Report>){
    val colors = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitle(text = "Mis estadísticas")

        StatsRow(
            stats = listOf(
                Stat("Activas", reports.count { it.status == ReportStatus.VERIFIED }, colors.primaryContainer, colors.onPrimaryContainer),
                Stat("Finalizadas", reports.count { it.status == ReportStatus.RESOLVED }, colors.secondaryContainer, colors.onSecondaryContainer),
                Stat("Pendientes de verificación", reports.count { it.status == ReportStatus.PENDING }, colors.surfaceVariant, colors.onSurfaceVariant)
            )
        )
    }
}

@Composable
private fun ModeratorStats(reports: List<Report>){
    val colors = MaterialTheme.colorScheme

    StatsRow(
        stats = listOf(
            Stat("Pendientes", reports.count { it.status == ReportStatus.PENDING }, colors.surfaceVariant, colors.onSurfaceVariant),
            Stat("Verificados", reports.count { it.status == ReportStatus.VERIFIED }, colors.primaryContainer, colors.onPrimaryContainer),
            Stat("Rechazados", reports.count { it.status == ReportStatus.REJECTED }, colors.errorContainer, colors.onErrorContainer)
        )
    )
}

@Composable
private fun StatsRow(stats: List<Stat>){
    Row(
        // IntrinsicSize.Min hace que las tarjetas tengan la misma altura
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(stat.containerColor)
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${stat.value}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = stat.contentColor
                )
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = stat.contentColor
                )
            }
        }
    }
}

@Composable
private fun MyReports(
    reports: List<Report>,
    onNavigateToReportDetail: (String) -> Unit
){
    Column {
        Box(modifier = Modifier.padding(bottom = 6.dp)) {
            SectionTitle(text = "Mis reportes")
        }

        if (reports.isEmpty()) {
            Text(
                text = "Aún no has creado reportes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        reports.forEach { report ->
            val category = reportCategoryOf(report.type)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToReportDetail(report.id) }
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(category.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = category.contentColor
                    )
                }
                Text(
                    text = report.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                StatusBadge(
                    status = report.status,
                    showIcon = false
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
}

@Composable
private fun Achievements(reports: List<Report>){
    val verifiedCount = reports.count { it.status == ReportStatus.VERIFIED || it.status == ReportStatus.RESOLVED }

    // Las insignias que dependen de comentarios y votos quedan bloqueadas: el modelo aún no tiene esos datos
    val achievements = listOf(
        Achievement("Primer reporte", if (reports.isNotEmpty()) "Obtenida" else "Crea un reporte", Icons.Filled.Flag, reports.isNotEmpty()),
        Achievement("Vecino activo", "10 comentarios", Icons.Filled.Forum, false),
        Achievement("Voz de la comunidad", "Reporte con 25+ votos", Icons.Filled.Campaign, false),
        Achievement("10 verificados", "$verifiedCount / 10", Icons.Filled.Verified, verifiedCount >= 10),
        Achievement("Reporte del mes", "El más votado del mes", Icons.Filled.MilitaryTech, false),
        Achievement("Rescatista", "Mascota encontrada", Icons.Filled.Pets, false)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionTitle(text = "Insignias")

        // Cuadrícula de 3 columnas
        achievements.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { achievement ->
                    AchievementItem(
                        achievement = achievement,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(
    achievement: Achievement,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            // Las insignias que aún no se obtienen se muestran atenuadas
            .alpha(if (achievement.earned) 1f else 0.6f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 8.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (achievement.earned) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = if (achievement.earned) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.outline
            )
        }
        Text(
            text = achievement.name,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = achievement.description,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PointRules(){
    Column {
        Box(modifier = Modifier.padding(bottom = 6.dp)) {
            SectionTitle(text = "Cómo ganar puntos")
        }

        pointRules.forEach { (label, points) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = points,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun Options(
    isAdmin: Boolean,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        // El moderador solo puede cerrar sesión
        if (!isAdmin) {
            OptionItem(
                text = "Editar datos personales",
                icon = Icons.Filled.ManageAccounts,
                onClick = onEditProfile,
                showChevron = true
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        }

        OptionItem(
            text = "Cerrar sesión",
            icon = Icons.AutoMirrored.Filled.Logout,
            onClick = onLogout
        )

        if (!isAdmin) {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            OptionItem(
                text = "Eliminar cuenta",
                icon = Icons.Filled.DeleteForever,
                onClick = onDeleteAccount,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun OptionItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color? = null, // Color del ícono y el texto (por defecto, los del tema)
    showChevron: Boolean = false
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color ?: MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = color ?: MaterialTheme.colorScheme.onSurface
        )
        if (showChevron) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
