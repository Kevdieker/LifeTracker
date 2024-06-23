package com.kevker.lifetracker.factories

import com.kevker.lifetracker.viewmodels.AppUsageViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kevker.lifetracker.data.Repository
import com.kevker.lifetracker.viewmodels.*

class ViewModelFactory(
    private val repository: Repository? = null,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeScreenViewModel::class.java) -> {
                HomeScreenViewModel(repository = repository!!) as T
            }

            modelClass.isAssignableFrom(ActivityViewModel::class.java) -> {
                ActivityViewModel(repository = repository!!) as T
            }

            modelClass.isAssignableFrom(AppUsageViewModel::class.java) -> {
                AppUsageViewModel(context = context!!) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
