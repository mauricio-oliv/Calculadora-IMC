package com.mauricio.calculadoraimc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mauricio.calculadoraimc.data.Medicao
import com.mauricio.calculadoraimc.domain.CalculadoraSaude
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    medicao: Medicao?, // Recebe os dados
    onBack: () -> Unit // Ação de voltar
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Medição") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (medicao != null) {
            // Calcula a Taxa de Consumo Diário (TCD)
            val tcd = CalculadoraSaude.calcularTCD(medicao.tmb, medicao.atividade)

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Cartão com os dados principais
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Data: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(medicao.data)}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text("IMC: ${String.format("%.2f", medicao.imc)}", style = MaterialTheme.typography.headlineMedium)
                        Text("Classificação: ${medicao.classificacao}", color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Indicadores de Saúde", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de detalhes
                DetalheItem("Peso", "${medicao.peso} kg")
                DetalheItem("Altura", "${medicao.altura} m")
                DetalheItem("Idade", "${medicao.idade} anos")
                DetalheItem("Gênero", medicao.genero)

                DetalheItem("Frequência de Atividade", medicao.atividade) // NOVO CAMPO

                Divider()

                DetalheItem("Peso Ideal Estimado", "${String.format("%.1f", medicao.pesoIdeal)} kg")
                DetalheItem("Taxa Metabólica Basal (TMB)", "${String.format("%.0f", medicao.tmb)} kcal")

                DetalheItem("Taxa de Consumo Diário (TCD)", "${String.format("%.0f", tcd)} kcal") // NOVO CAMPO

                DetalheItem("Gordura Corporal", "${String.format("%.1f", medicao.gordura)} %")
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun DetalheItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}