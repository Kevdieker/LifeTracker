package com.kevker.lifetracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.repositories.GlassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeeklyHydrationViewModel(private val context:Context,private val repository: GlassRepository) : ViewModel() {
    private val _waterIntakeByDay = MutableStateFlow<Map<String, Int>>(emptyMap())
    val waterIntakeByDay: StateFlow<Map<String, Int>> get() = _waterIntakeByDay

    init {
        viewModelScope.launch {
            repository.getAllGlasses().collect { glassList ->
                val intakeByDay = glassList.groupBy {
                    SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(it.timestamp))
                }.mapValues { entry ->
                    entry.value.sumOf { it.amount }
                }
                _waterIntakeByDay.value = intakeByDay
            }
        }
    }
}
