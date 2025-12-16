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

    // --- INPUTS (Cálculo IMC/TMB/Gordura Padrão) ---

    private val _pesoInput = MutableStateFlow("")
    val pesoInput: StateFlow<String> = _pesoInput.asStateFlow()

    private val _alturaInput = MutableStateFlow("")
    val alturaInput: StateFlow<String> = _alturaInput.asStateFlow()

    private val _idadeInput = MutableStateFlow("")
    val idadeInput: StateFlow<String> = _idadeInput.asStateFlow()

    private val _generoInput = MutableStateFlow("Masculino")
    val generoInput: StateFlow<String> = _generoInput.asStateFlow()

    private val _atividadeInput = MutableStateFlow("Sedentário") // Valor padrão
    val atividadeInput: StateFlow<String> = _atividadeInput.asStateFlow()

    // --- NOVO: INPUTS para a Calculadora de Gordura Corporal (Circunferências) ---

    // A altura e o gênero serão puxados do resultado principal (resultadoAtual)

    private val _pescocoInput = MutableStateFlow("") // Circunferência em cm
    val pescocoInput: StateFlow<String> = _pescocoInput.asStateFlow()

    private val _cinturaInput = MutableStateFlow("") // Circunferência em cm
    val cinturaInput: StateFlow<String> = _cinturaInput.asStateFlow()

    private val _quadrilInput = MutableStateFlow("") // Circunferência em cm (Apenas para mulheres)
    val quadrilInput: StateFlow<String> = _quadrilInput.asStateFlow()

    // --- RESULTADOS ---

    private val _resultadoAtual = MutableStateFlow<Medicao?>(null)
    val resultadoAtual: StateFlow<Medicao?> = _resultadoAtual.asStateFlow()

    val historico: StateFlow<List<Medicao>> = dao.listarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _medicaoSelecionada = MutableStateFlow<Medicao?>(null)
    val medicaoSelecionada: StateFlow<Medicao?> = _medicaoSelecionada.asStateFlow()

    // NOVO: Resultado Imediato da Gordura Corporal (Marinha)
    private val _gorduraAtual = MutableStateFlow<Double?>(null)
    val gorduraAtual: StateFlow<Double?> = _gorduraAtual.asStateFlow()


    // --- AÇÕES (O que o usuário faz) ---

    // Funções para atualizar os valores (Cálculo Padrão)
    fun onPesoChange(v: String) { _pesoInput.value = v }
    fun onAlturaChange(v: String) { _alturaInput.value = v }
    fun onIdadeChange(v: String) { _idadeInput.value = v }
    fun onGeneroChange(v: String) { _generoInput.value = v }
    fun onAtividadeChange(v: String) { _atividadeInput.value = v }

    // NOVO: Handlers para a Calculadora de Gordura Corporal (Marinha)
    fun onPescocoChange(v: String) { _pescocoInput.value = v }
    fun onCinturaChange(v: String) { _cinturaInput.value = v }
    fun onQuadrilChange(v: String) { _quadrilInput.value = v }

    // Funções para limpar os campos da tela de Gordura ao entrar
    fun clearGorduraInputs() {
        _pescocoInput.value = ""
        _cinturaInput.value = ""
        _quadrilInput.value = ""
        _gorduraAtual.value = null
    }

    fun calcular() {
        val p = _pesoInput.value.toDoubleOrNull()
        val a = _alturaInput.value.toDoubleOrNull()
        val i = _idadeInput.value.toIntOrNull()

        if (p != null && a != null && i != null) {
            val genero = _generoInput.value
            val atividade = _atividadeInput.value

            // Cálculos
            val imc = CalculadoraSaude.calcularIMC(p, a)
            val classif = CalculadoraSaude.classificarIMC(imc)
            val tmb = CalculadoraSaude.calcularTMB(p, a, i, genero)
            val ideal = CalculadoraSaude.calcularPesoIdeal(a)
            // Gordura Deurenberg (baseada em IMC, para o histórico)
            val gorduraDeurenberg = CalculadoraSaude.calcularGordura(imc, i, genero)

            val novaMedicao = Medicao(
                peso = p, altura = a, idade = i, genero = genero,
                imc = imc, classificacao = classif,
                tmb = tmb, pesoIdeal = ideal, gordura = gorduraDeurenberg,
                atividade = atividade,
                data = Date()
            )

            _resultadoAtual.value = novaMedicao

            viewModelScope.launch {
                dao.inserir(novaMedicao)
            }
        }
    }

    // NOVO: Função para calcular Gordura Corporal pela Marinha
    fun calcularGordura() {
        // Pega a altura e o gênero do resultado principal (já calculado)
        val medicaoBase = _resultadoAtual.value
        val alturaM = medicaoBase?.altura // Altura em metros
        val genero = medicaoBase?.genero

        // Pega os inputs de circunferência
        val p = _pescocoInput.value.toDoubleOrNull() // cm
        val c = _cinturaInput.value.toDoubleOrNull() // cm
        val q = _quadrilInput.value.toDoubleOrNull() // cm

        // Validação
        val isValidBase = alturaM != null && genero != null && p != null && c != null && alturaM > 0

        if (!isValidBase) {
            _gorduraAtual.value = null
            return
        }

        val alturaCm = alturaM * 100 // Converte altura de metros para cm
        val finalP = p ?: 0.0
        val finalC = c ?: 0.0

        if (genero == "Masculino") {
            val gordura = CalculadoraSaude.calcularGorduraMarinha(
                genero = genero,
                alturaCm = alturaCm,
                pescocoCm = finalP,
                cinturaCm = finalC,
                quadrilCm = 0.0 // Não usado para homens
            )
            _gorduraAtual.value = gordura

        } else if (genero == "Feminino" && q != null && q > 0) {
            val gordura = CalculadoraSaude.calcularGorduraMarinha(
                genero = genero,
                alturaCm = alturaCm,
                pescocoCm = finalP,
                cinturaCm = finalC,
                quadrilCm = q
            )
            _gorduraAtual.value = gordura

        } else {
            _gorduraAtual.value = null // Limpa o resultado se a entrada for inválida
        }
    }


    fun carregarDetalhe(id: Int) {
        viewModelScope.launch {
            _medicaoSelecionada.value = dao.buscarPorId(id)
        }
    }
}

// Factory (Sem alteração)
class CalculadoraViewModelFactory(private val dao: MedicaoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculadoraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculadoraViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}