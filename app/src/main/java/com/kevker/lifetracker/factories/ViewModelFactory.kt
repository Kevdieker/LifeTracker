package com.kevker.lifetracker.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kevker.lifetracker.repositories.*
import com.kevker.lifetracker.handlers.PermissionHandler
import com.kevker.lifetracker.viewmodels.*

class ViewModelFactory(
    private val stepRepository: StepRepository? = null,
    private val activityRepository: ActivityRepository? = null,
    private val glassRepository: GlassRepository? = null,
    private val sleepRepository: SleepRepository? = null,
    private val context: Context? = null,
    private val permissionHandler: PermissionHandler? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeScreenViewModel::class.java) -> {
                HomeScreenViewModel(context!!, permissionHandler = permissionHandler!!) as T
            }
            modelClass.isAssignableFrom(ActivityViewModel::class.java) -> {
                ActivityViewModel(repository = activityRepository!!) as T
            }
            modelClass.isAssignableFrom(AppUsageViewModel::class.java) -> {
                AppUsageViewModel(context = context!!) as T
            }
            modelClass.isAssignableFrom(HydrationViewModel::class.java) -> {
                HydrationViewModel(repository = glassRepository!!) as T
            }
            modelClass.isAssignableFrom(SleepViewModel::class.java) -> {
                SleepViewModel(repository = sleepRepository!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
