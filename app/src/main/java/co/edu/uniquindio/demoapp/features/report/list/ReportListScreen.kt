package co.edu.uniquindio.demoapp.features.report.list


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import co.edu.uniquindio.demoapp.domain.model.Report

@Composable
fun ReportListScreen(
    onNavigateToDetail: (String) -> Unit, //id del reporte
    reportsViewModel: ReportListViewModel = viewModel()
){
    // Obtener la lista de reportes desde el ViewModel
    val reports by reportsViewModel.reports.collectAsState(initial = emptyList())

    // Se usa LazyColumn para mostrar la lista de reportes.
    // LazyColumn solo renderiza los elementos visibles en pantalla, mejorando el rendimiento, además integra scrolling automáticamente.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 60.dp),
    ) {
        // Iterar sobre la lista de reportes y crear un ItemReport para cada uno
        items(reports) { report ->
            ReportItem(
                report = report,
                onNavigateToDetail = onNavigateToDetail
            )
        }
    }
}

@Composable
fun ReportItem(
    report: Report,
    onNavigateToDetail: (String) -> Unit
){
    ListItem(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable {
                onNavigateToDetail(report.id)
            },
        headlineContent = {
            Text(text = report.title)
        },
        supportingContent = {
            // Mostrar el estado del reporte como contenido secundario (puede ajustarse según se desee)
            Text(text = report.description)
        },
        leadingContent = {
            // Mostrar la foto del problema reportado
            AsyncImage(
                contentScale = ContentScale.Crop,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(report.photoUrl) // URL de la imagen
                    .crossfade(true) // Efecto de desvanecimiento al cargar
                    .build(),
                contentDescription = "Foto del reporte",
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .size(80.dp)
            )
        }
    )
}
