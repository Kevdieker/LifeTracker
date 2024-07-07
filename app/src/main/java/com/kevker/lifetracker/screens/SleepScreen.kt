package com.kevker.lifetracker.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.repositories.SleepRepository
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.handlers.NotificationHandler
import com.kevker.lifetracker.viewmodels.SleepViewModel
import com.kevker.lifetracker.widget.SetSleepTimeDialog
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

@Composable
fun SleepScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = LTDatabase.getDatabase(context)
    val sleepRepository = remember { SleepRepository.getInstance(database.sleepDao()) }
    val factory = ViewModelFactory(context,sleepRepository = sleepRepository)
    val viewModel: SleepViewModel = viewModel(factory = factory)

    val sharedPreferences = context.getSharedPreferences("LifeTrackerPrefs", Context.MODE_PRIVATE)
    val isAlarmSet = remember { mutableStateOf(sharedPreferences.getBoolean("isAlarmSet", false)) }
    val alarmTime = remember { mutableStateOf(sharedPreferences.getString("alarmTime", "20:00") ?: "20:00") }

    val sleepState by viewModel.sleepState.collectAsState()
    val yesterdaySleepDuration by viewModel.yesterdaySleepDuration.collectAsState()
    val todaySleepDuration by viewModel.todaySleepDuration.collectAsState()
    val allSleepEntries by viewModel.allSleepEntries.collectAsState()
    var bufferTime by remember { mutableStateOf(0) }
    var countdownTime by remember { mutableStateOf(0) }
    var countdownActive by remember { mutableStateOf(false) }
    var showAllEntries by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }



    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta)
    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = colors.first(),
        targetValue = colors.last(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val buttonColor by animateColorAsState(
        targetValue = if (countdownActive) animatedColor else if (sleepState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    )

    val textColor by animateColorAsState(
        targetValue = if (sleepState) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    )
    val notificationHandler = NotificationHandler(context)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            notificationHandler.sendNotification("Go to sleep!", "Sleepy time!!",0)
        } else {
            // Optionally handle the case where permission is denied
        }
    }

    LaunchedEffect(countdownTime, countdownActive) {
        if (countdownActive && countdownTime > 0) {
            delay(1000L)
            countdownTime -= 1
            if (countdownTime == 0) {
                viewModel.toggleSleepState(bufferTime)
                countdownActive = false
            }
        }
    }

    val setTime: (Int, Int) -> Unit = { hour, minute ->
        val timeString = String.format("%02d:%02d", hour, minute)
        with(sharedPreferences.edit()) {
            putString("alarmTime", timeString)
            putBoolean("isAlarmSet", true)
            apply()
        }
        alarmTime.value = timeString
        isAlarmSet.value = true
        notificationHandler.setDailySleepNotification( hour, minute)
    }

    val cancelReminder: () -> Unit = {
        with(sharedPreferences.edit()) {
            remove("alarmTime")
            putBoolean("isAlarmSet", false)
            apply()
        }
        alarmTime.value = ""
        isAlarmSet.value = false
        notificationHandler.cancelDailySleepNotification()
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Sleep Tracker",
                onNavigationIconClick = null,
                onSettingsIconClick = { showSettingsDialog = true }
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
            Button(
                onClick = {
                    if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationHandler.sendNotification("Go to sleep!", "Sleepy time!!",0)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Send Notification")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (sleepState) {
                        viewModel.toggleSleepState(0)
                    } else {
                        if (countdownActive) {
                            countdownActive = false
                            countdownTime = 0
                        } else {
                            if (bufferTime == 0) {
                                viewModel.toggleSleepState(bufferTime)
                            } else {
                                countdownTime = bufferTime * 60
                                countdownActive = true
                            }
                        }
                    }
                },
                modifier = Modifier.size(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor
                )
            ) {
                Text(
                    text = if (countdownActive) "Cancel (${countdownTime}s)" else if (sleepState) "Wake Up" else "Go to Sleep",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            val hours = TimeUnit.MILLISECONDS.toHours(yesterdaySleepDuration)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(yesterdaySleepDuration) % 60
            Text(
                text = "You got $hours hours and $minutes minutes of sleep today",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            val todayHours = TimeUnit.MILLISECONDS.toHours(todaySleepDuration)
            val todayMinutes = TimeUnit.MILLISECONDS.toMinutes(todaySleepDuration) % 60
            Text(
                text = "Siesta Time: $todayHours hours and $todayMinutes minutes",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Buffer Time: $bufferTime minutes",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = bufferTime.toFloat(),
                onValueChange = { bufferTime = it.toInt() },
                valueRange = 0f..60f,
                steps = 59,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showAllEntries = !showAllEntries }) {
                Text("Show All Sleep Entries")
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(if (isAlarmSet.value) Color.Green else Color.Red)
                    .align(Alignment.Start)
                    .padding(top = 16.dp, end = 16.dp)
            )

            if (showSettingsDialog) {
                SetSleepTimeDialog(
                    onDismiss = { showSettingsDialog = false },
                    onTimeSet = setTime,
                    onCancel = cancelReminder
                )
            }

            if (showAllEntries) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(allSleepEntries) { sleepEntry ->
                        Text(
                            text = "Start: ${viewModel.formatTime(sleepEntry.startTime)} - End: ${viewModel.formatTime(sleepEntry.endTime)}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}






