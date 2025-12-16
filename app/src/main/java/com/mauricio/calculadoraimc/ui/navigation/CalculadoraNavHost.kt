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
import com.mauricio.calculadoraimc.ui.screens.GorduraScreen // NOVO: Importar a nova tela
import com.mauricio.calculadoraimc.viewmodel.CalculadoraViewModel

@Composable
fun CalculadoraNavHost(viewModel: CalculadoraViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            // Coletando todos os estados
            val peso by viewModel.pesoInput.collectAsState()
            val altura by viewModel.alturaInput.collectAsState()
            val idade by viewModel.idadeInput.collectAsState()
            val genero by viewModel.generoInput.collectAsState()
            val atividade by viewModel.atividadeInput.collectAsState()
            val resultado by viewModel.resultadoAtual.collectAsState()

            HomeScreen(
                peso = peso,
                altura = altura,
                idade = idade,
                genero = genero,
                atividade = atividade,
                resultadoAtual = resultado,
                onPesoChange = viewModel::onPesoChange,
                onAlturaChange = viewModel::onAlturaChange,
                onIdadeChange = viewModel::onIdadeChange,
                onGeneroChange = viewModel::onGeneroChange,
                onAtividadeChange = viewModel::onAtividadeChange,
                onCalcularClick = { viewModel.calcular() },
                onVerHistoricoClick = { navController.navigate("history") },
                // NOVO: Handler para navegação à tela de gordura
                onCalcularGorduraClick = {
                    viewModel.clearGorduraInputs() // Limpa os inputs da tela de gordura ao navegar
                    navController.navigate("gordura")
                }
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
                    navController.popBackStack()
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
                onBack = { navController.popBackStack() }
            )
        }

        // --- NOVA ROTA: Calculadora de Gordura Corporal (Marinha) ---
        composable("gordura") {
            // Coleta os estados específicos da calculadora de gordura
            val medicaoBase by viewModel.resultadoAtual.collectAsState() // Pega altura e gênero da última medição
            val pescoco by viewModel.pescocoInput.collectAsState()
            val cintura by viewModel.cinturaInput.collectAsState()
            val quadril by viewModel.quadrilInput.collectAsState()
            val resultado by viewModel.gorduraAtual.collectAsState()

            GorduraScreen(
                medicaoBase = medicaoBase,
                pescoco = pescoco,
                cintura = cintura,
                quadril = quadril,
                resultadoGordura = resultado,
                onPescocoChange = viewModel::onPescocoChange,
                onCinturaChange = viewModel::onCinturaChange,
                onQuadrilChange = viewModel::onQuadrilChange,
                onCalcularClick = { viewModel.calcularGordura() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}