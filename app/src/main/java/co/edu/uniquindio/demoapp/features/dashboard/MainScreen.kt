package co.edu.uniquindio.demoapp.features.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.demoapp.domain.model.UserRole
import co.edu.uniquindio.demoapp.features.dashboard.component.BottomNavigationBar
import co.edu.uniquindio.demoapp.features.dashboard.navigation.DashboardDestination
import co.edu.uniquindio.demoapp.features.dashboard.navigation.DashboardNavigation
import co.edu.uniquindio.demoapp.features.dashboard.navigation.DashboardRoutes

@Composable
fun MainScreen(
    role: UserRole
) {
    val navController = rememberNavController()

    // Items de la barra inferior: solo los que corresponden al rol del usuario
    val items = DashboardDestination.entries.filter { role in it.roles }

    // Estado de los Snackbars, compartido por todas las pantallas del dashboard
    val snackbarHostState = remember { SnackbarHostState() }

    // El botón "Reportar" solo se muestra en la lista de reportes, para que no tape el contenido de las demás pantallas
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val showFab = navBackStackEntry?.destination?.hasRoute<DashboardRoutes.HomeUser>() == true

    // La barra inferior solo se muestra en las pantallas principales (las de sus propios items).
    // En las secundarias, como el detalle o la creación de un reporte, se oculta
    val showBottomBar = items.any { navBackStackEntry?.destination?.hasRoute(it.route::class) == true }

    // Estructura Scaffold (barra superior, barra inferior y contenido)
    Scaffold(
        floatingActionButton = {
            if (showFab) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(DashboardRoutes.CreateReport)
                    },
                    text = {
                        Text(
                            text = "Reportar",
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Reportar"
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    items = items,
                    navController = navController
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) // Los Snackbars aparecen encima de la barra inferior
        }
    ) { padding ->
        // Grafo interno del dashboard: el rol define la pantalla de inicio
        DashboardNavigation(
            navController = navController,
            padding = padding,
            role = role,
            snackbarHostState = snackbarHostState,
        )
    }
}