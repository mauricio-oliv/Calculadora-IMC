package com.mauricio.calculadoraimc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mauricio.calculadoraimc.data.Medicao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GorduraScreen(
    medicaoBase: Medicao?, // Dados do último cálculo (IMC, altura, gênero)
    pescoco: String,
    cintura: String,
    quadril: String,
    resultadoGordura: Double?,
    onPescocoChange: (String) -> Unit,
    onCinturaChange: (String) -> Unit,
    onQuadrilChange: (String) -> Unit,
    onCalcularClick: () -> Unit,
    onBack: () -> Unit
) {
    val genero = medicaoBase?.genero ?: "Não Informado"
    val altura = String.format("%.2f m", medicaoBase?.altura ?: 0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gordura Corporal (US Navy)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Cartão de Informações Base
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Informações do Último Cálculo:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Gênero: $genero")
                    Text("Altura: $altura")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Esses dados serão usados no cálculo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Campos de Entrada ---
            Text("Circunferências (em Centímetros - cm):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = pescoco,
                onValueChange = onPescocoChange,
                label = { Text("Pescoço (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = cintura,
                onValueChange = onCinturaChange,
                label = { Text("Cintura (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = quadril,
                onValueChange = onQuadrilChange,
                label = { Text("Quadril (cm) - Apenas Feminino") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = genero == "Feminino", // Desabilita para Masculino
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botão Calcular
            Button(
                onClick = onCalcularClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = medicaoBase != null // Só habilita se houver dados base (altura/gênero)
            ) {
                Text("CALCULAR GORDURA")
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // --- Resultado ---
            if (resultadoGordura != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Gordura Corporal Estimada:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${String.format("%.1f", resultadoGordura)} %",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Mensagem de classificação
                    Text(
                        text = classificarGorduraMarinha(resultadoGordura, genero),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (medicaoBase != null) {
                Text(
                    text = "Preencha as circunferências (em cm) e toque em CALCULAR.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Você deve realizar um cálculo de IMC/TMB na tela principal primeiro.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Classifica o percentual de gordura corporal de acordo com padrões de saúde.
 */
fun classificarGorduraMarinha(gordura: Double, genero: String): String {
    return if (genero == "Masculino") {
        when {
            gordura < 6 -> "Gordura Essencial (Perigoso)"
            gordura <= 13 -> "Atleta (Excelente)"
            gordura <= 17 -> "Fitness (Saudável)"
            gordura <= 24 -> "Média (Aceitável)"
            else -> "Obesidade (Risco)"
        }
    } else { // Feminino
        when {
            gordura < 14 -> "Gordura Essencial (Perigoso)"
            gordura <= 20 -> "Atleta (Excelente)"
            gordura <= 24 -> "Fitness (Saudável)"
            gordura <= 31 -> "Média (Aceitável)"
            else -> "Obesidade (Risco)"
        }
    }
}