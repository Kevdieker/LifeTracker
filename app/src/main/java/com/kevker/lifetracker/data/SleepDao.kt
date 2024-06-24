package com.kevker.lifetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kevker.lifetracker.models.SleepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Insert
    suspend fun insertSleepTime(sleepTime: SleepEntity)

    @Query("SELECT * FROM sleep_times")
    fun getAllSleepTimes(): Flow<List<SleepEntity>>
}
