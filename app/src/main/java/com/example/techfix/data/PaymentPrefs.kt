package com.example.techfix.data

import android.content.Context

/**
 * Guarda no celular a forma de pagamento preferida (só uma preferência —
 * o app NÃO processa pagamento de verdade nem guarda dados de cartão).
 */
object PaymentPrefs {
    private const val PREFS_NAME = "techfix_payment"
    private const val KEY_METHOD = "preferred_method"
    const val DEFAULT_METHOD = "pix"

    fun get(context: Context): String =
        prefs(context).getString(KEY_METHOD, DEFAULT_METHOD) ?: DEFAULT_METHOD

    fun set(context: Context, method: String) {
        prefs(context).edit().putString(KEY_METHOD, method).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
