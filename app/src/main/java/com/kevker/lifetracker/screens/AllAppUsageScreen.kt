package com.kevker.lifetracker.screens

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AllAppUsageScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AppUsageViewModel = viewModel(factory = ViewModelFactory(context = context))

    val allAppUsages by viewModel.allAppUsages.collectAsState()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "All App Usages",
                onNavigationIconClick = { navController.popBackStack() }
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.padding(16.dp)
        ) {
            items(allAppUsages.entries.toList()) { entry ->
                AppUsageCard(packageName = entry.key, usageTime = entry.value)
            }
        }
    }
}

@Composable
fun AppUsageCard(packageName: String, usageTime: Long) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var appIcon by remember { mutableStateOf<Drawable?>(null) }
    var appName by remember { mutableStateOf("") }

    LaunchedEffect(packageName) {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            appIcon = packageManager.getApplicationIcon(appInfo)
            appName = packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            // Handle error
        }
    }

    val hours = (usageTime / 1000 / 3600).toInt()
    val minutes = ((usageTime / 1000 % 3600) / 60).toInt()
    val seconds = (usageTime / 1000 % 60).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (appIcon != null) {
                Image(
                    bitmap = appIcon!!.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = appName, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
