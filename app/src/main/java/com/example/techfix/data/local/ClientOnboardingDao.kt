package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClientOnboardingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ClientOnboardingEntity)

    @Query("SELECT * FROM client_onboarding WHERE userEmail = :email LIMIT 1")
    suspend fun getByEmail(email: String): ClientOnboardingEntity?
}