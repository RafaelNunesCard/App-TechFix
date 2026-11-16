package com.example.techfix.data

import android.content.Context

/** Cartão guardado só para exibição: NUNCA guarda o número completo nem o CVV. */
data class SavedCard(
    val id: String,
    val brand: String,   // "Visa", "Mastercard" ou "Cartão"
    val last4: String,
    val holder: String,
    val expiry: String   // MM/AA
)

/**
 * Lista de cartões salvos no celular (SharedPreferences).
 * Só ficam a bandeira, os 4 últimos dígitos, o nome impresso e a validade.
 */
object CardPrefs {
    private const val PREFS_NAME = "techfix_cards"
    private const val KEY_CARDS = "cards"
    private const val SEP = '\u001F'

    fun list(context: Context): List<SavedCard> {
        val raw = prefs(context).getString(KEY_CARDS, "").orEmpty()
        if (raw.isBlank()) return emptyList()
        return raw.split('\n').mapNotNull { line ->
            val p = line.split(SEP)
            if (p.size == 5) SavedCard(p[0], p[1], p[2], p[3], p[4]) else null
        }
    }

    fun add(context: Context, card: SavedCard) {
        save(context, list(context) + card)
    }

    fun remove(context: Context, id: String) {
        save(context, list(context).filterNot { it.id == id })
    }

    private fun save(context: Context, cards: List<SavedCard>) {
        val raw = cards.joinToString("\n") { c ->
            listOf(c.id, c.brand, c.last4, c.holder.replace('\n', ' ').replace(SEP, ' '), c.expiry)
                .joinToString(SEP.toString())
        }
        prefs(context).edit().putString(KEY_CARDS, raw).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
