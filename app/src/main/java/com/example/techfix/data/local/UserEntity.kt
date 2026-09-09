package com.example.techfix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local representation of a user account.
 *
 * NOTE: [passwordHash] and [passwordSalt] are derived from the user's
 * password via PBKDF2 (see PasswordHasher.kt) — the plain-text password
 * itself is never stored anywhere.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: Long = System.currentTimeMillis()
)
