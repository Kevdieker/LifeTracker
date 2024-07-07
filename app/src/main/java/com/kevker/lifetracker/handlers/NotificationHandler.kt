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
import java.util.Calendar

class NotificationHandler(private val context: Context) {
    private val channelId = "goal_notifications"
    private val channelName = "Goal Notifications"
    private val channelDescription = "Notifications for goal achievements"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(title: String, message: String, notifyId:Int) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("no permission to send notification")
                return
            }
            println("notification gets send with "+notifyId)
            notify(notifyId, notification)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setDailySleepNotification(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SleepAlarmReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                1000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    // Function to cancel the alarm
    fun cancelDailySleepNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SleepAlarmReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                1000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager.cancel(pendingIntent)
    }


    @SuppressLint("ScheduleExactAlarm")
    fun scheduleWeeklyReminders(context: Context, activity: Activity) {
        println("warum kommst du nicht her")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 40)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        /*
        activity.reminderDaysOfWeek.forEach { dayOfWeek ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

         */
        println("tahts the id"+ activity.activityId)

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }


    fun cancelReminder(context: Context, activityId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ActivityReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activityId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
