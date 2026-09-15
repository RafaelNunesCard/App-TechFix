package com.example.techfix.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ClientOnboardingEntity::class,
        ProfessionalProfileEntity::class
    ],
    version = 2, // era 1 — subiu porque adicionamos tabelas novas
    exportSchema = false
)
abstract class TechFixDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun clientOnboardingDao(): ClientOnboardingDao
    abstract fun professionalProfileDao(): ProfessionalProfileDao

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