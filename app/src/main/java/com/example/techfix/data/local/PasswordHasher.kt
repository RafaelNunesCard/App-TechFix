package com.example.techfix.data.local

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Turns a plain-text password into a salted PBKDF2 hash.
 * Never store the plain-text password — only [hash] and [salt] go in the DB.
 */
object PasswordHasher {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    data class HashResult(val hash: String, val salt: String)

    fun hash(password: String): HashResult {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hashBytes = deriveKey(password, salt)
        return HashResult(
            hash = hashBytes.toHexString(),
            salt = salt.toHexString()
        )
    }

    fun verify(password: String, storedHash: String, storedSalt: String): Boolean {
        val salt = storedSalt.hexToByteArray()
        val computedHash = deriveKey(password, salt).toHexString()
        return computedHash == storedHash
    }

    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }

    private fun ByteArray.toHexString(): String =
        joinToString("") { "%02x".format(it) }

    private fun String.hexToByteArray(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
