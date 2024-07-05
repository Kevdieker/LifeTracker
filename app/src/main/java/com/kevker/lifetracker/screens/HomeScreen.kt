package com.kevker.lifetracker.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.repositories.StepRepository
import com.kevker.lifetracker.viewmodels.HomeScreenViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(context)
    val stepRepository = StepRepository.getInstance(db.stepCountDao())
    val factory = ViewModelFactory(context = context)
    val viewModel: HomeScreenViewModel = viewModel(factory = factory)

    val stepCount by viewModel.stepCount.collectAsState()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Home",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Text(modifier = Modifier.padding(innerPadding), text = "Steps: $stepCount")
    }
}
