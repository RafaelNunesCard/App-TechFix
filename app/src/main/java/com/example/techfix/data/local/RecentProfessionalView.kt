package com.example.techfix.data.local

import androidx.room.Entity

@Entity(
    tableName = "recent_professional_views",
    primaryKeys = ["viewerEmail", "professionalId"]
)
data class RecentProfessionalView(
    val viewerEmail: String,
    val professionalId: String,
    val viewedAt: Long
)
