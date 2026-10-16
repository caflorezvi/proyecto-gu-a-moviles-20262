package co.edu.uniquindio.demoapp.features.dashboard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import co.edu.uniquindio.demoapp.domain.model.UserRole

enum class DashboardDestination(
    val route: DashboardRoutes,
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val roles: Set<UserRole> // Roles que ven este item en la barra inferior
) {
    HOME_USER(DashboardRoutes.HomeUser, "Home", Icons.Outlined.Home, Icons.Filled.Home,setOf(UserRole.USER)),
    HOME_ADMIN(DashboardRoutes.HomeAdmin, "Home", Icons.Outlined.Home, Icons.Filled.Home,setOf(UserRole.ADMIN)),
    NOTIFICATION(DashboardRoutes.Notification, "Notificaciones", Icons.Outlined.Notifications, Icons.Filled.Notifications,setOf(UserRole.USER)),
    PROFILE(DashboardRoutes.Profile, "Perfil", Icons.Outlined.Person, Icons.Filled.Person,UserRole.entries.toSet())
}