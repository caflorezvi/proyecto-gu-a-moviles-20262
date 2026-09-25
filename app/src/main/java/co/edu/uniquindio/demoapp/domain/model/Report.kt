package co.edu.uniquindio.demoapp.domain.model

import java.time.LocalDate

data class Report(
    val id: String,
    val title: String,
    val description: String,
    val location: Location,
    val status: ReportStatus,
    val type: String,
    val photoUrl: String,
    val ownerId: String,
    val date: LocalDate
)