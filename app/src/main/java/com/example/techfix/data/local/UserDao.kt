package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun exists(email: String): Boolean

    /** Troca a senha (hash + salt) de uma conta. Devolve quantas linhas mudaram (0 = e-mail não existe). */
    @Query("UPDATE users SET passwordHash = :hash, passwordSalt = :salt WHERE email = :email")
    suspend fun updatePassword(email: String, hash: String, salt: String): Int
}
