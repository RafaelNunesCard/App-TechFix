package com.example.techfix.data

import androidx.room.withTransaction
import com.example.techfix.data.local.ServiceOrderEntity
import com.example.techfix.data.local.ServiceReviewEntity
import com.example.techfix.data.local.TechFixDatabase

internal enum class ReviewAuthor { CLIENT, PROFESSIONAL }

internal const val REVIEW_COMMENT_LIMIT = 500

internal fun ServiceOrderEntity.canBeReviewedBy(email: String, author: ReviewAuthor): Boolean {
    if (status != "COMPLETED" || email.isBlank() || clientEmail == professionalEmail) return false
    return when (author) {
        ReviewAuthor.CLIENT -> clientEmail == email
        ReviewAuthor.PROFESSIONAL -> professionalEmail == email
    }
}

internal suspend fun submitServiceReview(
    database: TechFixDatabase,
    orderId: Long,
    email: String,
    author: ReviewAuthor,
    stars: Int,
    comment: String
): Boolean {
    if (stars !in 1..5 || comment.length > REVIEW_COMMENT_LIMIT) return false

    return database.withTransaction {
        val reviews = database.serviceReviewDao()
        val order = reviews.findOrder(orderId) ?: return@withTransaction false
        if (!order.canBeReviewedBy(email, author)) return@withTransaction false

        val fromClient = author == ReviewAuthor.CLIENT
        val review = ServiceReviewEntity(
            orderId = orderId,
            authorRole = author.name,
            authorEmail = email,
            recipientEmail = if (fromClient) order.professionalEmail else order.clientEmail,
            professionalId = if (fromClient) order.professionalId else null,
            stars = stars,
            comment = comment.trim(),
            createdAt = System.currentTimeMillis()
        )
        // A chave (pedido, papel) preserva a primeira avaliação, inclusive em toques repetidos.
        reviews.insert(review) != -1L
    }
}
