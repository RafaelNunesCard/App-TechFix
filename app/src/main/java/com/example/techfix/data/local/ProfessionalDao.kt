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

    @Query("UPDATE professionals SET available = :available WHERE email = :email")
    suspend fun setAvailability(email: String, available: Boolean)

    /** null = esse profissional ainda não está na lista. */
    @Query("SELECT available FROM professionals WHERE email = :email LIMIT 1")
    fun observeAvailability(email: String): Flow<Boolean?>

    @Query("SELECT COUNT(*) FROM professionals")
    suspend fun count(): Int
}
