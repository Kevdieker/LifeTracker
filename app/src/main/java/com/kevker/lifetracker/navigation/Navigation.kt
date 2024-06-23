package com.kevker.lifetracker.navigation

import com.kevker.lifetracker.screens.AppUsageScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kevker.lifetracker.screens.ActivityScreen
import com.kevker.lifetracker.screens.HomeScreen
import com.kevker.lifetracker.screens.HydrationScreen

import com.kevker.lifetracker.screens.SleepScreen


@Composable
fun Navigation() {

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
    }
}