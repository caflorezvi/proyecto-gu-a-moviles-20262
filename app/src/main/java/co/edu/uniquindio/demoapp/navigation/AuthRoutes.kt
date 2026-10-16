package co.edu.uniquindio.demoapp.navigation

import co.edu.uniquindio.demoapp.domain.model.UserRole
import kotlinx.serialization.Serializable

sealed class AuthRoutes {

    @Serializable
    data object Home : AuthRoutes()

    @Serializable
    data object Login : AuthRoutes()

    @Serializable
    data object Register : AuthRoutes()

    @Serializable
    data class Main(val role: UserRole) : AuthRoutes() // Recibe el ID del reporte como parámetro

}