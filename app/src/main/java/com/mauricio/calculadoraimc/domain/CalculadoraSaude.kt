package com.mauricio.calculadoraimc.domain

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
        // IMC médio desejável de 22
        return 22.0 * (altura * altura)
    }

    // Fórmula de Deurenberg para gordura corporal
    fun calcularGordura(imc: Double, idade: Int, genero: String): Double {
        val s = if (genero == "Masculino") 1 else 0
        return (1.20 * imc) + (0.23 * idade) - (10.8 * s) - 5.4
    }
}