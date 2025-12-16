package com.mauricio.calculadoraimc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.mauricio.calculadoraimc.data.AppDatabase
import com.mauricio.calculadoraimc.ui.navigation.CalculadoraNavHost
import com.mauricio.calculadoraimc.ui.theme.CalculadoraIMCTheme // Use o nome do seu tema se for diferente
import com.mauricio.calculadoraimc.viewmodel.CalculadoraViewModel
import com.mauricio.calculadoraimc.viewmodel.CalculadoraViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Instancia o Banco de Dados
        val db = AppDatabase.getDatabase(applicationContext)

        // 2. Cria a Factory do ViewModel passando o DAO
        val factory = CalculadoraViewModelFactory(db.medicaoDao())

        // 3. Obtém o ViewModel pronto para uso
        val viewModel = ViewModelProvider(this, factory)[CalculadoraViewModel::class.java]

        setContent {
            // Use o tema padrão que veio com o projeto (pode ter outro nome)
            CalculadoraIMCTheme {
                // 4. Chama a navegação passando o ViewModel
                CalculadoraNavHost(viewModel = viewModel)
            }
        }
    }
}