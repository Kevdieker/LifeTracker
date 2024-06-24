package com.kevker.lifetracker.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.data.Repository
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.models.Activity
import com.kevker.lifetracker.viewmodels.ActivityViewModel
import com.kevker.lifetracker.widget.SimpleTopAppBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun AddEditActivityScreen(
    navController: NavController,
    activityId: Long? = null
) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(context)
    val repository = Repository(activityDao = db.activityDao())
    val factory = ViewModelFactory(repository = repository)
    val viewModel: ActivityViewModel = viewModel(factory = factory)

    val coroutineScope = rememberCoroutineScope()
    var activity by remember { mutableStateOf<Activity?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isTitleError by remember { mutableStateOf(false) }

    LaunchedEffect(activityId) {
        if (activityId != null) {

            coroutineScope.launch {
                viewModel.getActivityById(activityId).collect { existingActivity ->
                    existingActivity?.let {
                        title = it.title
                        description = it.description
                        activity = it
                    }
                }
            }
        }
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
                    .padding(16.dp),
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                            coroutineScope.launch {
                                    if (activityId == null) {
                                        viewModel.addActivity(Activity(title = title, description = description))
                                    } else {
                                        activity?.let {
                                            viewModel.updateActivity(
                                                it.copy(
                                                    title = title,
                                                    description = description
                                                )
                                            )
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
