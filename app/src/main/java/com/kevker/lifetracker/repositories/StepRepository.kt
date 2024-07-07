package com.kevker.lifetracker.repositories

import com.kevker.lifetracker.data.StepCountDao
import com.kevker.lifetracker.models.StepCount
import kotlinx.coroutines.flow.Flow

class StepRepository(private val stepsDao: StepCountDao) {

    suspend fun storeSteps(stepCount: StepCount) {
        stepsDao.insertAll(stepCount)
    }

    fun loadTodaySteps(startDateTime: Long, endDateTime: Long): Flow<List<StepCount>> {
        return stepsDao.loadAllStepsFromToday(startDateTime, endDateTime)
    }

    companion object {
        @Volatile
        private var instance: StepRepository? = null

        fun getInstance(stepsDao: StepCountDao) =
            instance ?: synchronized(this) {
                instance ?: StepRepository(stepsDao).also { instance = it }
            }
    }
}



