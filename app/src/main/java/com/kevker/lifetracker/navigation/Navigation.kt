package com.kevker.lifetracker.navigation

import com.kevker.lifetracker.screens.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kevker.lifetracker.viewmodels.ActivityViewModel

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.Sleep.route) {
            SleepScreen(navController = navController)
        }
        composable(route = Screen.Water.route) {
            HydrationScreen(navController = navController)
        }
        composable(route = Screen.Activity.route) {
            ActivityScreen(navController = navController)
        }
        composable(route = Screen.AppUsage.route) {
            AppUsageScreen(navController = navController)
        }
        composable(route = Screen.AllAppUsage.route) {
            AllAppUsageScreen(navController = navController)
        }
        composable(route = Screen.AddEditActivity.route) {
            AddEditActivityScreen(navController = navController)
        }
        composable(
            route = "${Screen.AddEditActivity.route}/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getLong("activityId")
            AddEditActivityScreen(navController = navController, activityId = activityId)
        }
    }
}
