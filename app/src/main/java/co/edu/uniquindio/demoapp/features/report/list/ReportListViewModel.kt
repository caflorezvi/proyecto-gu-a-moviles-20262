package co.edu.uniquindio.demoapp.features.report.list

import androidx.lifecycle.ViewModel
import co.edu.uniquindio.demoapp.domain.model.Location
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class ReportListViewModel: ViewModel() {
    // Patrón de StateFlow para manejar el estado de la lista de reportes
    private val _reports = MutableStateFlow(emptyList<Report>())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    // Inicializar con algunos datos de ejemplo
    init {
        fetchReports()
    }

    // Función para obtener un reporte por su ID
    fun findById(id: String): Report? {
        return _reports.value.find { it.id == id }
    }

    // Función para agregar un nuevo reporte a la lista
    fun save(report: Report) {
        _reports.value += report
    }

    // Función para simular algunos datos de reportes
    private fun fetchReports() {
        val reports = listOf(
            Report(
                id = "1",
                title = "Hueco en la vía",
                description = "Hueco grande en plena calzada, los vehículos deben esquivarlo",
                location = Location(latitude = 4.5339, longitude = -75.6811),
                status = ReportStatus.PENDING,
                type = "Infraestructura",
                photoUrl = "https://picsum.photos/200?random=1",
                ownerId = "1",
                date = LocalDate.now()
            ),
            Report(
                id = "2",
                title = "Alumbrado dañado",
                description = "Poste de luz sin funcionar desde hace una semana",
                location = Location(latitude = 4.5402, longitude = -75.6658),
                status = ReportStatus.VERIFIED,
                type = "Alumbrado",
                photoUrl = "https://picsum.photos/200?random=2",
                ownerId = "2",
                date = LocalDate.now().minusDays(3)
            ),
            Report(
                id = "3",
                title = "Basura acumulada",
                description = "Acumulación de basura en la esquina del parque",
                location = Location(latitude = 4.5475, longitude = -75.6585),
                status = ReportStatus.RESOLVED,
                type = "Basura",
                photoUrl = "https://picsum.photos/200?random=3",
                ownerId = "1",
                date = LocalDate.now().minusWeeks(1)
            )
        )
        _reports.value = reports
    }
}