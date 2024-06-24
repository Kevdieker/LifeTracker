package com.kevker.lifetracker.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.data.SleepRepository
import com.kevker.lifetracker.models.SleepEntity
import kotlinx.coroutines.delay

class SleepWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val bufferTime = inputData.getInt("bufferTime", 0)
        val database = LTDatabase.getDatabase(applicationContext)
        val sleepRepository = SleepRepository(database.sleepDao(), applicationContext)

        // Wait for the buffer time to elapse
        delay(bufferTime * 60 * 1000L)

        // Start the sleep session
        val startTime = System.currentTimeMillis()
        val sleepEntity = SleepEntity(startTime = startTime, endTime = 0L)
        sleepRepository.insertSleepTime(sleepEntity)

        return Result.success()
    }
}
