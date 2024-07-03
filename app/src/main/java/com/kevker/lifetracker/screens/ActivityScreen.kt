package com.kevker.lifetracker.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.repositories.ActivityRepository
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.navigation.Screen
import com.kevker.lifetracker.viewmodels.ActivityViewModel
import com.kevker.lifetracker.widget.ActivityList
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun ActivityScreen(
    navController: NavController
) {

    val db = LTDatabase.getDatabase(LocalContext.current)
    val activityRepository = ActivityRepository(activityDao = db.activityDao())
    val factory = ViewModelFactory(activityRepository = activityRepository)
    val viewModel: ActivityViewModel = viewModel(factory = factory)
    val activitiesState by viewModel.activities.collectAsState()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Activity Tracker",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddEditActivity.route)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        ActivityList(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            activities = activitiesState,
            viewModel = viewModel
        )
    }
}

