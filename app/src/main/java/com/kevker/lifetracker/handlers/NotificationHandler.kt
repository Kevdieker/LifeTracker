package com.kevker.lifetracker.handlers

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kevker.lifetracker.R
import com.kevker.lifetracker.models.Activity
import com.kevker.lifetracker.notificationreceivers.ActivityReminderReceiver
import com.kevker.lifetracker.notificationreceivers.SleepAlarmReceiver
import java.util.Calendar
import android.util.Log

class NotificationHandler(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "goal_notifications"
        private const val CHANNEL_NAME = "Goal Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for goal achievements"
        private const val NOTIFICATION_ID = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(title: String, message: String, notifyId: Int) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("NotificationHandler", "No permission to send notification")
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(context)) {
            Log.d("NotificationHandler", "Notification gets sent with ID: $notifyId")
            notify(notifyId, notification)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setDailySleepNotification(hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SleepAlarmReceiver::class.java)
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        setExactAlarm(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    fun cancelDailySleepNotification() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SleepAlarmReceiver::class.java)
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID)
        alarmManager.cancel(pendingIntent)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleWeeklyReminders(activity: Activity) {
        if (!activity.hasReminder || activity.reminderTime == null || activity.reminderDaysOfWeek.isEmpty()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        activity.reminderDaysOfWeek.forEach { dayOfWeek ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                set(Calendar.HOUR_OF_DAY, Calendar.getInstance().apply { timeInMillis = activity.reminderTime }.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, Calendar.getInstance().apply { timeInMillis = activity.reminderTime }.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, ActivityReminderReceiver::class.java).apply {
                putExtra("title", activity.title)
                putExtra("notificationId", activity.activityId.toInt())
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                activity.activityId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            setExactAlarm(alarmManager, calendar.timeInMillis, pendingIntent)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setExactAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancelReminder(activityId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ActivityReminderReceiver::class.java)
        val pendingIntent = createPendingIntent(intent, activityId.toInt())
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(intent: Intent, requestCode: Int): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }


}
