package com.mauricio.calculadoraimc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Lista todas as tabelas (entities) e a versão do banco
@Database(entities = [Medicao::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Adiciona o tradutor de datas
abstract class AppDatabase : RoomDatabase() {

    // Conecta o DAO
    abstract fun medicaoDao(): MedicaoDao

    companion object {
        // A variável INSTANCE guarda o banco para ser reutilizado
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Se já existe, retorna o existente
            return INSTANCE ?: synchronized(this) {
                // Se não existe, cria um novo
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calculadora_imc_database" // Nome do arquivo do banco no celular
                )
                    .fallbackToDestructiveMigration() // Se mudar o banco, apaga e cria de novo (útil em dev)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}