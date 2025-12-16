package com.mauricio.calculadoraimc.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mauricio.calculadoraimc.ui.screens.DetailScreen
import com.mauricio.calculadoraimc.ui.screens.HistoryScreen
import com.mauricio.calculadoraimc.ui.screens.HomeScreen
import com.mauricio.calculadoraimc.viewmodel.CalculadoraViewModel

@Composable
fun CalculadoraNavHost(viewModel: CalculadoraViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            // Coletando todos os estados novos
            val peso by viewModel.pesoInput.collectAsState()
            val altura by viewModel.alturaInput.collectAsState()
            val idade by viewModel.idadeInput.collectAsState()
            val genero by viewModel.generoInput.collectAsState()
            val resultado by viewModel.resultadoAtual.collectAsState()

            HomeScreen(
                peso = peso, altura = altura, idade = idade, genero = genero,
                resultadoAtual = resultado,
                onPesoChange = viewModel::onPesoChange,
                onAlturaChange = viewModel::onAlturaChange,
                onIdadeChange = viewModel::onIdadeChange,
                onGeneroChange = viewModel::onGeneroChange,
                onCalcularClick = { viewModel.calcular() },
                onVerHistoricoClick = { navController.navigate("history") }
            )
        }

        composable("history") {
            val lista by viewModel.historico.collectAsState()

            HistoryScreen(
                listaMedicoes = lista,
                onItemClick = { id ->
                    navController.navigate("details/$id")
                },
                onBack = {
                    navController.popBackStack() // <--- Isso faz voltar para a Home
                }
            )
        }

        composable(
            "details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0

            // Pede ao ViewModel para carregar esse ID
            LaunchedEffect(id) {
                viewModel.carregarDetalhe(id)
            }

            val medicao by viewModel.medicaoSelecionada.collectAsState()

            DetailScreen(
                medicao = medicao,
                onBack = { navController.popBackStack() } // Volta para a tela anterior
            )
        }
    }
}