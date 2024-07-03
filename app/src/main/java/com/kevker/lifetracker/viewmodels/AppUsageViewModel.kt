package com.kevker.lifetracker.viewmodels

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import java.util.TimeZone
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AppUsageViewModel(private val context: Context) : ViewModel() {

    private val _topAppUsageTime = MutableStateFlow(0L)
    val topAppUsageTime: StateFlow<Long> = _topAppUsageTime

    private val _totalScreenTime = MutableStateFlow(0L)
    val totalScreenTime: StateFlow<Long> = _totalScreenTime

    private val _topAppIcon = MutableStateFlow<Drawable?>(null)
    val topAppIcon: StateFlow<Drawable?> = _topAppIcon

    private val _topAppName = MutableStateFlow("")
    val topAppName: StateFlow<String> = _topAppName

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppUsagePrefs", Context.MODE_PRIVATE)

    private val _screenTimeGoal = MutableStateFlow(
        sharedPreferences.getLong("screenTimeGoal", 2 * 60 * 60 * 1000L) // Default 2 hours
    )
    val screenTimeGoal: StateFlow<Long> = _screenTimeGoal

    private val _trackingStartTime = MutableStateFlow(
        sharedPreferences.getLong("trackingStartTime", getDefaultTrackingStartTime())
    )
    val trackingStartTime: StateFlow<Long> = _trackingStartTime

    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    init {
        fetchUsageStats()
    }

    fun fetchUsageStats() {
        viewModelScope.launch {
            if (!hasUsageStatsPermission(context)) {
                promptUsageStatsPermission(context)
                return@launch
            }

            val endTime = System.currentTimeMillis()
            val startTime = _trackingStartTime.value

            val usageStatsMap: Map<String, UsageStats> =
                usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

            val filteredUsageStatsMap = usageStatsMap
                .filter { it.value.totalTimeInForeground > 0 }
                .filterNot {
                    it.key.startsWith("com.sec.") ||
                            it.key.startsWith("com.kevker.lifetracker") ||
                            it.key.startsWith("android.") ||
                            it.key.startsWith("com.samsung.")
                }

            val topUsageStat = filteredUsageStatsMap.maxByOrNull { it.value.totalTimeInForeground }

            if (topUsageStat != null) {
                val topPackageName = topUsageStat.key
                val topUsageTime = topUsageStat.value.totalTimeInForeground

                _topAppUsageTime.value = topUsageTime

                // Get the application label (actual app name)
                val packageManager = context.packageManager
                try {
                    val appInfo = packageManager.getApplicationInfo(topPackageName, 0)
                    _topAppIcon.value = packageManager.getApplicationIcon(appInfo)
                    _topAppName.value = packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    Log.e(
                        "AppUsageViewModel",
                        "Error fetching app icon for package: $topPackageName",
                        e
                    )
                }
            } else {
                Log.d("AppUsageViewModel", "No app found with non-zero usage time.")
            }

            // Calculate total screen time
            val totalScreenTime = filteredUsageStatsMap.values.sumOf { it.totalTimeInForeground }
            _totalScreenTime.value = totalScreenTime
        }
    }

    fun setScreenTimeGoal(goal: Long) {
        _screenTimeGoal.value = goal
        sharedPreferences.edit().putLong("screenTimeGoal", goal).apply()
    }

    fun setTrackingStartTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Vienna")).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Ensure the date is set to today
        val now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Vienna"))
        calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

        val newStartTime = calendar.timeInMillis
        _trackingStartTime.value = newStartTime
        sharedPreferences.edit().putLong("trackingStartTime", newStartTime).apply()
        fetchUsageStats()
    }



    private fun getDefaultTrackingStartTime(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager =
            context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    private fun promptUsageStatsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        context.startActivity(intent)
    }
}
