package co.edu.uniquindio.demoapp.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object Home : MainRoutes()

    @Serializable
    data object Login : MainRoutes()

    @Serializable
    data object Register : MainRoutes()

    @Serializable
    data object ReportList : MainRoutes()

    @Serializable
    data class ReportDetail(val reportId: String) : MainRoutes() // Recibe el ID del reporte como parámetro

}