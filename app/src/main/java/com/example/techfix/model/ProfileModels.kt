package com.example.techfix.model

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
    val dateLabel: String, // "FINALIZADO EM" ou "PREVISÃO DE ENTREGA"
    val date: String,
    val warranty: String? = null // ex: "Válida por 90 dias"
)

// Dados de exemplo extraídos do protótipo Figma para preview/teste
object SampleData {
    val carlosProfile = UserProfile(
        name = "Carlos Eduardo Araujo",
        avatarUrl = "https://exemplo.com/avatar.jpg",
        isVerified = true,
        location = "São Paulo, SP",
        memberSince = "Membro desde Março 2024",
        completedServices = 12,
        rating = 4.8,
        confidencePercent = 96,
        bio = "Profissional de TI com interesse em manutenção preventiva e upgrades de " +
            "hardware. Valorizo pontualidade, qualidade no serviço e comunicação clara.",
        interestCategories = listOf(
            "Reparo de Notebooks", "Troca de Tela", "Upgrade", "Preventiva", "Dados"
        ),
        preferences = listOf(
            "Atendimento presencial preferido",
            "Horário flexível (manhã ou tarde)",
            "Prefere orçamento prévio",
            "Comunicação via app"
        ),
        loyaltyLabel = "Excelente",
        loyaltyPercent = 96,
        serviceHistory = listOf(
            ServiceHistoryItem(
                deviceName = "MacBook Pro 16",
                description = "Troca de bateria & limpeza interna técnica",
                status = ServiceStatus.CONCLUIDO,
                progress = 100,
                dateLabel = "FINALIZADO EM",
                date = "14/10/2026",
                warranty = "Válida por 90 dias"
            ),
            ServiceHistoryItem(
                deviceName = "iPhone 14 Pro",
                description = "Substituição de tela OLED frontal",
                status = ServiceStatus.CONCLUIDO,
                progress = 100,
                dateLabel = "FINALIZADO EM",
                date = "28/09/2026",
                warranty = "Válida por 90 dias"
            )
        )
    )
}
