package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfessionalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(professionals: List<ProfessionalEntity>)

    @Query("SELECT * FROM professionals ORDER BY rating DESC, reviewCount DESC")
    fun observeAll(): Flow<List<ProfessionalEntity>>

    @Query("SELECT COUNT(*) FROM professionals")
    suspend fun count(): Int
}
