package com.kevker.lifetracker.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kevker.lifetracker.data.ActivityRepository
import com.kevker.lifetracker.data.GlassRepository
import com.kevker.lifetracker.viewmodels.*

class ViewModelFactory(
    private val activityRepository: ActivityRepository? = null,
    private val glassRepository: GlassRepository? = null,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeScreenViewModel::class.java) -> {
                HomeScreenViewModel(repository = activityRepository!!) as T
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

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
