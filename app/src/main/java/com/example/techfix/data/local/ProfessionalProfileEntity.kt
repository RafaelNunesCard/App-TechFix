package com.example.techfix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "professional_profile")
data class ProfessionalProfileEntity(
    @PrimaryKey val userEmail: String,
    val serviceCategoryIds: String, // CSV
    val specialtyIds: String,       // CSV
    val experienceLevel: String,
    val serviceRadiusKm: Int,
    val availableDayIds: String,    // CSV
    val bio: String,
    val photoUri: String?
)