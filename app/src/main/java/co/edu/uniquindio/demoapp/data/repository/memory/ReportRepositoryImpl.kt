package co.edu.uniquindio.demoapp.data.repository.memory

import co.edu.uniquindio.demoapp.domain.model.Location
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import co.edu.uniquindio.demoapp.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton // Anotamos la clase como Singleton para que Hilt gestione una única instancia
class ReportRepositoryImpl @Inject constructor(): ReportRepository { // Implementamos la interfaz ReportRepository

    // Usamos StateFlow para manejar la lista de reportes de manera reactiva
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    override val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    init {
        _reports.value = fetchReports()
    }

    override suspend fun save(report: Report){
        if(!report.title.contains("mazamorra")){
            _reports.value += report
        }else{
            throw Exception("Su reporte no puede contener la palabra \"mazamorra\"")
        }
    }

    override suspend fun findById(id: String): Report? {
        return _reports.value.firstOrNull { it.id == id }
    }

    private fun fetchReports(): List<Report> {
        // Mueva aquí la lista de reportes de ejemplo que tenía el ReportListViewModel
        // (guía de entidades); solo cambia que ahora se retorna en lugar de asignarse
        return listOf(
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
    }
}