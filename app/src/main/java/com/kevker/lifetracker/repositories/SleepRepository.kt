package com.kevker.lifetracker.repositories

import com.kevker.lifetracker.data.SleepDao
import com.kevker.lifetracker.models.SleepEntity
import kotlinx.coroutines.flow.Flow

class SleepRepository private constructor(private val sleepDao: SleepDao) {
    val allSleepTimes: Flow<List<SleepEntity>> = sleepDao.getAllSleepTimes()

    suspend fun insertSleepTime(sleepTime: SleepEntity) {
        sleepDao.insertSleepTime(sleepTime)
    }

    companion object {
        @Volatile
        private var instance: SleepRepository? = null

        fun getInstance(sleepDao: SleepDao): SleepRepository =
            instance ?: synchronized(this) {
                instance ?: SleepRepository(sleepDao).also { instance = it }
            }
    }
}
