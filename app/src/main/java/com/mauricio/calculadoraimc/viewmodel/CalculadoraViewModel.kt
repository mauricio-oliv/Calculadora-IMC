package com.mauricio.calculadoraimc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mauricio.calculadoraimc.data.Medicao
import com.mauricio.calculadoraimc.data.MedicaoDao
import com.mauricio.calculadoraimc.domain.CalculadoraSaude
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class CalculadoraViewModel(private val dao: MedicaoDao) : ViewModel() {

    // --- INPUTS (Onde o usuário digita) ---
    // Usamos o padrão Backing Property (_privado e publico)

    private val _pesoInput = MutableStateFlow("")
    val pesoInput: StateFlow<String> = _pesoInput.asStateFlow()

    private val _alturaInput = MutableStateFlow("")
    val alturaInput: StateFlow<String> = _alturaInput.asStateFlow()

    private val _idadeInput = MutableStateFlow("")
    val idadeInput: StateFlow<String> = _idadeInput.asStateFlow()

    private val _generoInput = MutableStateFlow("Masculino")
    val generoInput: StateFlow<String> = _generoInput.asStateFlow()

    // --- RESULTADOS ---

    // Resultado Imediato (para mostrar na Home)
    private val _resultadoAtual = MutableStateFlow<Medicao?>(null)
    val resultadoAtual: StateFlow<Medicao?> = _resultadoAtual.asStateFlow()

    // Histórico (Vem do banco)
    val historico: StateFlow<List<Medicao>> = dao.listarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Detalhe Selecionado (Para a tela de detalhes)
    private val _medicaoSelecionada = MutableStateFlow<Medicao?>(null)
    val medicaoSelecionada: StateFlow<Medicao?> = _medicaoSelecionada.asStateFlow()

    // --- AÇÕES (O que o usuário faz) ---

    // Funções para atualizar os valores enquanto digita
    fun onPesoChange(v: String) { _pesoInput.value = v }
    fun onAlturaChange(v: String) { _alturaInput.value = v }
    fun onIdadeChange(v: String) { _idadeInput.value = v }
    fun onGeneroChange(v: String) { _generoInput.value = v }

    fun calcular() {
        // Pega o valor atual (.value) dos fluxos
        val p = _pesoInput.value.toDoubleOrNull()
        val a = _alturaInput.value.toDoubleOrNull()
        val i = _idadeInput.value.toIntOrNull()

        // Só calcula se tudo for válido
        if (p != null && a != null && i != null) {
            val genero = _generoInput.value // Pega o valor atual do gênero

            val imc = CalculadoraSaude.calcularIMC(p, a)
            val classif = CalculadoraSaude.classificarIMC(imc)
            val tmb = CalculadoraSaude.calcularTMB(p, a, i, genero)
            val ideal = CalculadoraSaude.calcularPesoIdeal(a)
            val gordura = CalculadoraSaude.calcularGordura(imc, i, genero)

            val novaMedicao = Medicao(
                peso = p, altura = a, idade = i, genero = genero,
                imc = imc, classificacao = classif,
                tmb = tmb, pesoIdeal = ideal, gordura = gordura,
                data = Date()
            )

            // Atualiza a tela imediatamente
            _resultadoAtual.value = novaMedicao

            // Salva no banco
            viewModelScope.launch {
                dao.inserir(novaMedicao)
                // Opcional: Limpar campos descomentando abaixo
                // _pesoInput.value = ""
                // _alturaInput.value = ""
            }
        }
    }

    // Buscar detalhes pelo ID
    fun carregarDetalhe(id: Int) {
        viewModelScope.launch {
            _medicaoSelecionada.value = dao.buscarPorId(id)
        }
    }
}

// Factory (Necessária para passar o DAO)
class CalculadoraViewModelFactory(private val dao: MedicaoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculadoraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculadoraViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}