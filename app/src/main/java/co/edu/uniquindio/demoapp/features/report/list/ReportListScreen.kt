package co.edu.uniquindio.demoapp.features.report.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import co.edu.uniquindio.demoapp.core.component.Badge
import co.edu.uniquindio.demoapp.core.component.CategoryChip
import co.edu.uniquindio.demoapp.core.component.StatusBadge
import co.edu.uniquindio.demoapp.core.component.relativeDate
import co.edu.uniquindio.demoapp.core.component.reportCategories
import co.edu.uniquindio.demoapp.core.component.reportCategoryOf
import co.edu.uniquindio.demoapp.core.component.reportSeverityOf
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus

@Composable
fun ReportListScreen(
    onNavigateToReportDetail: (String) -> Unit, // Función para navegar al detalle del reporte (recibe el ID del reporte)
    onNavigateToCreateReport: () -> Unit,
    padding: PaddingValues = PaddingValues(), // Espacio que ocupan las barras del Scaffold
    reportsViewModel: ReportListViewModel = hiltViewModel()
){
    // Obtener la lista de reportes desde el ViewModel
    val reports by reportsViewModel.reports.collectAsState(initial = emptyList())

    // Categoría seleccionada en los filtros (null = "Todas")
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Reportes marcados como "Es importante". Por ahora es estado local de la pantalla:
    // el modelo Report aún no tiene votos
    var votedIds by remember { mutableStateOf(emptySet<String>()) }

    // El feed no muestra reportes rechazados ni eliminados, y deja los resueltos al final
    val feed = remember(reports, selectedCategory) {
        reports
            .filter { it.status != ReportStatus.REJECTED && it.status != ReportStatus.DELETED }
            .filter { selectedCategory == null || it.type == selectedCategory }
            .sortedBy { it.status == ReportStatus.RESOLVED }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {

        Title()

        Filters(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // Se usa LazyColumn para mostrar la lista de reportes.
        // LazyColumn solo renderiza los elementos visibles en pantalla, mejorando el rendimiento, además integra scrolling automáticamente.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // El padding inferior evita que el botón "Reportar" tape la última tarjeta
            contentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FeedCount(count = feed.size)
            }

            // Iterar sobre la lista de reportes y crear un ReportItem para cada uno
            items(feed, key = { it.id }) { report ->
                ReportItem(
                    report = report,
                    onNavigateToReportDetail = onNavigateToReportDetail,
                    voted = report.id in votedIds,
                    onVote = {
                        votedIds = if (report.id in votedIds) votedIds - report.id else votedIds + report.id
                    }
                )
            }

            if (feed.isEmpty()) {
                item {
                    Text(
                        text = "No hay reportes con estos filtros.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 48.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

    }

}

@Composable
fun Title(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Alerta Vecinal",
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Calarcá, Quindío",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Ver mapa",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

@Composable
fun Filters(
    selectedCategory: String?, // Categoría seleccionada (null = "Todas")
    onCategorySelected: (String?) -> Unit
){
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Cerca de mí", "Mi barrio", "Ciudad")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp)
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex,
                    label = {
                        Text(
                            text = label,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    label = "Todas",
                    icon = Icons.Filled.Apps,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) }
                )
            }

            items(reportCategories) { category ->
                CategoryChip(
                    label = category.label,
                    icon = category.icon,
                    iconTint = category.contentColor,
                    selected = selectedCategory == category.label,
                    onClick = { onCategorySelected(category.label) }
                )
            }
        }
    }


}

@Composable
private fun FeedCount(count: Int){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = if (count == 1) "1 reporte ·" else "$count reportes ·",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.Filled.SwapVert,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "por relevancia",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ReportItem(
    report: Report,
    onNavigateToReportDetail: (String) -> Unit, // Función para navegar al detalle del reporte (recibe el ID del reporte)
    voted: Boolean = false,
    onVote: () -> Unit = {}
){
    val category = reportCategoryOf(report.type)

    // El modelo Report aún no tiene votos ni comentarios: por ahora solo cuenta el voto local
    val votes = if (voted) 1 else 0
    val comments = 0
    val severity = reportSeverityOf(votes)

    ElevatedCard(
        // Se avisa el ID del reporte seleccionado; la navegación se resuelve en AppNavigation
        onClick = { onNavigateToReportDetail(report.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        // Mostrar la foto del problema reportado
        AsyncImage(
            contentScale = ContentScale.Crop,
            model = ImageRequest.Builder(LocalContext.current)
                .data(report.photoUrl) // URL de la imagen
                .crossfade(true) // Efecto de desvanecimiento al cargar
                .build(),
            contentDescription = "Foto del reporte",
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Column(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Categoría, fecha y estado del reporte
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(category.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = category.contentColor
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = category.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = category.contentColor,
                        letterSpacing = 0.9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = relativeDate(report.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = report.status)
            }

            Text(
                text = report.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Severidad, comentarios y botón "Es importante"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Badge(
                    text = severity.label,
                    containerColor = severity.containerColor,
                    contentColor = severity.contentColor,
                    icon = Icons.Filled.Warning,
                    height = 28.dp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comentarios",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$comments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    onClick = onVote,
                    shape = CircleShape,
                    color = if (voted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (voted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .height(32.dp)
                            .padding(start = 10.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (voted) Icons.Filled.Campaign else Icons.Outlined.Campaign,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Es importante · $votes",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
