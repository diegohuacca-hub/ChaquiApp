// navigation/NavGraph.kt
package com.example.datossinmvvm.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.datossinmvvm.ui.DetalleScreen
import com.example.datossinmvvm.ui.FormularioScreen
import com.example.datossinmvvm.ui.IncidenteViewModel
import com.example.datossinmvvm.ui.ListaScreen

// Rutas de navegación como constantes
object Routes {
    const val LISTA = "lista"
    const val FORMULARIO = "formulario"
    const val DETALLE = "detalle/{incidenteId}"
    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // Un solo ViewModel compartido entre todas las pantallas
    val viewModel: IncidenteViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LISTA
    ) {
        composable(Routes.LISTA) {
            ListaScreen(
                viewModel = viewModel,
                onNuevoIncidente = { navController.navigate(Routes.FORMULARIO) },
                onVerDetalle = { id -> navController.navigate(Routes.detalle(id)) }
            )
        }

        composable(Routes.FORMULARIO) {
            FormularioScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETALLE,
            arguments = listOf(
                navArgument("incidenteId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("incidenteId") ?: return@composable
            DetalleScreen(
                incidenteId = id,
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
