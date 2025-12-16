package com.mauricio.calculadoraimc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mauricio.calculadoraimc.data.Medicao

@Composable
fun HomeScreen(
    peso: String, altura: String, idade: String, genero: String,
    resultadoAtual: Medicao?, // Novo parâmetro
    onPesoChange: (String) -> Unit,
    onAlturaChange: (String) -> Unit,
    onIdadeChange: (String) -> Unit,
    onGeneroChange: (String) -> Unit,
    onCalcularClick: () -> Unit,
    onVerHistoricoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Permite rolar se a tela for pequena
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calculadora Completa", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Inputs
        OutlinedTextField(value = peso, onValueChange = onPesoChange, label = { Text("Peso (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = altura, onValueChange = onAlturaChange, label = { Text("Altura (m)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = idade, onValueChange = onIdadeChange, label = { Text("Idade (anos)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        // Seletor de Gênero
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = genero == "Masculino", onClick = { onGeneroChange("Masculino") })
            Text("Masculino")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = genero == "Feminino", onClick = { onGeneroChange("Feminino") })
            Text("Feminino")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCalcularClick, modifier = Modifier.fillMaxWidth()) { Text("Calcular") }

        // --- RESULTADO IMEDIATO ---
        if (resultadoAtual != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resultado:", style = MaterialTheme.typography.titleLarge)
                    Text("IMC: ${String.format("%.2f", resultadoAtual.imc)}", style = MaterialTheme.typography.headlineLarge)
                    Text("Classificação: ${resultadoAtual.classificacao}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("TMB: ${String.format("%.0f", resultadoAtual.tmb)} kcal")
                    Text("Gordura: ${String.format("%.1f", resultadoAtual.gordura)}%")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onVerHistoricoClick, modifier = Modifier.fillMaxWidth()) { Text("Ver Histórico Completo") }
    }
}