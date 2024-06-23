package com.kevker.lifetracker.screens

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.viewmodels.AppUsageViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun AppUsageScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: AppUsageViewModel = viewModel(factory = ViewModelFactory(context = context))
    val usageTime by viewModel.usageTime.collectAsState()
    val topAppIcon by viewModel.topAppIcon.collectAsState()
    val topAppName by viewModel.topAppName.collectAsState()

    // Ensure data is fetched whenever the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.fetchUsageStats(context)
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "App Usage",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressBar(
                    usageTime = usageTime,
                    topAppIcon = topAppIcon,
                    topAppName = topAppName
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AvailableTime(usageTime = usageTime)
        }
    }
}

@Composable
fun CircularProgressBar(usageTime: Long, topAppIcon: Drawable?, topAppName: String) {
    val hours = (usageTime / 1000 / 3600).toInt()
    val minutes = ((usageTime / 1000 % 3600) / 60).toInt()
    val seconds = (usageTime / 1000 % 60).toInt()
    val maxTime = 2 * 60 * 60 * 1000L // 2 hours in milliseconds

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { usageTime / maxTime.toFloat() },
            modifier = Modifier.size(200.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 8.dp,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (topAppIcon != null) {
                Image(
                    bitmap = topAppIcon.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Usage Time",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = topAppName,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AvailableTime(usageTime: Long) {
    val maxTime = 2 * 60 * 60 * 1000L // 2 hours in milliseconds
    val timeAvailable = maxTime - usageTime
    val hoursAvailable = (timeAvailable / 1000 / 3600).toInt()
    val minutesAvailable = ((timeAvailable / 1000 % 3600) / 60).toInt()
    val secondsAvailable = (timeAvailable / 1000 % 60).toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Available Time: ${
                String.format(
                    "%02d:%02d:%02d",
                    hoursAvailable,
                    minutesAvailable,
                    secondsAvailable
                )
            }",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
