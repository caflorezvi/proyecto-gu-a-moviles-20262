package co.edu.uniquindio.demoapp.features.dashboard.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import co.edu.uniquindio.demoapp.domain.model.UserRole
import co.edu.uniquindio.demoapp.features.notifications.NotificationScreen
import co.edu.uniquindio.demoapp.features.report.create.CreateReportScreen
import co.edu.uniquindio.demoapp.features.report.detail.ReportDetailScreen
import co.edu.uniquindio.demoapp.features.report.list.ReportListScreen
import co.edu.uniquindio.demoapp.features.user.profile.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardNavigation(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    padding: PaddingValues,
    role: UserRole
){

    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        // Cada rol arranca en su propia pantalla de inicio
        startDestination = when (role) {
            UserRole.USER -> DashboardRoutes.HomeUser
            UserRole.ADMIN -> DashboardRoutes.HomeAdmin
        }
    ) {

        // --- Pantallas de usuarios ---

        composable<DashboardRoutes.HomeUser> {
            // La pantalla principal de la sección de usuarios que muestra la lista de reportes
            ReportListScreen(
                padding = padding, // Se pasa el padding del Scaffold para evitar solapamientos
                onNavigateToReportDetail = {
                    navController.navigate(DashboardRoutes.ReportDetail(it))
                },
                onNavigateToCreateReport = {
                    navController.navigate(DashboardRoutes.CreateReport)
                }
            )
        }

        composable<DashboardRoutes.Notification> {
            NotificationScreen(
                padding = padding, // Se pasa el padding del Scaffold para evitar solapamientos
                onNavigateToReportDetail = {
                    navController.navigate(DashboardRoutes.ReportDetail(it))
                }
            ) // Debe crear este composable en el paquete user/search
        }

        composable<DashboardRoutes.ReportDetail> {
            val args = it.toRoute<DashboardRoutes.ReportDetail>()
            ReportDetailScreen(
                padding = padding, // Se pasa el padding del Scaffold para evitar solapamientos
                reportId = args.reportId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<DashboardRoutes.CreateReport> {
            CreateReportScreen(
                padding = padding, // Se pasa el padding del Scaffold para evitar solapamientos
                onNavigateBack = {
                    navController.popBackStack()
                },
                onReportCreated = { reportId, message ->
                    // Al crear el reporte se abre su detalle. Se quita el formulario de la pila,
                    // para que el botón atrás regrese a la lista y no al formulario
                    navController.navigate(DashboardRoutes.ReportDetail(reportId)) {
                        popUpTo<DashboardRoutes.CreateReport> { inclusive = true }
                    }
                    // El Snackbar se lanza desde aquí porque la pantalla de creación ya salió de la composición
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }

        // --- Pantallas de administradores ---

        composable<DashboardRoutes.HomeAdmin> {
            // Pantalla principal del administrador. Créela según las necesidades de su proyecto.
            Box(modifier = Modifier.padding(padding)) {
                Text(text = "Inicio administrador")
            }
        }

        // --- Pantallas compartidas ---

        composable<DashboardRoutes.Profile> {
            ProfileScreen(
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateToReportDetail = {
                    navController.navigate(DashboardRoutes.ReportDetail(it))
                }
            )
        }
    }

}