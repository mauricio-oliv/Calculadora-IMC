package com.mauricio.calculadoraimc.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "medicoes")
data class Medicao(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val peso: Double,
    val altura: Double,
    val imc: Double,
    val classificacao: String,
    // Novos campos:
    val idade: Int,
    val genero: String,
    val tmb: Double,
    val pesoIdeal: Double,
    val gordura: Double,
    // NOVO CAMPO: Atividade Física
    val atividade: String, // Adicionar para persistência
    val data: Date = Date()
)