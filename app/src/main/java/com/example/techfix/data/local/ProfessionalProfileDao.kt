package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfessionalProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProfessionalProfileEntity)

    @Query("SELECT * FROM professional_profile WHERE userEmail = :email LIMIT 1")
    suspend fun getByEmail(email: String): ProfessionalProfileEntity?

    @Query("SELECT * FROM professional_profile")
    fun observeAll(): Flow<List<ProfessionalProfileEntity>>
}
