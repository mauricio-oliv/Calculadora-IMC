package com.mauricio.calculadoraimc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mauricio.calculadoraimc.data.Medicao
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class) // Necessário para o TopAppBar
@Composable
fun HistoryScreen(
    listaMedicoes: List<Medicao>,
    onItemClick: (Int) -> Unit,
    onBack: () -> Unit // <--- Adicionamos este parâmetro para a ação de voltar
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Medições") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // O paddingValues garante que a lista não fique escondida embaixo da barra
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Importante!
            contentPadding = PaddingValues(16.dp)
        ) {
            if (listaMedicoes.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum registro encontrado.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(listaMedicoes) { medicao ->
                MedicaoCard(medicao = medicao, onClick = { onItemClick(medicao.id) })
            }
        }
    }
}

@Composable
fun MedicaoCard(medicao: Medicao, onClick: () -> Unit) {
    // Formata a data para ficar bonita (Ex: 15/10/2023)
    val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(medicao.data)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = dataFormatada, style = MaterialTheme.typography.labelMedium)
                Text(text = medicao.classificacao, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "IMC: ${String.format("%.2f", medicao.imc)}", style = MaterialTheme.typography.titleLarge)
        }
    }
}