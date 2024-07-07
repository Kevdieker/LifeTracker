package com.kevker.lifetracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
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
        selectedIcon = Icons.Filled.WaterDrop,
        unselectedIcon = Icons.Outlined.WaterDrop
    )

    object Sleep : Screen(
        route = "sleep-screen",
        title = "Sleep",
        selectedIcon = Icons.Filled.Bed,
        unselectedIcon = Icons.Outlined.Bed
    )

    object AppUsage : Screen(
        route = "app-usage-screen",
        title = "App Usage",
        selectedIcon = Icons.Filled.PhoneAndroid,
        unselectedIcon = Icons.Outlined.PhoneAndroid
    )

    object Activity : Screen(
        route = "activity-screen",
        title = "Activity",
        selectedIcon = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter
    )

    object AddEditActivity : Screen(
        route = "add-edit-activity-screen",
        title = "Add/Edit Activity",
    )

    object AllAppUsage : Screen(
        route = "all-app-usage-screen",
        title = "All App Usage",
    )

    object WeeklySteps : Screen(
        route = "weekly-steps-screen",
        title = "Weekly Steps"
    )

    object WeeklyHydration : Screen(
        route = "weekly-hydration-screen",
        title = "Weekly Hydration"
    )
}
