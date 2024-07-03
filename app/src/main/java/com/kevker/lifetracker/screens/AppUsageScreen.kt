package com.kevker.lifetracker.screens

import TimePickerDialog
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.viewmodels.AppUsageViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar
import java.util.*

@Composable
fun AppUsageScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AppUsageViewModel = viewModel(factory = ViewModelFactory(context = context))
    val usageTime by viewModel.usageTime.collectAsState()
    val totalScreenTime by viewModel.totalScreenTime.collectAsState()
    val topAppIcon by viewModel.topAppIcon.collectAsState()
    val topAppName by viewModel.topAppName.collectAsState()
    val screenTimeGoal by viewModel.screenTimeGoal.collectAsState()
    val trackingStartTime by viewModel.trackingStartTime.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newScreenTimeGoal by remember { mutableStateOf((screenTimeGoal / (60 * 60 * 1000)).toString()) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf(6) }
    var minute by remember { mutableStateOf(0) }

    // Ensure data is fetched whenever the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.fetchUsageStats()
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "App Usage",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TrackingStartTime(startTime = trackingStartTime, modifier = Modifier.align(Alignment.Start))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressBar(
                    usageTime = usageTime,
                    totalScreenTime = totalScreenTime,
                    topAppIcon = topAppIcon,
                    topAppName = topAppName,
                    screenTimeGoal = screenTimeGoal
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AvailableTime(
                totalScreenTime = totalScreenTime,
                screenTimeGoal = screenTimeGoal,
                onShowDialog = { showDialog = true },
                onShowStartTimePicker = { showStartTimePicker = true }
            )
        }
    }

    if (showDialog) {
        SetGoalDialog(
            newScreenTimeGoal = newScreenTimeGoal,
            onDismiss = { showDialog = false },
            onSetGoal = { goal ->
                viewModel.setScreenTimeGoal(goal)
                showDialog = false
            },
            onNewScreenTimeGoalChange = { newScreenTimeGoal = it }
        )
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { selectedHour, selectedMinute ->
                viewModel.setTrackingStartTime(selectedHour, selectedMinute)
                showStartTimePicker = false
            }
        )
    }
}

@Composable
fun CircularProgressBar(
    usageTime: Long,
    totalScreenTime: Long,
    topAppIcon: Drawable?,
    topAppName: String,
    screenTimeGoal: Long
) {
    val hours = (totalScreenTime / 1000 / 3600).toInt()
    val minutes = ((totalScreenTime / 1000 % 3600) / 60).toInt()
    val seconds = (totalScreenTime / 1000 % 60).toInt()

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = totalScreenTime / screenTimeGoal.toFloat(),
            modifier = Modifier.size(200.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 8.dp,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (topAppIcon != null) {
                Image(
                    bitmap = topAppIcon.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Usage Time",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = topAppName,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AvailableTime(
    totalScreenTime: Long,
    screenTimeGoal: Long,
    onShowDialog: () -> Unit,
    onShowStartTimePicker: () -> Unit
) {
    val timeAvailable = screenTimeGoal - totalScreenTime
    val overTime = timeAvailable < 0
    val absTimeAvailable = kotlin.math.abs(timeAvailable)
    val hoursAvailable = (absTimeAvailable / 1000 / 3600).toInt()
    val minutesAvailable = ((absTimeAvailable / 1000 % 3600) / 60).toInt()
    val secondsAvailable = (absTimeAvailable / 1000 % 60).toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (overTime) {
                "Time over by: ${String.format("%02d:%02d:%02d", hoursAvailable, minutesAvailable, secondsAvailable)}"
            } else {
                "Available Time: ${String.format("%02d:%02d:%02d", hoursAvailable, minutesAvailable, secondsAvailable)}"
            },
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onShowDialog) {
            Text("Set Screen Time Goal")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onShowStartTimePicker) {
            Text("Set Tracking Start Time")
        }
    }
}

@Composable
fun SetGoalDialog(
    newScreenTimeGoal: String,
    onDismiss: () -> Unit,
    onSetGoal: (Long) -> Unit,
    onNewScreenTimeGoalChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Screen Time Goal") },
        text = {
            OutlinedTextField(
                value = newScreenTimeGoal,
                onValueChange = onNewScreenTimeGoalChange,
                label = { Text("Enter screen time goal (hours)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = {
                val newGoalInMillis = newScreenTimeGoal.toLongOrNull()?.times(60 * 60 * 1000)
                if (newGoalInMillis != null) {
                    onSetGoal(newGoalInMillis)
                }
            }) {
                Text("Set Goal")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun SetStartTimeDialog(
    hour: Int,
    minute: Int,
    onDismiss: () -> Unit,
    onSetTime: (Int, Int) -> Unit,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Tracking Start Time") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hour: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = hour.toString(),
                        onValueChange = { onHourChange(it.toIntOrNull() ?: hour) },
                        label = { Text("Hour") },
                        modifier = Modifier.width(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Minute: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = minute.toString(),
                        onValueChange = { onMinuteChange(it.toIntOrNull() ?: minute) },
                        label = { Text("Minute") },
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSetTime(hour, minute)
            }) {
                Text("Set Time")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TrackingStartTime(startTime: Long, modifier: Modifier = Modifier) {
    val calendar = Calendar.getInstance().apply { timeInMillis = startTime }
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Tracking started at: ${String.format("%02d:%02d", hours, minutes)}",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
