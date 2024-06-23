package com.kevker.lifetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.data.Repository
import com.kevker.lifetracker.models.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: Repository) : ViewModel() {
    private val _activities = MutableStateFlow(listOf<Activity>())
    val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    init {
        viewModelScope.launch {
            val activities = emptyList<Activity>()//getActivities()
            activities.forEachIndexed { index, activity ->
                val existingMovie = repository.getActivityById(activity.activityId)?.firstOrNull()
                if (existingMovie == null) {
                    val activityIndex = index + 1
                    addActivity(activity)

                }

            }
            repository.getAllActivities().collect { activityList ->
                _activities.value = activityList
            }
        }
    }

    private suspend fun addActivity(activity: Activity) {
        repository.add(activity)
    }

}
