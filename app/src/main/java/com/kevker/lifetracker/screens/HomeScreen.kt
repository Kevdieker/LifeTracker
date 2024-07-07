package com.kevker.lifetracker.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.navigation.Screen
import com.kevker.lifetracker.repositories.ActivityRepository
import com.kevker.lifetracker.repositories.GlassRepository
import com.kevker.lifetracker.repositories.SleepRepository
import com.kevker.lifetracker.repositories.StepRepository
import com.kevker.lifetracker.viewmodels.HomeScreenViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(context)
    val stepRepository = StepRepository.getInstance(db.stepCountDao())
    val activityRepository = ActivityRepository.getInstance(db.activityDao())
    val glassRepository = GlassRepository.getInstance(db.glassDao())
    val sleepRepository = SleepRepository.getInstance(db.sleepDao())

    val viewModel: HomeScreenViewModel = viewModel(factory = ViewModelFactory(context, stepRepository = stepRepository))

    val stepCount by viewModel.stepCount.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var exportUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        exportUri = uri
    }

    exportUri?.let { uri ->
        val scope = rememberCoroutineScope()
        LaunchedEffect(uri) {
            scope.launch {
                exportDataToCSV(context, uri, stepRepository, activityRepository, glassRepository, sleepRepository)
            }
        }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Home",
                onNavigationIconClick = null,
                onSettingsIconClick = { showSettingsDialog = true }
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.WeeklySteps.route) }
                    .padding(16.dp)
            ) {
                Text(text = "Steps: $stepCount", modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showSettingsDialog) {
        ExportDataDialog(
            onDismiss = { showSettingsDialog = false },
            onExport = {
                exportLauncher.launch("lifetracker_export.csv")
                showSettingsDialog = false
            }
        )
    }
}

@Composable
fun ExportDataDialog(onDismiss: () -> Unit, onExport: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column {
                Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
                    Text("Export Data to CSV")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

suspend fun exportDataToCSV(
    context: Context,
    uri: Uri,
    stepRepository: StepRepository,
    activityRepository: ActivityRepository,
    glassRepository: GlassRepository,
    sleepRepository: SleepRepository
) {
    withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream, StandardCharsets.UTF_8)


                // Export activities data
                writer.write("Activities Data\n")
                val activities = activityRepository.getAllActivities().firstOrNull() ?: emptyList()
                activities.forEach {
                    writer.write("${it.title},${it.description},${it.date},${it.reminderTime},${it.category}\n")
                }

                // Export glasses data
                writer.write("Glasses Data\n")
                val glasses = glassRepository.getAllGlasses().firstOrNull() ?: emptyList()
                glasses.forEach {
                    writer.write("${it.amount},${it.timestamp}\n")
                }


                writer.flush()
                writer.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "CSV exported successfully.", Toast.LENGTH_SHORT).show()
                }
            } ?: throw Exception("Failed to open output stream.")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error exporting CSV: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


