package com.kevker.lifetracker.screens

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.handlers.ActivityReminderReceiver
import com.kevker.lifetracker.handlers.NotificationHandler
import com.kevker.lifetracker.models.Activity
import com.kevker.lifetracker.repositories.ActivityRepository
import com.kevker.lifetracker.viewmodels.ActivityViewModel
import com.kevker.lifetracker.widget.SimpleTopAppBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AddEditActivityScreen(
    navController: NavController,
    activityId: Long? = null
) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(context)
    val activityRepository = ActivityRepository(activityDao = db.activityDao())
    val factory = ViewModelFactory(context = context, activityRepository = activityRepository)
    val viewModel: ActivityViewModel = viewModel(factory = factory)

    val coroutineScope = rememberCoroutineScope()
    var activity by remember { mutableStateOf<Activity?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Long?>(null) }
    var hasReminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var reminderDaysOfWeek by remember { mutableStateOf<List<Int>>(emptyList()) }
    var isTitleError by remember { mutableStateOf(false) }

    val notificationHandler = NotificationHandler(context)
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LaunchedEffect(activityId) {
        if (activityId != null) {
            coroutineScope.launch {
                viewModel.getActivityById(activityId).collect { existingActivity ->
                    existingActivity?.let {
                        title = it.title
                        description = it.description
                        date = it.date
                        hasReminder = it.hasReminder
                        reminderTime = it.reminderTime
                        reminderDaysOfWeek = it.reminderDaysOfWeek
                        activity = it
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        date?.let {
            calendar.timeInMillis = it
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                date = calendar.timeInMillis
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        reminderTime?.let {
            calendar.timeInMillis = it
        }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                reminderTime = calendar.timeInMillis
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = if (activityId == null) "Add New Activity" else "Edit Activity",
                onNavigationIconClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        isTitleError = it.isBlank()
                    },
                    label = { Text("Title") },
                    isError = isTitleError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isTitleError) {
                    Text(
                        text = "Title is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                date?.let {
                    Text(text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)), modifier = Modifier.padding(bottom = 8.dp))
                } ?: Text(text = "No Goal Date Selected", modifier = Modifier.padding(bottom = 8.dp))

                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Goal Date")
                }


                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                    Text(text = "Set Reminder")
                }

                if (hasReminder) {
                    reminderTime?.let {
                        Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)), modifier = Modifier.padding(bottom = 8.dp))
                    } ?: Text(text = "No Reminder Time Selected", modifier = Modifier.padding(bottom = 8.dp))
                    Button(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Reminder Time")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val daysOfWeek = listOf(
                        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
                    )

                    val selectedDays = remember { mutableStateListOf(*reminderDaysOfWeek.toTypedArray()) }

                    daysOfWeek.forEachIndexed { index, day ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedDays.contains(index + 1),
                                onCheckedChange = {
                                    if (it) {
                                        selectedDays.add(index + 1)
                                    } else {
                                        selectedDays.remove(index + 1)
                                    }
                                    reminderDaysOfWeek = selectedDays.toList()
                                }
                            )
                            Text(text = day)
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (activityId == null) {
                                val newActivity = Activity(
                                    title = title,
                                    description = description,
                                    date = date,
                                    hasReminder = hasReminder,
                                    reminderTime = reminderTime,
                                    reminderDaysOfWeek = reminderDaysOfWeek
                                )
                                println("hängt es sich hier auf?")
                                var activityId= viewModel.addActivity(newActivity)
                                println("genau hier")
                                if (hasReminder) {
                                    notificationHandler.scheduleWeeklyReminders(context, newActivity.copy(activityId=activityId))
                                }
                                println("oder doch hier")
                            } else {
                                activity?.let {
                                    val updatedActivity = it.copy(
                                        title = title,
                                        description = description,
                                        date = date,
                                        hasReminder = hasReminder,
                                        reminderTime = reminderTime,
                                        reminderDaysOfWeek = reminderDaysOfWeek
                                    )
                                    viewModel.updateActivity(updatedActivity)
                                    if (hasReminder) {
                                        println("reminder is set")
                                        notificationHandler.scheduleWeeklyReminders(context, it)
                                    } else {
                                        notificationHandler.cancelReminder(context, it.activityId)
                                    }
                                }
                            }
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (activityId == null) "Add Activity" else "Save Changes")
                }

            }
        }
    )
}


