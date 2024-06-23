package com.kevker.lifetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kevker.lifetracker.models.Activity


@Database(
    entities = [Activity::class],
    version = 1,
    exportSchema = false,
)
abstract class LTDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile //preventCaching
        private var Instance: LTDatabase? = null
        fun getDatabase(context: Context): LTDatabase {
            return Instance ?: synchronized(this) { //prevent multithreading code-block
                Room.databaseBuilder(context, LTDatabase::class.java, "lt_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }
}