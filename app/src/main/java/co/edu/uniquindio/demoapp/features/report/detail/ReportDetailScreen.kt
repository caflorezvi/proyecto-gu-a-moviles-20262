package co.edu.uniquindio.demoapp.features.report.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ReportDetailScreen(
    reportId: String, // Recibe el ID del reporte como parámetro
    padding: PaddingValues = PaddingValues() // Espaciado que más adelante recibirá del Scaffold (barras superior/inferior)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), // Aplica el espaciado del Scaffold para no quedar tapado por las barras
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Reporte $reportId")
    }
}
