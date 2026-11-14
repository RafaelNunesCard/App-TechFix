package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Mensagem recebida + nome de quem enviou (usado nas notificações). */
data class IncomingMessage(
    @Embedded val message: ChatMessageEntity,
    val senderName: String
)

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insert(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE orderId = :orderId ORDER BY createdAt ASC")
    fun observeByOrder(orderId: Long): Flow<List<ChatMessageEntity>>

    /** Últimas mensagens que OUTRA pessoa mandou nos pedidos deste usuário. */
    @Query(
        "SELECT m.*, u.fullName AS senderName FROM chat_messages m " +
            "INNER JOIN service_orders o ON o.id = m.orderId " +
            "INNER JOIN users u ON u.email = m.senderEmail " +
            "WHERE (o.clientEmail = :email OR o.professionalEmail = :email) " +
            "AND m.senderEmail != :email ORDER BY m.createdAt DESC LIMIT 20"
    )
    fun observeIncoming(email: String): Flow<List<IncomingMessage>>
}
