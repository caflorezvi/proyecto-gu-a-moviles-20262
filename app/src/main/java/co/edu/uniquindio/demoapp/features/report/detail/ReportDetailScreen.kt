package co.edu.uniquindio.demoapp.features.report.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import co.edu.uniquindio.demoapp.core.component.Badge
import co.edu.uniquindio.demoapp.core.component.ProgressBar
import co.edu.uniquindio.demoapp.core.component.initialsOf
import co.edu.uniquindio.demoapp.core.component.relativeDate
import co.edu.uniquindio.demoapp.core.component.reportCategoryOf
import co.edu.uniquindio.demoapp.core.component.reportSeverityOf
import co.edu.uniquindio.demoapp.core.component.reportStatusStyleOf
import co.edu.uniquindio.demoapp.core.util.RequestResult
import co.edu.uniquindio.demoapp.domain.model.Location
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import co.edu.uniquindio.demoapp.domain.model.User
import java.util.Locale


@Composable
fun ReportDetailScreen(
    padding: PaddingValues = PaddingValues(),
    reportId: String,
    onNavigateBack: () -> Unit = {}, // Función para regresar a la pantalla anterior
    reportViewModel: ReportDetailViewModel = hiltViewModel(),
){
    // Se observa el estado completo de la pantalla desde el ViewModel
    val state by reportViewModel.uiState.collectAsState()

    // Se pide el reporte al entrar a la pantalla y cada vez que cambie el reportId
    LaunchedEffect(reportId) {
        reportViewModel.findById(reportId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            // Sube el contenido cuando aparece el teclado, sin volver a sumar el espacio de las barras
            .consumeWindowInsets(padding)
            .imePadding()
    ) {

        TopBar(onNavigateBack = onNavigateBack)

        when (val result = state.result) {
            // Todavía no hay respuesta, o la búsqueda está en curso
            null, RequestResult.Loading -> CenteredContent {
                CircularProgressIndicator()
            }

            // El reporte no existe (o, más adelante, hubo un error al buscarlo)
            is RequestResult.Failure -> CenteredContent {
                Text(text = result.errorMessage)
            }

            // El ViewModel publica Success junto con el reporte, así que aquí ya está disponible
            is RequestResult.Success -> state.report?.let { report ->
                ReportDetailContent(
                    report = report,
                    author = state.author,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TopBar(onNavigateBack: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onNavigateBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar"
            )
        }
        Text(
            text = "Reporte",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun CenteredContent(content: @Composable () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ReportDetailContent(
    report: Report,
    author: User?,
    modifier: Modifier = Modifier
){
    // El modelo Report aún no tiene votos ni comentarios: por ahora son estado local de la pantalla
    var voted by rememberSaveable { mutableStateOf(false) }
    var comments by remember { mutableStateOf(emptyList<String>()) }
    var comment by rememberSaveable { mutableStateOf("") }

    val votes = if (voted) 1 else 0
    val category = reportCategoryOf(report.type)
    val status = reportStatusStyleOf(report.status)
    val severity = reportSeverityOf(votes)

    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            // Categoría y estado del reporte
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Badge(
                    text = category.label,
                    containerColor = category.containerColor,
                    contentColor = category.contentColor,
                    icon = category.icon,
                    height = 32.dp,
                    iconSize = 18.dp,
                    textStyle = MaterialTheme.typography.labelLarge
                )
                Badge(
                    text = status.label,
                    containerColor = status.containerColor,
                    contentColor = status.contentColor,
                    icon = status.icon,
                    height = 32.dp,
                    iconSize = 18.dp,
                    textStyle = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = report.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Author(
                name = author?.name ?: "Usuario desconocido",
                date = relativeDate(report.date)
            )

            if (report.status == ReportStatus.REJECTED) {
                Banner(
                    text = "Rechazado por un moderador",
                    icon = Icons.Filled.Block,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            if (report.status == ReportStatus.RESOLVED) {
                Banner(
                    text = "Este reporte fue marcado como resuelto.",
                    icon = Icons.Filled.TaskAlt,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            SeverityCard(
                label = "Severidad ${severity.label}",
                barColor = severity.barColor,
                votes = votes,
                voted = voted,
                onVote = { voted = !voted }
            )

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium
            )

            LocationCard(
                location = report.location,
                pinColor = severity.barColor
            )

            Text(
                text = "Comentarios (${comments.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (comments.isEmpty()) {
                Text(
                    text = "Aún no hay comentarios. Sé el primero en aportar información.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            comments.forEach { message ->
                CommentItem(message = message)
            }
        }

        CommentBar(
            comment = comment,
            onCommentChange = { comment = it },
            onSend = {
                if (comment.isNotBlank()) {
                    comments = comments + comment.trim()
                    comment = ""
                }
            }
        )
    }
}

@Composable
private fun Author(
    name: String,
    date: String
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initialsOf(name),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Aviso de ancho completo con ícono y texto (reporte rechazado o resuelto)
@Composable
private fun Banner(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = contentColor
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}

@Composable
private fun SeverityCard(
    label: String,
    barColor: Color,
    votes: Int,
    voted: Boolean,
    onVote: () -> Unit
){
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "$votes votos «Es importante»",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // La barra se llena a medida que el reporte recibe votos (40 votos = barra completa)
            ProgressBar(
                progress = (votes / 40f).coerceAtLeast(0.03f),
                color = barColor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Baja", "Media · 5", "Alta · 15", "Crítica · 30").forEach { level ->
                    Text(
                        text = level,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Button(
                onClick = onVote,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (voted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (voted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = if (voted) Icons.Filled.Campaign else Icons.Outlined.Campaign,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (voted) "Marcaste «Es importante»" else "Es importante",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LocationCard(
    location: Location,
    pinColor: Color
){
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        // Espacio reservado para el mapa: por ahora solo muestra el marcador
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = pinColor
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column {
                Text(
                    text = "Ubicación del reporte",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = String.format(Locale.US, "%.4f, %.4f", location.latitude, location.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CommentItem(message: String){
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TÚ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Tú · Ahora",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Barra fija en la parte inferior para escribir un comentario
@Composable
private fun CommentBar(
    comment: String,
    onCommentChange: (String) -> Unit,
    onSend: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(text = "Aporta información…")
            },
            shape = CircleShape,
            maxLines = 3
        )
        FilledIconButton(
            onClick = onSend,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar comentario",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
