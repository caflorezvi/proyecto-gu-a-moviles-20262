package co.edu.uniquindio.demoapp.core.navigation

import androidx.compose.runtime.Composable
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
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home
    ){

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
            ReportListScreen(
                onNavigateToDetail = { idReport ->
                    navController.navigate(MainRoutes.ReportDetail(idReport))
                }
            )
        }

        composable<MainRoutes.ReportDetail> {
            val args = it.toRoute<MainRoutes.ReportDetail>()
            ReportDetailScreen(
                args.idReport
            )
        }

    }

}