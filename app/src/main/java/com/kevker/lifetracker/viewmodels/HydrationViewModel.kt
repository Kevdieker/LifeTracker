package com.kevker.lifetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.models.Glass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HydrationViewModel : ViewModel() {
    private val _glasses = MutableStateFlow<List<Glass>>(emptyList())
    val glasses: StateFlow<List<Glass>> get() = _glasses

    private val _dailyGoal = MutableStateFlow(2000) // Daily goal in milliliters
    val dailyGoal: StateFlow<Int> get() = _dailyGoal

    val totalWaterIntake: Int
        get() = glasses.value.sumOf { it.ml }

    fun addGlass(glass: Glass) {
        viewModelScope.launch {
            _glasses.value = _glasses.value + glass
        }
    }

    fun removeGlass(glass: Glass) {
        viewModelScope.launch {
            _glasses.value = _glasses.value - glass
        }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            _dailyGoal.value = goal
        }
    }
}
