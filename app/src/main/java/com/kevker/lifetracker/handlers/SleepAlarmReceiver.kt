package com.kevker.lifetracker.handlers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SleepAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHandler = NotificationHandler(context)
        notificationHandler.sendNotification("Time to Sleep", "It's 20:00! Time to wind down and prepare for bed.")
    }
}