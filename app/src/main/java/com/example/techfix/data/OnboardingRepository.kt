package com.example.techfix.data

import com.example.techfix.data.local.ClientOnboardingEntity
import com.example.techfix.data.local.ProfessionalProfileEntity
import com.example.techfix.data.local.TechFixDatabase

data class ClientOnboardingData(
    val selectedCategoryIds: Set<String>,
    val priorityId: String
)

class OnboardingRepository(private val db: TechFixDatabase) {

    private val clientDao = db.clientOnboardingDao()
    private val proDao = db.professionalProfileDao()

    suspend fun saveClientOnboarding(
        userEmail: String,
        selectedCategoryIds: Set<String>,
        priorityId: String
    ) {
        clientDao.upsert(
            ClientOnboardingEntity(
                userEmail = userEmail,
                selectedCategoryIds = selectedCategoryIds.joinToString(","),
                priorityId = priorityId
            )
        )
    }

    suspend fun getClientOnboarding(userEmail: String): ClientOnboardingData? {
        val entity = clientDao.getByEmail(userEmail) ?: return null
        return ClientOnboardingData(
            selectedCategoryIds = entity.selectedCategoryIds
                .split(",")
                .filter { it.isNotBlank() }
                .toSet(),
            priorityId = entity.priorityId
        )
    }

    suspend fun saveProfessionalProfile(
        userEmail: String,
        serviceCategoryIds: Set<String>,
        specialtyIds: Set<String>,
        experienceLevel: String,
        serviceRadiusKm: Int,
        availableDayIds: Set<String>,
        bio: String,
        photoUri: String?
    ) {
        proDao.upsert(
            ProfessionalProfileEntity(
                userEmail = userEmail,
                serviceCategoryIds = serviceCategoryIds.joinToString(","),
                specialtyIds = specialtyIds.joinToString(","),
                experienceLevel = experienceLevel,
                serviceRadiusKm = serviceRadiusKm,
                availableDayIds = availableDayIds.joinToString(","),
                bio = bio,
                photoUri = photoUri
            )
        )
    }
}