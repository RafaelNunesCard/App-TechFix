package com.example.techfix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Uma mensagem do chat de um pedido (cliente <-> profissional). */
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val senderEmail: String,
    val text: String,
    val createdAt: Long
)
