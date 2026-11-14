package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceOrderDao {

    @Insert
    suspend fun insert(order: ServiceOrderEntity)

    @Insert
    suspend fun insertAll(orders: List<ServiceOrderEntity>)

    /** Pedidos de um cliente, do mais novo para o mais antigo. Atualiza sozinho a tela quando muda. */
    @Query("SELECT * FROM service_orders WHERE clientEmail = :email ORDER BY createdAt DESC")
    fun observeByClient(email: String): Flow<List<ServiceOrderEntity>>

    /** Pedidos recebidos por um profissional (usado na etapa do painel profissional). */
    @Query("SELECT * FROM service_orders WHERE professionalEmail = :email ORDER BY createdAt DESC")
    fun observeByProfessional(email: String): Flow<List<ServiceOrderEntity>>

    @Query("SELECT COUNT(*) FROM service_orders WHERE clientEmail = :email")
    suspend fun countByClient(email: String): Int
}
