package com.kevker.lifetracker.notificationreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.kevker.lifetracker.handlers.NotificationHandler

class SleepAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("LifeTrackerPrefs", Context.MODE_PRIVATE)
        val alarmTime = sharedPreferences.getString("alarmTime", "not set")
        val notificationHandler = NotificationHandler(context)
        notificationHandler.sendNotification("Time to Sleep", "It's $alarmTime! Time to wind down and prepare for bed.",1000)
    }
}
