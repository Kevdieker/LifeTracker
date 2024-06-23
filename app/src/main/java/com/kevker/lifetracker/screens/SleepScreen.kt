package com.kevker.lifetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.viewmodels.SleepViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun SleepScreen(
    navController: NavController
) {
    val viewModel: SleepViewModel = viewModel()
    val sleepState by viewModel.sleepState.collectAsState()
    val sleepTimes by viewModel.sleepTimes.collectAsState()
    val yesterdaySleepDuration by viewModel.yesterdaySleepDuration.collectAsState()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Sleep Tracker",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (sleepState) "Asleep" else "Awake",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.toggleSleepState() },
                modifier = Modifier.size(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (sleepState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    contentColor = if (sleepState) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (sleepState) "Wake Up" else "Go to Sleep",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Sleep Times:",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                sleepTimes.forEach { (start, end) ->
                    Text(
                        text = "Start: ${viewModel.formatTime(start)} - End: ${
                            if (end != 0L) viewModel.formatTime(
                                end
                            ) else "Ongoing"
                        }",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}
