package com.kevker.lifetracker.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.data.SleepRepository
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.viewmodels.SleepViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar
import kotlinx.coroutines.delay

@Composable
fun SleepScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = LTDatabase.getDatabase(context)
    val sleepRepository = SleepRepository(database.sleepDao(), context)
    val factory = ViewModelFactory(sleepRepository = sleepRepository)
    val viewModel: SleepViewModel = viewModel(factory = factory)

    val sleepState by viewModel.sleepState.collectAsState()
    val yesterdaySleepDuration by viewModel.yesterdaySleepDuration.collectAsState()
    val todaySleepDuration by viewModel.todaySleepDuration.collectAsState()
    val allSleepEntries by viewModel.allSleepEntries.collectAsState()
    val bufferTime by viewModel.bufferTime.collectAsState()
    val countdownTime by viewModel.countdownTime.collectAsState()
    var countdownActive by remember { mutableStateOf(false) }
    var showAllEntries by remember { mutableStateOf(false) }

    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta)
    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = colors.first(),
        targetValue = colors.last(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(countdownTime, countdownActive) {
        if (countdownActive && countdownTime > 0) {
            delay(1000L)
            viewModel.updateCountdownTime(countdownTime - 1)
            if (countdownTime - 1 == 0) {
                countdownActive = false
                viewModel.toggleSleepState(bufferTime)
            }
        }
    }

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
                onClick = {
                    if (sleepState) {
                        viewModel.toggleSleepState(0)
                    } else {
                        if (countdownActive) {
                            countdownActive = false
                            viewModel.updateCountdownTime(0)
                        } else {
                            if (bufferTime == 0) {
                                viewModel.toggleSleepState(bufferTime)
                            } else {
                                viewModel.updateCountdownTime(bufferTime * 60)
                                countdownActive = true
                                viewModel.startCountdownWorker(bufferTime)
                            }
                        }
                    }
                },
                modifier = Modifier.size(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (countdownActive) animatedColor else if (sleepState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    contentColor = if (countdownActive) animatedColor else if (sleepState) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (countdownActive) "Cancel (${countdownTime}s)" else if (sleepState) "Wake Up" else "Go to Sleep",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Sleep Times:",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                items(viewModel.sleepTimes.collectAsState().value) { (start, end) ->
                    Text(
                        text = "Start: ${viewModel.formatTime(start)} - End: ${viewModel.formatTime(end)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Slider(
                value = bufferTime.toFloat(),
                onValueChange = { viewModel.updateBufferTime(it.toInt()) },
                valueRange = 0f..30f,
                steps = 29,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
