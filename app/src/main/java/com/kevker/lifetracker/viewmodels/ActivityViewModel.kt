package com.kevker.lifetracker.viewmodels

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.notificationreceivers.ActivityReminderReceiver
import com.kevker.lifetracker.repositories.ActivityRepository
import com.kevker.lifetracker.models.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class ActivityViewModel(private val context: Context, private val repository: ActivityRepository) : ViewModel() {
    private val _activities = MutableStateFlow(listOf<Activity>())
    val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllActivities().collect { activityList ->
                _activities.value = activityList
            }
        }
    }

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            repository.delete(activity)
            cancelReminder(activity.activityId)
        }
    }

    suspend fun addActivity(activity: Activity):Long {
         return repository.add(activity)
    }

    suspend fun updateActivity(activity: Activity) {
        return repository.update(activity)
    }

    fun getActivityById(activityId: Long): StateFlow<Activity?> {
        val activityFlow = MutableStateFlow<Activity?>(null)
        viewModelScope.launch {
            activityFlow.value = repository.getActivityById(activityId).firstOrNull()
        }
        return activityFlow.asStateFlow()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleWeeklyReminders(activity: Activity) {
        if (!activity.hasReminder || activity.reminderTime == null || activity.reminderDaysOfWeek.isEmpty()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        activity.reminderDaysOfWeek.forEach { dayOfWeek ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                timeInMillis = activity.reminderTime
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

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }
    }

    private fun cancelReminder(activityId: Long) {
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
