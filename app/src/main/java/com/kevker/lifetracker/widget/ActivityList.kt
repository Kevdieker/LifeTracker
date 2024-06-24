package com.kevker.lifetracker.widget

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kevker.lifetracker.models.Activity
import com.kevker.lifetracker.navigation.Screen
import com.kevker.lifetracker.viewmodels.ActivityViewModel

@Composable
fun ActivityList(
    modifier: Modifier,
    navController: NavController,
    viewModel: ActivityViewModel,
    activities: List<Activity>,
) {
    LazyColumn(modifier = modifier) {
        items(activities) { activity ->
            ActivityCard(
                activity = activity,
                onDeleteClick = { viewModel.deleteActivity(it) },
                onEditClick = { navController.navigate("${Screen.AddEditActivity.route}/${activity.activityId}") }
            )
        }
    }
}
