package com.kevker.lifetracker.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kevker.lifetracker.apis.StepCounter
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.repositories.StepRepository


class StepCounterWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val repository = StepRepository(LTDatabase.getDatabase(appContext).stepCountDao())
    private val stepCounter = StepCounter(appContext)

    override suspend fun doWork(): Result {
        return try {
            val stepsSinceLastReboot = stepCounter.steps()
            repository.storeSteps(stepsSinceLastReboot)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
