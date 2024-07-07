package com.kevker.lifetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.kevker.lifetracker.models.StepCount
import kotlinx.coroutines.flow.Flow

@Dao
interface StepCountDao {
    @Insert
    suspend fun insertAll(vararg steps: StepCount)

    @Transaction
    @Query("SELECT * FROM step WHERE created_at >= :startDateTime AND created_at < :endDateTime")
    fun loadAllStepsFromToday(startDateTime: Long, endDateTime: Long): Flow<List<StepCount>>

    @Transaction
    @Query("SELECT * FROM step")
    fun getAll(): Flow<List<StepCount>>


    @Query("SELECT * FROM step WHERE created_at >= :startDateTime AND created_at < :endDateTime")
    fun loadAllStepsFromPeriod(startDateTime: Long, endDateTime: Long): Flow<List<StepCount>>

}