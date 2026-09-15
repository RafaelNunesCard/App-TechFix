package com.example.techfix.data

import android.content.Context

/**
 * Diferente do UserSession (que é só uma variável na memória),
 * este objeto grava a informação num arquivo pequeno no celular
 * (SharedPreferences) — por isso ela sobrevive a fechar o app de
 * verdade. Só guardamos o EMAIL de quem está logado; os outros dados
 * (nome, etc.) a gente busca de novo no Room quando precisar,
 * usando esse email.
 */
object SessionManager {
    private const val PREFS_NAME = "techfix_session"
    private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"

    fun saveLoggedInEmail(context: Context, email: String) {
        prefs(context).edit().putString(KEY_LOGGED_IN_EMAIL, email).apply()
    }

    fun getLoggedInEmail(context: Context): String? =
        prefs(context).getString(KEY_LOGGED_IN_EMAIL, null)

    /** Chame isso num futuro botão de "Sair" (logout). */
    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_LOGGED_IN_EMAIL).apply()
    }

    private fun prefs(context: Context) =
        // "applicationContext" evita guardar uma referência a uma Activity
        // específica — usamos o contexto do app inteiro, que dura mais.
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
