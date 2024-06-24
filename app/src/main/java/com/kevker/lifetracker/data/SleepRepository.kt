package com.kevker.lifetracker.data

import android.content.Context
import com.kevker.lifetracker.models.SleepEntity
import kotlinx.coroutines.flow.Flow

class SleepRepository(private val sleepDao: SleepDao, val context: Context) {
    val allSleepTimes: Flow<List<SleepEntity>> = sleepDao.getAllSleepTimes()

    suspend fun insertSleepTime(sleepEntity: SleepEntity) {
        sleepDao.insertSleepTime(sleepEntity)
    }

    suspend fun updateSleepEndTime(id: Int, endTime: Long) {
        sleepDao.updateSleepEndTime(id, endTime)
    }
}
