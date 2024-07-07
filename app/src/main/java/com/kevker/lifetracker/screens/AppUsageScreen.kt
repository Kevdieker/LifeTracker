package com.kevker.lifetracker.screens

import TimePickerDialog
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.navigation.Screen
import com.kevker.lifetracker.viewmodels.AppUsageViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppUsageScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AppUsageViewModel = viewModel(factory = ViewModelFactory(context = context))

    val topAppUsageTime by viewModel.topAppUsageTime.collectAsState()
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
    val formatedtime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(trackingStartTime)
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUsageStats()
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "App Usage",
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
            verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = "Tracking started at: $formatedtime",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressBar(
                totalScreenTime = totalScreenTime,
                screenTimeGoal = screenTimeGoal
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TopAppComposable(
                    usageTime = topAppUsageTime,
                    AppIcon = topAppIcon,
                    AppName = topAppName
                )
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .height(120.dp)
                        .width(1.dp)
                )
                TopAppComposable(
                    usageTime = topAppUsageTime,
                    AppIcon = topAppIcon,
                    AppName = topAppName
                )
            }

            AvailableTime(
                totalScreenTime = totalScreenTime,
                screenTimeGoal = screenTimeGoal,
            )

            Button(onClick = {
                navController.navigate(Screen.AllAppUsage.route)
            }) {
                Text(text = "View All App Usage")
            }
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Settings") },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showDialog = true }) {
                        Text("Set Screen Time Goal")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showStartTimePicker = true }) {
                        Text("Set Tracking Start Time")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSettingsDialog = false }) {
                    Text("Close")
                }
            }
        )
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

@SuppressLint("DefaultLocale")
@Composable
fun CircularProgressBar(
    totalScreenTime: Long,
    screenTimeGoal: Long
) {
    val hours = (totalScreenTime / 1000 / 3600).toInt()
    val minutes = ((totalScreenTime / 1000 % 3600) / 60).toInt()
    val seconds = (totalScreenTime / 1000 % 60).toInt()
    //val timegoal = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(screenTimeGoal)
    val hours2 = (screenTimeGoal / 1000 / 3600).toInt()
    val minutes2 = ((screenTimeGoal / 1000 % 3600) / 60).toInt()
    val seconds2 = (screenTimeGoal / 1000 % 60).toInt()
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { totalScreenTime / screenTimeGoal.toFloat() },
            modifier = Modifier.size(200.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 8.dp,
            trackColor = ProgressIndicatorDefaults.circularTrackColor,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = String.format("%02d:%02d:%02d", hours2, minutes2, seconds2),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Usage Time",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TopAppComposable(
    usageTime: Long,
    AppIcon: Drawable?,
    AppName: String,
) {
    val hours = (usageTime / 1000 / 3600).toInt()
    val minutes = ((usageTime / 1000 % 3600) / 60).toInt()
    val seconds = (usageTime / 1000 % 60).toInt()

    Box(contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (AppIcon != null) {
                Image(
                    bitmap = AppIcon.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = AppName,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Usage Time",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AvailableTime(
    totalScreenTime: Long,
    screenTimeGoal: Long,

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
                "Time over by: ${
                    String.format(
                        "%02d:%02d:%02d",
                        hoursAvailable,
                        minutesAvailable,
                        secondsAvailable
                    )
                }"
            } else {
                "Available Time: ${
                    String.format(
                        "%02d:%02d:%02d",
                        hoursAvailable,
                        minutesAvailable,
                        secondsAvailable
                    )
                }"
            },
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )
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