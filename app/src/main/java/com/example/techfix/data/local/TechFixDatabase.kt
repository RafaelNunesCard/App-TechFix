package com.example.techfix.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ClientOnboardingEntity::class,
        ProfessionalProfileEntity::class,
        ServiceOrderEntity::class,
        ProfessionalEntity::class,
               ChatMessageEntity::class,
        ProfileDetailsEntity::class
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
    abstract fun profileDetailsDao(): ProfileDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: TechFixDatabase? = null

        fun getInstance(context: Context): TechFixDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TechFixDatabase::class.java,
                    "techfix.db"
                )
                    // Enquanto não existir migração real, isso evita crash
                    // ao mudar o version — mas apaga os dados salvos no
                    // emulador/dispositivo a cada bump de versão.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
