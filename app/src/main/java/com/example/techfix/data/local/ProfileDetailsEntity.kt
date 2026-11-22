package com.example.techfix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.techfix.model.UserProfile

/**
 * Dados do perfil que a própria pessoa edita (nome de exibição, localização, bio,
 * categorias e preferências). Uma linha por e-mail. A foto continua em ProfilePrefs.
 */
@Entity(tableName = "profile_details")
data class ProfileDetailsEntity(
    @PrimaryKey val email: String,
    val displayName: String,
    val location: String,
    val bio: String,
    val interests: String,   // itens separados por "|"
    val preferences: String  // itens separados por "|"
)

private const val SEPARATOR = "|"

private fun List<String>.toDbString(): String = joinToString(SEPARATOR)

private fun String.fromDbString(): List<String> =
    if (isBlank()) emptyList() else split(SEPARATOR)

/** UserProfile (tela) -> linha do Room. */
fun UserProfile.toDetails(email: String) = ProfileDetailsEntity(
    email = email,
    displayName = name,
    location = location,
    bio = bio,
    interests = interestCategories.toDbString(),
    preferences = preferences.toDbString()
)

/** Aplica o que foi salvo no Room por cima do perfil montado no login. */
fun ProfileDetailsEntity.applyTo(profile: UserProfile): UserProfile = profile.copy(
    name = displayName.ifBlank { profile.name },
    location = location,
    bio = bio,
    interestCategories = interests.fromDbString(),
    preferences = preferences.fromDbString()
)
