package com.kevker.lifetracker.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.enums.Category
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.repositories.ActivityRepository
import com.kevker.lifetracker.navigation.Screen
import com.kevker.lifetracker.viewmodels.ActivityViewModel
import com.kevker.lifetracker.widget.ActivityList
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar
import kotlinx.coroutines.launch

@Composable
fun ActivityScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = LTDatabase.getDatabase(context)
    val activityRepository = ActivityRepository(activityDao = db.activityDao())
    val factory = ViewModelFactory(context, activityRepository= activityRepository)
    val viewModel: ActivityViewModel = viewModel(factory = factory)
    val activitiesState by viewModel.activities.collectAsState()

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val filteredActivities = if (selectedCategory == null) {
        activitiesState
    } else {
        activitiesState.filter { it.category == selectedCategory }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = MaterialTheme.colorScheme.onBackground,
        drawerContent = {
            val categories = Category.values().toList()
            CategoryFilterMenu(
                categories = categories,
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
                SimpleTopAppBar2(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar2(
    title: String,
    onNavigationIconClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}

@Composable
fun CategoryFilterMenu(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit
) {
    Column( modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Select Category", modifier = Modifier.padding(20.dp))

        // Option for "No Category"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCategorySelected(null) }
                .padding(16.dp)
        ) {
            RadioButton(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "No Category")
        }

        // List of categories
        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCategorySelected(category)
                    }
                    .padding(16.dp)
            ) {
                RadioButton(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = category.name)
            }
        }
    }
}



