package com.kevker.lifetracker.notificationreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kevker.lifetracker.handlers.NotificationHandler

class ActivityReminderReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_NOTIFICATION_ID = "notificationId"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Activity Reminder"
        val notifyId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        Log.d("ActivityReminderReceiver", "Notification ID: $notifyId")

        val notificationHandler = NotificationHandler(context)
        notificationHandler.sendNotification(title, "It's time to do this activity!", notifyId)
    }
}
