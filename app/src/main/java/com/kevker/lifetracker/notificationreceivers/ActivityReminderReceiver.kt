package com.kevker.lifetracker.notificationreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kevker.lifetracker.handlers.NotificationHandler


class ActivityReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Activity Reminder"
        val notifyId = intent.getIntExtra("notificationId", 0)
        println("and here is the problem"+ notifyId)
        val notificationHandler = NotificationHandler(context)
        notificationHandler.sendNotification(title, "It's time to do this activity!",notifyId)
    }
}
