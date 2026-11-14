package com.example.techfix.data

import com.example.techfix.data.local.PasswordHasher
import com.example.techfix.data.local.ProfessionalEntity
import com.example.techfix.data.local.ProfessionalProfileEntity
import com.example.techfix.data.local.ClientOnboardingEntity
import com.example.techfix.data.local.ServiceOrderEntity
import com.example.techfix.data.local.TechFixDatabase
import com.example.techfix.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Dados de demonstração (para a feira / apresentação).
 *
 * É chamado toda vez que o app abre, mas só cria o que ainda NÃO existe:
 * se a conta demo já está no banco, não mexe em nada. Se o banco for
 * apagado (ex: mudança de versão), tudo isso é recriado sozinho.
 *
 * Contas de teste (a senha é a mesma nas duas):
 *   Cliente:      cliente@techfix.demo
 *   Profissional: profissional@techfix.demo
 *   Senha:        techfix123
 */
object DemoData {
    const val CLIENT_EMAIL = "cliente@techfix.demo"
    const val PRO_EMAIL = "profissional@techfix.demo"
    const val PASSWORD = "techfix123"

    private const val DAY = 24L * 60 * 60 * 1000

    suspend fun seedIfNeeded(db: TechFixDatabase) = withContext(Dispatchers.Default) {
        val userDao = db.userDao()
        val now = System.currentTimeMillis()

        // ---- Contas ----
        if (!userDao.exists(CLIENT_EMAIL)) {
            val h = PasswordHasher.hash(PASSWORD)
            userDao.insert(
                UserEntity(
                    email = CLIENT_EMAIL,
                    fullName = "Marina Souza",
                    passwordHash = h.hash,
                    passwordSalt = h.salt,
                    createdAt = now - 120 * DAY
                )
            )
        }
        if (!userDao.exists(PRO_EMAIL)) {
            val h = PasswordHasher.hash(PASSWORD)
            userDao.insert(
                UserEntity(
                    email = PRO_EMAIL,
                    fullName = "Carlos Mendes",
                    passwordHash = h.hash,
                    passwordSalt = h.salt,
                    createdAt = now - 200 * DAY
                )
            )
        }

        // ---- Onboarding salvo (é isso que diferencia cliente de profissional) ----
        if (db.clientOnboardingDao().getByEmail(CLIENT_EMAIL) == null) {
            db.clientOnboardingDao().upsert(
                ClientOnboardingEntity(
                    userEmail = CLIENT_EMAIL,
                    selectedCategoryIds = "computers,phones,wifi",
                    priorityId = "highest_rated"
                )
            )
        }
        if (db.professionalProfileDao().getByEmail(PRO_EMAIL) == null) {
            db.professionalProfileDao().upsert(
                ProfessionalProfileEntity(
                    userEmail = PRO_EMAIL,
                    serviceCategoryIds = "technology,electrical",
                    specialtyIds = "",
                    experienceLevel = "Mais de 10 anos",
                    serviceRadiusKm = 15,
                    availableDayIds = "mon,tue,wed,thu,fri",
                    bio = "Técnico com mais de 10 anos de experiência em manutenção de " +
                        "computadores, redes e instalações elétricas.",
                    photoUri = null
                )
            )
        }

        // ---- Profissionais da Home ----
        val proDao = db.professionalDao()
        if (proDao.count() == 0) {
            proDao.insertAll(
                listOf(
                    ProfessionalEntity("1", PRO_EMAIL, "Carlos Mendes", "Eletricista", 4.9, 128, 1.2, true),
                    ProfessionalEntity("2", null, "Ana Ferreira", "Técnica em Celulares", 4.8, 94, 2.4, true),
                    ProfessionalEntity("3", null, "Roberto Lima", "Técnico em TI", 4.7, 210, 0.8, false),
                    ProfessionalEntity("4", null, "Juliana Costa", "Redes e Wi-Fi", 4.9, 76, 3.1, true)
                )
            )
        }

        // ---- Pedidos da cliente demo ----
        val orderDao = db.serviceOrderDao()
        if (orderDao.countByClient(CLIENT_EMAIL) == 0) {
            orderDao.insertAll(
                listOf(
                    ServiceOrderEntity(
                        clientEmail = CLIENT_EMAIL, professionalEmail = PRO_EMAIL,
                        professionalName = "Carlos Mendes",
                        deviceName = "Notebook Dell Inspiron",
                        serviceDescription = "Limpeza interna e troca de pasta térmica",
                        status = "IN_PROGRESS", priceCents = 18000, createdAt = now,
                        problemText = "O notebook esquenta muito e desliga sozinho depois de uns 20 minutos.",
                        scheduledLabel = "Hoje, 16:00", locationLabel = "Na casa do cliente - Rua das Flores, 120"
                    ),
                    ServiceOrderEntity(
                        clientEmail = CLIENT_EMAIL, professionalEmail = PRO_EMAIL,
                        professionalName = "Carlos Mendes",
                        deviceName = "Roteador Wi-Fi",
                        serviceDescription = "Instalação e configuração da rede da casa",
                        status = "PENDING", priceCents = null, createdAt = now - 1 * DAY,
                        problemText = "Preciso instalar um roteador novo e configurar a rede para toda a casa.",
                        scheduledLabel = "Amanhã, 09:00", locationLabel = "Na casa do cliente - Rua das Flores, 120"
                    ),
                    ServiceOrderEntity(
                        clientEmail = CLIENT_EMAIL, professionalEmail = null,
                        professionalName = "Ana Ferreira",
                        deviceName = "iPhone 14 Pro",
                        serviceDescription = "Substituição da tela OLED frontal",
                        status = "WAITING_PARTS", priceCents = 89000, createdAt = now - 3 * DAY,
                        problemText = "A tela trincou depois de uma queda e o toque parou de funcionar.",
                        scheduledLabel = "Há 3 dias", locationLabel = "Levar ao técnico"
                    ),
                    ServiceOrderEntity(
                        clientEmail = CLIENT_EMAIL, professionalEmail = null,
                        professionalName = "Roberto Lima",
                        deviceName = "MacBook Pro 16",
                        serviceDescription = "Troca de bateria e limpeza técnica",
                        status = "COMPLETED", priceCents = 42000, createdAt = now - 30 * DAY,
                        problemText = "A bateria não segura mais carga e o aparelho precisa de limpeza.",
                        scheduledLabel = "Há 30 dias", locationLabel = "Levar ao técnico"
                    )
                )
            )
        }
    }
}
