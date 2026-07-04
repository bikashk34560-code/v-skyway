package com.vskyway.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AiProviderEntity::class, 
        SessionEntity::class, 
        MessageEntity::class, 
        ApprovalRuleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SkywayDatabase : RoomDatabase() {

    abstract fun skywayDao(): SkywayDao

    companion object {
        @Volatile
        private var INSTANCE: SkywayDatabase? = null

        fun getDatabase(context: Context): SkywayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkywayDatabase::class.java,
                    "skyway_master_db"
                )
                // Yahan hardware keystore encryption aage add hoga
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}