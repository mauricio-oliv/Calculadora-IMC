package com.mauricio.calculadoraimc.domain

import kotlin.math.log10

object CalculadoraSaude {

    fun calcularIMC(peso: Double, altura: Double): Double {
        if (altura <= 0) return 0.0
        return peso / (altura * altura)
    }

    fun classificarIMC(imc: Double): String {
        return when {
            imc < 18.5 -> "Abaixo do peso"
            imc < 25.0 -> "Peso Normal"
            imc < 30.0 -> "Sobrepeso"
            imc < 35.0 -> "Obesidade Grau I"
            imc < 40.0 -> "Obesidade Grau II"
            else -> "Obesidade Grau III"
        }
    }

    // Fórmula de Harris-Benedict Revisada
    fun calcularTMB(peso: Double, altura: Double, idade: Int, genero: String): Double {
        val alturaCm = altura * 100
        return if (genero == "Masculino") {
            88.36 + (13.4 * peso) + (4.8 * alturaCm) - (5.7 * idade)
        } else {
            447.6 + (9.2 * peso) + (3.1 * alturaCm) - (4.3 * idade)
        }
    }

    // Estimativa simples baseada no IMC
    fun calcularPesoIdeal(altura: Double): Double {
        return 22.0 * (altura * altura)
    }

    // Fórmula de Deurenberg (mantida)
    fun calcularGordura(imc: Double, idade: Int, genero: String): Double {
        val s = if (genero == "Masculino") 1 else 0
        return (1.20 * imc) + (0.23 * idade) - (10.8 * s) - 5.4
    }

    fun getFatorAtividade(atividade: String): Double {
        return when (atividade) {
            "Sedentário" -> 1.2
            "Leve" -> 1.375
            "Moderado" -> 1.55
            "Intenso" -> 1.725
            else -> 1.2
        }
    }

    fun calcularTCD(tmb: Double, atividade: String): Double {
        return tmb * getFatorAtividade(atividade)
    }

    // --- GORDURA CORPORAL - US NAVY (VERSÃO CORRIGIDA E ESTÁVEL) ---

    /**
     * Todas as medidas devem ser informadas em CENTÍMETROS (cm).
     */
    fun calcularGorduraMarinha(
        genero: String,
        alturaCm: Double,
        pescocoCm: Double,
        cinturaCm: Double,
        quadrilCm: Double // usado apenas para mulheres
    ): Double {

        if (alturaCm <= 0) return 0.0

        return if (genero == "Masculino") {

            val diferenca = cinturaCm - pescocoCm
            if (diferenca <= 0) return 0.0

            86.010 * log10(diferenca) -
                    70.041 * log10(alturaCm) +
                    36.76

        } else {

            val diferenca = cinturaCm + quadrilCm - pescocoCm
            if (diferenca <= 0) return 0.0

            163.205 * log10(diferenca) -
                    97.684 * log10(alturaCm) -
                    78.387
        }
    }
}
