package co.edu.uniquindio.demoapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.edu.uniquindio.demoapp.features.home.HomeScreen
import co.edu.uniquindio.demoapp.features.login.LoginScreen
import co.edu.uniquindio.demoapp.features.register.RegisterScreen
import co.edu.uniquindio.demoapp.features.report.detail.ReportDetailScreen
import co.edu.uniquindio.demoapp.features.report.list.ReportListScreen

@Composable
fun AppNavigation() {
    // Estado de la navegación, permite controlar la navegación entre pantallas
    val navController = rememberNavController()

    // Un Surface que ocupa toda la pantalla y se adapta al tema de la aplicación
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController, // Controlador de navegación
            startDestination = MainRoutes.Home // Primera pantalla que se muestra al iniciar la aplicación
        ) {

            composable<MainRoutes.Home> {
                HomeScreen(
                    onNavigateToLogin = {
                        navController.navigate(MainRoutes.Login)
                    },
                    onNavigateToRegister = {
                        navController.navigate(MainRoutes.Register)
                    }
                )
            }

            composable<MainRoutes.Login> {
                LoginScreen(
                    onNavigateToReports = {
                        navController.navigate(MainRoutes.ReportList)
                    }
                )
            }

            composable<MainRoutes.Register> {
                RegisterScreen(
                    onNavigateToBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<MainRoutes.ReportList> {
                // Se pasa la función de navegación a la pantalla de la lista de reportes
                ReportListScreen(
                    onNavigateToReportDetail = { reportId ->
                        navController.navigate(MainRoutes.ReportDetail(reportId))
                    }
                )
            }

            composable<MainRoutes.ReportDetail> {
                // Se obtienen los argumentos de la ruta
                val args = it.toRoute<MainRoutes.ReportDetail>()
                // Se pasa el ID del reporte a la pantalla de detalles del reporte
                ReportDetailScreen(
                    reportId = args.reportId
                )
            }

        }
    }
}
