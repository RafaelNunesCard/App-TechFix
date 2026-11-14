package com.example.techfix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Profissional exibido em "Profissionais em destaque" na Home. */
@Entity(tableName = "professionals")
data class ProfessionalEntity(
    @PrimaryKey val id: String,
    val email: String?,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val available: Boolean
)
