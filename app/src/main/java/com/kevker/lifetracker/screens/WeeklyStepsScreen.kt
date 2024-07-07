package com.kevker.lifetracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.repositories.StepRepository
import com.kevker.lifetracker.viewmodels.WeeklyStepsViewModel
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun WeeklyStepsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(LocalContext.current)
    val stepRepository = StepRepository.getInstance(db.stepCountDao())
    val viewModel: WeeklyStepsViewModel = viewModel(factory = ViewModelFactory(context, stepRepository = stepRepository))

    val stepsByDay by viewModel.stepsByDay.collectAsState()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Weekly Steps",
                onNavigationIconClick = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)) {
            stepsByDay.forEach { (day, steps) ->
                Text(text = "$day: $steps steps", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
