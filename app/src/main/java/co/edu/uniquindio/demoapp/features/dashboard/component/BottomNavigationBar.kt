package co.edu.uniquindio.demoapp.features.dashboard.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import co.edu.uniquindio.demoapp.features.dashboard.navigation.DashboardDestination

@Composable
fun BottomNavigationBar(
    items: List<DashboardDestination>, // Items de navegación según el rol
    navController: NavHostController
){
    // Obtener la entrada actual de la pila de navegación
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Crear la barra de navegación inferior
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
    ){
        // Iteramos cada item recibido
        items.forEach { destination ->

            // Verificar si el item está seleccionado
            val isSelected = currentDestination?.route == destination.route::class.qualifiedName

            NavigationBarItem(
                label = {
                    Text(text = destination.label)
                },
                onClick = {
                    // Navegar a la ruta correspondiente al item seleccionado
                    navController.navigate(destination.route){
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if(isSelected) destination.iconSelected else destination.icon,
                        contentDescription = destination.label
                    )
                },
                selected = isSelected
            )
        }
    }
}