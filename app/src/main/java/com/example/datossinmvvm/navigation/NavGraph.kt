package com.example.datossinmvvm.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.datossinmvvm.ui.*

object Routes {
    const val LISTA = "lista"
    const val FORMULARIO = "formulario"
    const val DETALLE = "detalle/{incidenteId}"
    const val CATEGORIAS = "categorias"
    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: IncidenteViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.LISTA) {

        composable(Routes.LISTA) {
            ListaScreen(
                viewModel = viewModel,
                onNuevoIncidente = { navController.navigate(Routes.FORMULARIO) },
                onVerDetalle = { id -> navController.navigate(Routes.detalle(id)) },
                onGestionarCategorias = { navController.navigate(Routes.CATEGORIAS) }
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
            arguments = listOf(navArgument("incidenteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("incidenteId") ?: return@composable
            DetalleScreen(
                incidenteId = id,
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.CATEGORIAS) {
            CategoriasScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
