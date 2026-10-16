package co.edu.uniquindio.demoapp.domain.usecase.report

import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.repository.ReportRepository
import jakarta.inject.Inject

class CreateReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(report: Report) {
        reportRepository.save(report)
    }
}