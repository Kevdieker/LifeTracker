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
import com.kevker.lifetracker.repositories.GlassRepository
import com.kevker.lifetracker.viewmodels.WeeklyHydrationViewModel
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun WeeklyHydrationScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(context)
    val glassRepository = GlassRepository.getInstance(db.glassDao())
    val viewModel: WeeklyHydrationViewModel = viewModel(factory = ViewModelFactory(context, glassRepository = glassRepository))

    val waterIntakeByDay by viewModel.waterIntakeByDay.collectAsState()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Weekly Hydration",
                onNavigationIconClick = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)) {
            waterIntakeByDay.forEach { (day, amount) ->
                Text(text = "$day: $amount ml", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
