package co.edu.uniquindio.demoapp.features.report.list

import androidx.lifecycle.ViewModel
import co.edu.uniquindio.demoapp.domain.model.Location
import co.edu.uniquindio.demoapp.domain.model.Report
import co.edu.uniquindio.demoapp.domain.model.ReportStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import co.edu.uniquindio.demoapp.domain.usecase.report.ObserveReportsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel // Anotamos el ViewModel con @HiltViewModel para que Hilt pueda inyectarlo
class ReportListViewModel @Inject constructor(
    observeReportsUseCase: ObserveReportsUseCase
) : ViewModel() {

    val reports: StateFlow<List<Report>> = observeReportsUseCase()

}