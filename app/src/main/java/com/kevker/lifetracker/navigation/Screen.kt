package com.kevker.lifetracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen(
        route = "home-screen",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    object Water : Screen(
        route = "water-screen",
        title = "Hydration",
        selectedIcon = Icons.Filled.Place,
        unselectedIcon = Icons.Outlined.Place
    )
    object Sleep : Screen(
        route = "sleep-screen",
        title = "Sleep",
        selectedIcon = Icons.Filled.Place,
        unselectedIcon = Icons.Outlined.Place
    )
    object AppUsage : Screen(
        route = "app-usage-screen",
        title = "AppUsage",
        selectedIcon = Icons.Filled.Place,
        unselectedIcon = Icons.Outlined.Place
    )
    object Activity : Screen(
        route = "activity-screen",
        title = "Activity",
        selectedIcon = Icons.Filled.Place,
        unselectedIcon = Icons.Outlined.Place
    )
    object AddEditActivity : Screen(
        route = "add-edit-activity-screen",
        title = "Add/Edit Activity",
        selectedIcon = Icons.Filled.Place,
        unselectedIcon = Icons.Outlined.Place
    )
}
