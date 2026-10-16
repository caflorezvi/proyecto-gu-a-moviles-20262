package co.edu.uniquindio.demoapp.domain.usecase.report

import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.repository.ReportRepository
import kotlinx.coroutines.flow.StateFlow
import jakarta.inject.Inject

class ObserveReportsUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    // El operador invoke permite ejecutar el caso de uso como una función: observeReportsUseCase()
    operator fun invoke(): StateFlow<List<Report>> = reportRepository.reports
}