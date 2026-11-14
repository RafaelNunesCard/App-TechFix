package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Pedido recebido por um profissional, já com o nome de quem pediu. */
data class ReceivedOrder(
    @Embedded val order: ServiceOrderEntity,
    val clientName: String
)

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

    /** Pedidos recebidos por um profissional + nome do cliente. */
    @Query(
        "SELECT o.*, u.fullName AS clientName FROM service_orders o " +
            "INNER JOIN users u ON u.email = o.clientEmail " +
            "WHERE o.professionalEmail = :email ORDER BY o.createdAt DESC"
    )
    fun observeReceivedByProfessional(email: String): Flow<List<ReceivedOrder>>

    @Query("UPDATE service_orders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM service_orders WHERE clientEmail = :email")
    suspend fun countByClient(email: String): Int
}
