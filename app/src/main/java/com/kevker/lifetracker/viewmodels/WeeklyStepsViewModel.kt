package com.kevker.lifetracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.repositories.StepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class WeeklyStepsViewModel(
    private val context: Context,
    private val repository: StepRepository
) : ViewModel() {

    private val _stepsByDay = MutableStateFlow(mapOf<String, Long>())
    val stepsByDay: StateFlow<Map<String, Long>> = _stepsByDay

    init {
        fetchWeeklySteps()
    }

    private fun fetchWeeklySteps() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            val startOfWeek = calendar.timeInMillis

            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endOfWeek = calendar.timeInMillis

            repository.loadStepsForPeriod(startOfWeek, endOfWeek).collect { steps ->
                val stepsMap = mutableMapOf<String, Long>()
                steps.groupBy {
                    val cal = Calendar.getInstance().apply { timeInMillis = it.createdAt }
                    cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: "Unknown"
                }.forEach { (day, stepList) ->
                    stepsMap[day] = stepList.sumOf { it.steps }
                }
                _stepsByDay.value = stepsMap
            }
        }
    }
}
