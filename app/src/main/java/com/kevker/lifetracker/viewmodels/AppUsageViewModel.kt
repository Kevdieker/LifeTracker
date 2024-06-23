package com.kevker.lifetracker.viewmodels

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AppUsageViewModel(context: Context) : ViewModel() {

    private val _usageTime = MutableStateFlow(0L)
    val usageTime: StateFlow<Long> = _usageTime

    private val _topAppIcon = MutableStateFlow<Drawable?>(null)
    val topAppIcon: StateFlow<Drawable?> = _topAppIcon

    private val _topAppName = MutableStateFlow("")
    val topAppName: StateFlow<String> = _topAppName

    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    init {
        fetchUsageStats(context)
    }

    fun fetchUsageStats(context: Context) {
        viewModelScope.launch {
            if (!hasUsageStatsPermission(context)) {
                promptUsageStatsPermission(context)
                return@launch
            }

            val endTime = System.currentTimeMillis()

            // Get current time and set the start time to 6 AM of the current day
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 6)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val startTime = calendar.timeInMillis

            // If current time is before 6 AM, set start time to 6 AM of the previous day
            if (endTime < startTime) {
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }

            val adjustedStartTime = calendar.timeInMillis

            Log.d("AppUsageViewModel", "lol: ${endTime - adjustedStartTime}")

            val usageStatsMap: Map<String, UsageStats> =
                usageStatsManager.queryAndAggregateUsageStats(adjustedStartTime, endTime)


            val filteredUsageStatsMap = usageStatsMap
                .filter { it.value.totalTimeInForeground > 0 }
                .filterNot {
                    //it.key.startsWith("com.google.") ||
                    it.key.startsWith("android.") ||
                            it.key.startsWith("com.samsung.")
                }

            val topUsageStat = filteredUsageStatsMap.maxByOrNull { it.value.totalTimeInForeground }

            if (topUsageStat != null) {
                val topPackageName = topUsageStat.key
                val topUsageTime = topUsageStat.value.totalTimeInForeground

                Log.d("AppUsageViewModel", "Top package name: $topPackageName")
                Log.d(
                    "AppUsageViewModel",
                    "Top total time in foreground: $topUsageTime milliseconds"
                )

                _usageTime.value = topUsageTime

                // Get the application label (actual app name)
                val packageManager = context.packageManager
                try {
                    val appInfo = packageManager.getApplicationInfo(topPackageName, 0)
                    _topAppIcon.value = packageManager.getApplicationIcon(appInfo)
                    _topAppName.value = packageManager.getApplicationLabel(appInfo)
                        .toString() // Set the actual app name
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
        }
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
