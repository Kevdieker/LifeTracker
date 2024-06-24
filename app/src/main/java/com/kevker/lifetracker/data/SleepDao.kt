package com.kevker.lifetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kevker.lifetracker.models.SleepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Insert
    suspend fun insertSleepTime(sleepEntity: SleepEntity)

    @Query("UPDATE sleep_times SET endTime = :endTime WHERE id = :id")
    suspend fun updateSleepEndTime(id: Int, endTime: Long)

    @Query("SELECT * FROM sleep_times")
    fun getAllSleepTimes(): Flow<List<SleepEntity>>
}
