package com.example.techfix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Um pedido de serviço (cliente contratando um profissional).
 * [status] guarda o NOME do OrderStatus (ex: "IN_PROGRESS") como texto.
 * [priceCents] é o valor em centavos (18000 = R$ 180,00); null = "a combinar".
 */
@Entity(tableName = "service_orders")
data class ServiceOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientEmail: String,
    val professionalEmail: String?,
    val professionalName: String,
    val deviceName: String,
    val serviceDescription: String,
    val status: String,
    val priceCents: Int?,
    val createdAt: Long
)
