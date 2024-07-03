package com.kevker.lifetracker.repositories

import com.kevker.lifetracker.data.SleepDao
import com.kevker.lifetracker.models.SleepEntity
import kotlinx.coroutines.flow.Flow

class SleepRepository(private val sleepDao: SleepDao) {
    val allSleepTimes: Flow<List<SleepEntity>> = sleepDao.getAllSleepTimes()

    suspend fun insertSleepTime(sleepTime: SleepEntity) {
        sleepDao.insertSleepTime(sleepTime)
    }
}