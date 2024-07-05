package com.kevker.lifetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kevker.lifetracker.handlers.PermissionHandler
import com.kevker.lifetracker.workers.StepCounterWorker
import java.util.concurrent.TimeUnit

class LifeTrackerApplication : Application(), Configuration.Provider {

    lateinit var permissionHandler: PermissionHandler
        private set

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        permissionHandler = PermissionHandler(this)

        val myWork = PeriodicWorkRequestBuilder<StepCounterWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "StepCounterWork",
            ExistingPeriodicWorkPolicy.KEEP,
            myWork
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "goal_notifications",
                "Goal Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for goal notifications"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}
