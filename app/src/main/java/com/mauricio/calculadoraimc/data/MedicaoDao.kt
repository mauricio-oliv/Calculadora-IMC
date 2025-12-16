package com.mauricio.calculadoraimc.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicaoDao {
    // Insere uma nova medição no banco
    @Insert
    suspend fun inserir(medicao: Medicao)

    // Retorna uma lista que se atualiza sozinha (Flow) quando o banco muda
    @Query("SELECT * FROM medicoes ORDER BY id DESC")
    fun listarTodas(): Flow<List<Medicao>>

    // Busca apenas uma pelo ID (para a tela de detalhes)
    @Query("SELECT * FROM medicoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Medicao?
}