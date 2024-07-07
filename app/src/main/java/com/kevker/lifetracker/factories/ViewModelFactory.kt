package com.kevker.lifetracker.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kevker.lifetracker.repositories.*
import com.kevker.lifetracker.viewmodels.*

class ViewModelFactory(
    private val applicationContext: Context,
    private val stepRepository: StepRepository? = null,
    private val activityRepository: ActivityRepository? = null,
    private val glassRepository: GlassRepository? = null,
    private val sleepRepository: SleepRepository? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeScreenViewModel::class.java) -> {
                val repo = stepRepository ?: throw IllegalArgumentException("StepRepository is required for HomeScreenViewModel")
                HomeScreenViewModel(applicationContext, repo) as T
            }
            modelClass.isAssignableFrom(WeeklyStepsViewModel::class.java) -> {
                val repo = stepRepository ?: throw IllegalArgumentException("StepRepository is required for WeeklyStepsViewModel")
                WeeklyStepsViewModel(applicationContext, repo) as T
            }
            modelClass.isAssignableFrom(ActivityViewModel::class.java) -> {
                val repo = activityRepository ?: throw IllegalArgumentException("ActivityRepository is required for ActivityViewModel")
                ActivityViewModel(applicationContext, repo) as T
            }
            modelClass.isAssignableFrom(AppUsageViewModel::class.java) -> {
                AppUsageViewModel(applicationContext) as T
            }
            modelClass.isAssignableFrom(HydrationViewModel::class.java) -> {
                val repo = glassRepository ?: throw IllegalArgumentException("GlassRepository is required for HydrationViewModel")
                HydrationViewModel(repo) as T
            }
            modelClass.isAssignableFrom(WeeklyHydrationViewModel::class.java) -> {
                val repo = glassRepository ?: throw IllegalArgumentException("GlassRepository is required for WeeklyHydrationViewModel")
                WeeklyHydrationViewModel(applicationContext, repo) as T
            }
            modelClass.isAssignableFrom(SleepViewModel::class.java) -> {
                val repo = sleepRepository ?: throw IllegalArgumentException("SleepRepository is required for SleepViewModel")
                SleepViewModel(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
