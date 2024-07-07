package com.kevker.lifetracker.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch
import java.io.OutputStream

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(LocalContext.current)
    val repository = StepRepository.getInstance(db.stepCountDao())
    val viewModel: HomeScreenViewModel = viewModel(factory = ViewModelFactory(context = context, stepRepository = repository))

    val stepCount by viewModel.stepCount.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var exportUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            exportDataToCSV(context, it)
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
                    .clickable { navController.navigate("weekly_steps") }
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

fun exportDataToCSV(context: Context, uri: Uri) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write("Hello, World!".toByteArray())
            Toast.makeText(context, "CSV exported successfully.", Toast.LENGTH_SHORT).show()
        } ?: throw Exception("Failed to open output stream.")
    } catch (e: Exception) {
        Toast.makeText(context, "Error exporting CSV: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
