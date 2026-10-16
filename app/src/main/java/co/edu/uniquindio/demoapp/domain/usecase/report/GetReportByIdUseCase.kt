package co.edu.uniquindio.demoapp.domain.usecase.report

import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.repository.ReportRepository
import jakarta.inject.Inject

class GetReportByIdUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    // Es suspend porque el repositorio expone findById como una operación suspend
    suspend operator fun invoke(reportId: String): Report? = reportRepository.findById(reportId)
}