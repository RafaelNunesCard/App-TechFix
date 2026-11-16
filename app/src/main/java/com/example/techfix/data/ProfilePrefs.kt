package com.example.techfix.data

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Foto de perfil guardada só no celular (sem servidor).
 *
 * A imagem escolhida na galeria é copiada para a pasta privada do app e o
 * caminho fica nas SharedPreferences, separado por e-mail. Assim não precisa
 * mexer no banco Room (nada de nova versão que apague os dados).
 */
object ProfilePrefs {
    private const val PREFS = "techfix_profile_photos"

    /** Devolve "file:///..." da foto da conta, ou null se ainda não tem. */
    fun photoUri(context: Context, email: String): String? {
        if (email.isBlank()) return null
        val path = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(email.lowercase(), null) ?: return null
        val file = File(path)
        return if (file.exists()) Uri.fromFile(file).toString() else null
    }

    /** Copia a imagem escolhida e devolve o novo "file:///...", ou null se falhar. */
    fun savePhoto(context: Context, email: String, source: Uri): String? {
        if (email.isBlank()) return null
        return try {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val key = email.lowercase()
            val safe = key.replace(Regex("[^a-z0-9]"), "_")
            val target = File(context.filesDir, "foto_${safe}_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(source)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
            prefs.getString(key, null)?.let { old -> File(old).delete() }
            prefs.edit().putString(key, target.absolutePath).apply()
            Uri.fromFile(target).toString()
        } catch (e: Exception) {
            null
        }
    }
}
