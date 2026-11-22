package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDetailsDao {

    @Query("SELECT * FROM profile_details WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): ProfileDetailsEntity?

    /** Cria ou substitui os dados do perfil daquele e-mail. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(details: ProfileDetailsEntity)
}
