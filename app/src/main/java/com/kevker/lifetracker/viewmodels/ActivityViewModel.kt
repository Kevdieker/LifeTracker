package com.kevker.lifetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.data.ActivityRepository
import com.kevker.lifetracker.models.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {
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
        }
    }

    fun addActivity(activity: Activity) {
        viewModelScope.launch {
            repository.add(activity)
        }
    }

    fun updateActivity(activity: Activity) {
        viewModelScope.launch {
            repository.update(activity)
        }
    }

    fun getActivityById(activityId: Long): StateFlow<Activity?> {
        val activityFlow = MutableStateFlow<Activity?>(null)
        viewModelScope.launch {
            activityFlow.value = repository.getActivityById(activityId).firstOrNull()
        }
        return activityFlow.asStateFlow()
    }
}
