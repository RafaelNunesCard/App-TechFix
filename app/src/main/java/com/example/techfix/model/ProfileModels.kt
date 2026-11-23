package com.example.techfix.model

// Os campos de UserProfile e ServiceHistoryItem são os mesmos de antes
// (o MainActivity monta e lê estes nomes). Só foram removidos os dados fictícios.

data class UserProfile(
    val name: String,
    val avatarUrl: String,
    val isVerified: Boolean,
    val location: String,
    val memberSince: String,
    val completedServices: Int,
    val rating: Double,
    val confidencePercent: Int,
    val bio: String,
    val interestCategories: List<String>,
    val preferences: List<String>,
    val loyaltyLabel: String,
    val loyaltyPercent: Int,
    val serviceHistory: List<ServiceHistoryItem>
)

enum class ServiceStatus(val label: String) {
    CONCLUIDO("Concluído"),
    EM_ANDAMENTO("Em Andamento"),
    AGUARDANDO_PECAS("Aguardando Peças")
}

data class ServiceHistoryItem(
    val deviceName: String,
    val description: String,
    val status: ServiceStatus,
    val progress: Int,
    val dateLabel: String, // "FINALIZADO EM" ou "ABERTO EM"
    val date: String,
    val warranty: String? = null // ex: "Válida por 90 dias"
)

/** Opções e limites da edição de perfil. As categorias são as mesmas do onboarding do cliente. */
object ProfileOptions {
    val interests = listOf(
        "Computadores",
        "Celulares",
        "Wi-Fi e Redes",
        "Elétrica",
        "Hidráulica",
        "Manutenção",
        "Pintura",
        "Fotografia"
    )

    val preferences = listOf(
        "Atendimento presencial preferido",
        "Horário flexível (manhã ou tarde)",
        "Prefere orçamento prévio",
        "Comunicação via app"
    )

    const val MAX_NAME = 60
    const val MAX_LOCATION = 60
    const val MAX_BIO = 200

    /** Quantos serviços aparecem no perfil; o restante fica na aba Pedidos. */
    const val HISTORY_PREVIEW_COUNT = 3

    /** Demonstração: 5 toques seguidos na logo abrem o visualizador do banco. */
    const val DB_VIEWER_TAPS = 5
    const val DB_VIEWER_TAP_WINDOW_MS = 800L
}
