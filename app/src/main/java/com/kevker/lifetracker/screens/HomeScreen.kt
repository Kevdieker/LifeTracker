package com.kevker.lifetracker.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import com.kevker.lifetracker.viewmodels.HomeScreenViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(LocalContext.current)
    val repository = StepRepository.getInstance(db.stepCountDao())
    val viewModel: HomeScreenViewModel = viewModel(factory = ViewModelFactory(context = context, stepRepository = repository))

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
        Column(modifier = Modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("weekly_steps") }
                    .padding(16.dp)
            ) {
                Text(text = "Steps: $stepCount", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
