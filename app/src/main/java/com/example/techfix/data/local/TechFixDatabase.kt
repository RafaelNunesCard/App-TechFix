package com.example.techfix.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        ClientOnboardingEntity::class,
        ProfessionalProfileEntity::class,
        ServiceOrderEntity::class,
        ProfessionalEntity::class,
        ChatMessageEntity::class,
        ServiceReviewEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class TechFixDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun clientOnboardingDao(): ClientOnboardingDao
    abstract fun professionalProfileDao(): ProfessionalProfileDao
    abstract fun serviceOrderDao(): ServiceOrderDao
    abstract fun professionalDao(): ProfessionalDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun serviceReviewDao(): ServiceReviewDao

    companion object {
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE service_orders ADD COLUMN professionalId TEXT")
                // SQLite anterior a 3.39 exige GROUP BY quando há HAVING.
                db.execSQL(
                    """
                    UPDATE service_orders
                    SET professionalId = (
                        SELECT CASE WHEN COUNT(*) = 1 THEN MIN(p.id) END
                        FROM professionals AS p
                        WHERE (service_orders.professionalEmail IS NOT NULL
                               AND p.email = service_orders.professionalEmail)
                           OR (service_orders.professionalEmail IS NULL
                               AND p.email IS NULL AND p.name = service_orders.professionalName)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS service_reviews (
                        orderId INTEGER NOT NULL,
                        authorRole TEXT NOT NULL,
                        authorEmail TEXT NOT NULL,
                        recipientEmail TEXT,
                        professionalId TEXT,
                        stars INTEGER NOT NULL,
                        comment TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY (orderId, authorRole),
                        FOREIGN KEY (orderId) REFERENCES service_orders (id)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_service_reviews_recipientEmail_authorRole " +
                        "ON service_reviews (recipientEmail, authorRole)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_service_reviews_professionalId " +
                        "ON service_reviews (professionalId)"
                )
            }
        }

        @Volatile
        private var INSTANCE: TechFixDatabase? = null

        fun getInstance(context: Context): TechFixDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TechFixDatabase::class.java,
                    "techfix.db"
                )
                    .addMigrations(MIGRATION_5_6)
                    // Mantém o comportamento anterior para esquemas sem migração cadastrada.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
