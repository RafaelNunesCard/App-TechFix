package com.example.techfix.data

import com.example.techfix.data.local.PasswordHasher
import com.example.techfix.data.local.UserDao
import com.example.techfix.data.local.UserEntity

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}

class AuthRepository(private val userDao: UserDao) {

    suspend fun signUp(fullName: String, email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()

        if (userDao.exists(normalizedEmail)) {
            return AuthResult.Failure("Já existe uma conta com esse e-mail.")
        }

        val hashed = PasswordHasher.hash(password)
        val user = UserEntity(
            email = normalizedEmail,
            fullName = fullName.trim(),
            passwordHash = hashed.hash,
            passwordSalt = hashed.salt
        )

        return try {
            userDao.insert(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Failure("Não foi possível criar a conta: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.findByEmail(normalizedEmail)
            ?: return AuthResult.Failure("E-mail ou senha incorretos.")

        val passwordMatches = PasswordHasher.verify(
            password = password,
            storedHash = user.passwordHash,
            storedSalt = user.passwordSalt
        )

        return if (passwordMatches) {
            AuthResult.Success(user)
        } else {
            AuthResult.Failure("E-mail ou senha incorretos.")
        }
    }
}
