package com.kevker.lifetracker.notificationreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.kevker.lifetracker.handlers.NotificationHandler

class SleepAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val PREFS_NAME = "LifeTrackerPrefs"
        const val KEY_ALARM_TIME = "alarmTime"
        const val DEFAULT_ALARM_TIME = "not set"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alarmTime = sharedPreferences.getString(KEY_ALARM_TIME, DEFAULT_ALARM_TIME)
        Log.d("SleepAlarmReceiver", "Alarm Time: $alarmTime")

        val notificationHandler = NotificationHandler(context)
        notificationHandler.sendNotification("Time to Sleep", "It's $alarmTime! Time to wind down and prepare for bed.", 1000)
    }
}
