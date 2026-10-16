package co.edu.uniquindio.demoapp.features.dashboard.navigation

import kotlinx.serialization.Serializable

sealed class DashboardRoutes {

    // --- Sección de usuarios ---
    @Serializable
    data object HomeUser : DashboardRoutes()

    @Serializable
    data class ReportDetail(val reportId : String) : DashboardRoutes()

    @Serializable
    data object CreateReport : DashboardRoutes()

    // --- Sección de administradores ---
    @Serializable
    data object HomeAdmin : DashboardRoutes()

    // --- Compartida ---
    @Serializable
    data object Profile : DashboardRoutes()


    @Serializable
    data object Notification: DashboardRoutes()
}