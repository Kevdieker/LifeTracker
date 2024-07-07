package com.kevker.lifetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.repositories.GlassRepository
import com.kevker.lifetracker.models.Glass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HydrationViewModel(private val repository: GlassRepository) : ViewModel() {
    private val _glasses = MutableStateFlow<List<Glass>>(emptyList())
    val glasses: StateFlow<List<Glass>> get() = _glasses

    private val _dailyGoal = MutableStateFlow(2000) // Daily goal in milliliters
    val dailyGoal: StateFlow<Int> get() = _dailyGoal

    init {
        fetchAllGlasses()
    }

    private fun fetchAllGlasses() {
        viewModelScope.launch {
            repository.getAllGlasses().collect { glassList ->
                _glasses.value = glassList
            }
        }
    }

    val totalWaterIntake: Int
        get() = glasses.value.sumOf { it.amount }

    fun addGlass(amount: Int) {
        viewModelScope.launch {
            repository.add(Glass(amount = amount))
        }
    }

    fun removeGlass(glass: Glass) {
        viewModelScope.launch {
            repository.delete(glass)
        }
    }

    fun setDailyGoal(goal: Int) {
        _dailyGoal.value = goal
    }
}
