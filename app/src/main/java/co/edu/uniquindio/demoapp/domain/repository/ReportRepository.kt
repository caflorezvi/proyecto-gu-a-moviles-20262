package co.edu.uniquindio.demoapp.domain.repository

import co.edu.uniquindio.demoapp.domain.model.Report
import kotlinx.coroutines.flow.StateFlow

interface ReportRepository {
    val reports: StateFlow<List<Report>>
    suspend fun save(report: Report)
    suspend fun findById(id: String): Report?
}