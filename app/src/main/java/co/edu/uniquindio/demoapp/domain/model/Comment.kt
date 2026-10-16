package co.edu.uniquindio.demoapp.domain.model

import java.time.LocalDateTime

data class Comment (
    val id: String,
    val message: String,
    val userId: String,
    val date: LocalDateTime
)