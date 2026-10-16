package co.edu.uniquindio.demoapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.demoapp.features.dashboard.MainScreen
import co.edu.uniquindio.demoapp.features.home.HomeScreen
import co.edu.uniquindio.demoapp.features.login.LoginScreen
import co.edu.uniquindio.demoapp.features.register.RegisterScreen

@Composable
fun AppNavigation(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    // Observa el estado de la sesión desde el ViewModel
    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = sessionState) {
            is SessionState.Loading -> {
                // Se muestra un indicador de carga mientras se determina el estado de la sesión
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            // Sin sesión: se muestra la navegación de autenticación
            is SessionState.NotAuthenticated -> AuthNavigation()
            // Con sesión: se delega en MainScreen, pasándole el rol del usuario
            is SessionState.Authenticated -> MainScreen(
                role = state.session.role
            )
        }
    }
}

@Composable
private fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.Home
    ) {

        composable<AuthRoutes.Home> {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(AuthRoutes.Login)
                },
                onNavigateToRegister = {
                    navController.navigate(AuthRoutes.Register)
                }
            )
        }

        composable<AuthRoutes.Login> {
            // Sin callback de navegación al autenticar: al guardarse la sesión,
            // AppNavigation cambia automáticamente a MainScreen.
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AuthRoutes.Register)
                }
            )
        }

        composable<AuthRoutes.Register> {
            RegisterScreen(
                onNavigateToBack = {
                    navController.popBackStack()
                }
            )
        }

        // Otras pantallas que no requieren autenticación pueden ir aquí
    }
}
