package com.example.techfix.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "service_reviews",
    primaryKeys = ["orderId", "authorRole"],
    foreignKeys = [
        ForeignKey(
            entity = ServiceOrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["recipientEmail", "authorRole"]), Index(value = ["professionalId"])]
)
data class ServiceReviewEntity(
    val orderId: Long,
    val authorRole: String,
    val authorEmail: String,
    val recipientEmail: String?,
    val professionalId: String?,
    val stars: Int,
    val comment: String,
    val createdAt: Long
)

data class ServiceReview(val authorName: String, val stars: Int, val comment: String)

data class ProfessionalRating(val professionalId: String, val rating: Double, val reviewCount: Int)

@Dao
interface ServiceReviewDao {
    @Query("SELECT * FROM service_orders WHERE id = :orderId")
    suspend fun findOrder(orderId: Long): ServiceOrderEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(review: ServiceReviewEntity): Long

    @Query(
        "SELECT * FROM service_reviews WHERE orderId = :orderId " +
            "AND authorRole = :authorRole AND authorEmail = :authorEmail"
    )
    fun observeSubmitted(orderId: Long, authorRole: String, authorEmail: String): Flow<ServiceReviewEntity?>

    @Query(
        "SELECT u.fullName AS authorName, r.stars, r.comment FROM service_reviews AS r " +
            "INNER JOIN users AS u ON u.email = r.authorEmail " +
            "WHERE r.recipientEmail = :email AND r.authorRole = :authorRole " +
            "ORDER BY r.createdAt DESC, r.orderId DESC"
    )
    fun observeReceived(email: String, authorRole: String): Flow<List<ServiceReview>>

    @Query(
        "SELECT u.fullName AS authorName, r.stars, r.comment FROM service_reviews AS r " +
            "INNER JOIN users AS u ON u.email = r.authorEmail " +
            "WHERE r.professionalId = :professionalId AND r.authorRole = 'CLIENT' " +
            "ORDER BY r.createdAt DESC, r.orderId DESC"
    )
    fun observeForProfessional(professionalId: String): Flow<List<ServiceReview>>

    @Query(
        "SELECT professionalId, AVG(stars) AS rating, COUNT(*) AS reviewCount FROM service_reviews " +
            "WHERE professionalId IS NOT NULL AND authorRole = 'CLIENT' GROUP BY professionalId"
    )
    fun observeProfessionalRatings(): Flow<List<ProfessionalRating>>
}
