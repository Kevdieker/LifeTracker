package com.kevker.lifetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kevker.lifetracker.models.Activity
import com.kevker.lifetracker.models.Glass
import com.kevker.lifetracker.models.SleepEntity

@Database(
    entities = [Activity::class, Glass::class, SleepEntity::class],
    version = 2,
    exportSchema = false
)
abstract class LTDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun glassDao(): GlassDao
    abstract fun sleepDao(): SleepDao

    companion object {
        @Volatile
        private var Instance: LTDatabase? = null

        fun getDatabase(context: Context): LTDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LTDatabase::class.java,
                    "lt_db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
