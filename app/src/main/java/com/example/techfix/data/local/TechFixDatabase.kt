package com.example.techfix.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class TechFixDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: TechFixDatabase? = null

        fun getInstance(context: Context): TechFixDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TechFixDatabase::class.java,
                    "techfix.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
