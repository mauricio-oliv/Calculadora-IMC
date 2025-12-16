package com.mauricio.calculadoraimc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mauricio.calculadoraimc.data.Medicao
import com.mauricio.calculadoraimc.domain.CalculadoraSaude
import java.text.SimpleDateFormat
import java.util.Locale

// Lista de opções de Atividade Física (para Dropdown)
private val atividades = listOf("Sedentário", "Leve", "Moderado", "Intenso")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    peso: String,
    altura: String,
    idade: String,
    genero: String,
    atividade: String, // NOVO: Estado para atividade
    resultadoAtual: Medicao?,
    onPesoChange: (String) -> Unit,
    onAlturaChange: (String) -> Unit,
    onIdadeChange: (String) -> Unit,
    onGeneroChange: (String) -> Unit,
    onAtividadeChange: (String) -> Unit, // NOVO: Handler para atividade
    onCalcularClick: () -> Unit,
    onVerHistoricoClick: () -> Unit,
    onCalcularGorduraClick: () -> Unit // NOVO: Handler para o novo botão
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadora de Saúde") },
                actions = {
                    TextButton(onClick = onVerHistoricoClick) {
                        Text("HISTÓRICO")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campos de Entrada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = peso,
                    onValueChange = onPesoChange,
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = altura,
                    onValueChange = onAlturaChange,
                    label = { Text("Altura (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = idade,
                    onValueChange = onIdadeChange,
                    label = { Text("Idade (anos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                MenuButton(
                    modifier = Modifier.weight(1f),
                    label = "Gênero",
                    opcoes = listOf("Masculino", "Feminino"),
                    valorSelecionado = genero,
                    onValorSelecionado = onGeneroChange
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Seletor de Atividade Física (Novo)
            MenuButton(
                modifier = Modifier.fillMaxWidth(),
                label = "Atividade Física Semanal",
                opcoes = atividades,
                valorSelecionado = atividade,
                onValorSelecionado = onAtividadeChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Calcular
            Button(
                onClick = onCalcularClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CALCULAR IMC e TMB")
            }

            // --- Resultado ---
            resultadoAtual?.let { medicao ->
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Exibição do IMC
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Seu IMC: ${String.format("%.2f", medicao.imc)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Classificação: ${medicao.classificacao}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Outros Resultados
                    Text(text = "TMB: ${String.format("%.0f", medicao.tmb)} kcal")
                    // TCD é calculada aqui para ser exibida na Home
                    val tcd = CalculadoraSaude.calcularTCD(medicao.tmb, medicao.atividade)
                    Text(text = "TCD Estimada: ${String.format("%.0f", tcd)} kcal")

                    Spacer(modifier = Modifier.height(16.dp))

                    // NOVO BOTÃO: Cálculo de Gordura por Medidas
                    OutlinedButton(
                        onClick = onCalcularGorduraClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "Calcular Gordura")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CALCULAR GORDURA POR MEDIDAS")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onVerHistoricoClick) {
                        Text("Ver Detalhes e Histórico Completo")
                    }
                }
            }
        }
    }
}

// Componente reutilizável para o Dropdown Menu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    label: String,
    opcoes: List<String>,
    valorSelecionado: String,
    onValorSelecionado: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = valorSelecionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier.menuAnchor().fillMaxWidth().clickable { expandido = true }
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opcoes.forEach { selecao ->
                DropdownMenuItem(
                    text = { Text(selecao) },
                    onClick = {
                        onValorSelecionado(selecao)
                        expandido = false
                    }
                )
            }
        }
    }
}