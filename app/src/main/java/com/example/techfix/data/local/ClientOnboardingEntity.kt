package com.example.techfix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_onboarding")
data class ClientOnboardingEntity(
    @PrimaryKey val userEmail: String,
    val selectedCategoryIds: String, // CSV, ex: "computers,phones"
    val priorityId: String
)