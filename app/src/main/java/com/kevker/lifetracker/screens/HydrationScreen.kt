package com.kevker.lifetracker.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.models.Glass
import com.kevker.lifetracker.viewmodels.HydrationViewModel
import com.kevker.lifetracker.widget.GlassComposable
import com.kevker.lifetracker.widget.GlassList
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar
import com.kevker.lifetracker.widget.Waterjug

@Composable
fun HydrationScreen(navController: NavController) {
    val viewModel: HydrationViewModel = viewModel()

    val glasses by viewModel.glasses.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()

    val totalWaterIntake = glasses.sumOf { it.ml }
    val sliderPosition = totalWaterIntake / dailyGoal.toFloat()
    val percentage = (sliderPosition * 100).toInt()

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedGlass by remember { mutableStateOf<Glass?>(null) }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Hydration Tracker",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center the content
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "$totalWaterIntake ml / $dailyGoal ml",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Waterjug(
                sliderPosition = sliderPosition,
                percentage = percentage
            )

            Spacer(modifier = Modifier.height(32.dp))

            GlassList(
                glasses = glasses,
                onAddGlass = { size ->
                    if (size == -1) {
                        showDialog = true
                    } else {
                        viewModel.addGlass(Glass(size))
                    }
                },
                onDeleteGlass = { glass ->
                    selectedGlass = glass
                    showDeleteDialog = true
                }
            )
        }
    }

    AddGlassDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onAddGlass = { size ->
            viewModel.addGlass(Glass(size))
            showDialog = false
        }
    )

    DeleteGlassDialog(
        showDialog = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onDelete = {
            selectedGlass?.let { viewModel.removeGlass(it) }
            showDeleteDialog = false
        }
    )
}

@Composable
fun AddGlassDialog(showDialog: Boolean, onDismiss: () -> Unit, onAddGlass: (Int) -> Unit) {
    if (showDialog) {
        var newGlassSize by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Glass") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newGlassSize,
                        onValueChange = { newGlassSize = it },
                        label = { Text("Glass Size (ml)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val size = newGlassSize.toIntOrNull()
                    if (size != null && size > 0) {
                        onAddGlass(size)
                        onDismiss()
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DeleteGlassDialog(showDialog: Boolean, onDismiss: () -> Unit, onDelete: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Glass") },
            text = { Text("Are you sure you want to delete this glass?") },
            confirmButton = {
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
