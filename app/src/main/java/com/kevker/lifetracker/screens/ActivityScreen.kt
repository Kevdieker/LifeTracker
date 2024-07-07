package com.kevker.lifetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.enums.Category
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.navigation.Screen
import com.kevker.lifetracker.repositories.ActivityRepository
import com.kevker.lifetracker.viewmodels.ActivityViewModel
import com.kevker.lifetracker.widget.ActivityList
import com.kevker.lifetracker.widget.ActivityTopAppBar
import com.kevker.lifetracker.widget.CategoryFilterMenu
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import kotlinx.coroutines.launch

@Composable
fun ActivityScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { LTDatabase.getDatabase(context) }
    val activityRepository = remember { ActivityRepository.getInstance(db.activityDao()) }
    val factory = remember { ViewModelFactory(context, activityRepository = activityRepository) }
    val viewModel: ActivityViewModel = viewModel(factory = factory)
    val activitiesState by viewModel.activities.collectAsState()

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val filteredActivities = activitiesState.filter { it.category == selectedCategory || selectedCategory == null }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = MaterialTheme.colorScheme.onBackground,
        drawerContent = {
            CategoryFilterMenu(
                categories = Category.values().toList(),
                selectedCategory = selectedCategory,
                onCategorySelected = {
                    selectedCategory = it
                    scope.launch { drawerState.close() }
                },
            )
        }
    ) {
        Scaffold(
            topBar = {
                ActivityTopAppBar(
                    title = "Activity Tracker  ${selectedCategory?.name ?: ""}",
                    onNavigationIconClick = {
                        scope.launch { drawerState.open() }
                    }
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
                activities = filteredActivities,
                viewModel = viewModel
            )
        }
    }
}