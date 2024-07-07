package com.kevker.lifetracker.viewmodels

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
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
import java.util.TimeZone

class AppUsageViewModel(private val context: Context) : ViewModel() {

    private val _topAppUsageTimeToday = MutableStateFlow(0L)
    val topAppUsageTimeToday: StateFlow<Long> = _topAppUsageTimeToday

    private val _topAppIconToday = MutableStateFlow<Drawable?>(null)
    val topAppIconToday: StateFlow<Drawable?> = _topAppIconToday

    private val _topAppNameToday = MutableStateFlow("")
    val topAppNameToday: StateFlow<String> = _topAppNameToday

    private val _topAppUsageTimeYesterday = MutableStateFlow(0L)
    val topAppUsageTimeYesterday: StateFlow<Long> = _topAppUsageTimeYesterday

    private val _topAppIconYesterday = MutableStateFlow<Drawable?>(null)
    val topAppIconYesterday: StateFlow<Drawable?> = _topAppIconYesterday

    private val _topAppNameYesterday = MutableStateFlow("")
    val topAppNameYesterday: StateFlow<String> = _topAppNameYesterday

    private val _totalScreenTime = MutableStateFlow(0L)
    val totalScreenTime: StateFlow<Long> = _totalScreenTime

    private val _allAppUsages = MutableStateFlow<Map<String, Long>>(emptyMap())
    val allAppUsages: StateFlow<Map<String, Long>> = _allAppUsages

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
            val startTimeToday = _trackingStartTime.value
            val startTimeYesterday = getStartOfYesterday()

            val usageStatsMapToday = queryUsageStats(startTimeToday, endTime)
            val usageStatsMapYesterday = queryUsageStats(startTimeYesterday, startTimeToday)

            processUsageStats(usageStatsMapToday, _topAppUsageTimeToday, _topAppIconToday, _topAppNameToday)
            processUsageStats(usageStatsMapYesterday, _topAppUsageTimeYesterday, _topAppIconYesterday, _topAppNameYesterday)

            _totalScreenTime.value = usageStatsMapToday.values.sumOf { it.totalTimeInForeground }
            _allAppUsages.value = usageStatsMapToday.mapValues { it.value.totalTimeInForeground }
        }
    }

    private suspend fun queryUsageStats(startTime: Long, endTime: Long): Map<String, UsageStats> {
        return usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
            .filter { it.value.totalTimeInForeground > 0 }
            .filterNot {
                it.key.startsWith("com.sec.") ||
                        it.key.startsWith("com.kevker.lifetracker") ||
                        it.key.startsWith("android.") ||
                        it.key.startsWith("com.samsung.")
            }
    }

    private suspend fun processUsageStats(
        usageStatsMap: Map<String, UsageStats>,
        topAppUsageTimeFlow: MutableStateFlow<Long>,
        topAppIconFlow: MutableStateFlow<Drawable?>,
        topAppNameFlow: MutableStateFlow<String>
    ) {
        val topUsageStat = usageStatsMap.maxByOrNull { it.value.totalTimeInForeground }
        topUsageStat?.let { stat ->
            val packageName = stat.key
            val usageTime = stat.value.totalTimeInForeground
            topAppUsageTimeFlow.value = usageTime

            val packageManager = context.packageManager
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                topAppIconFlow.value = packageManager.getApplicationIcon(appInfo)
                topAppNameFlow.value = packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                Log.e("AppUsageViewModel", "Error fetching app icon for package: $packageName", e)
            }
        } ?: Log.d("AppUsageViewModel", "No app found with non-zero usage time.")
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

    private fun getStartOfYesterday(): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
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
