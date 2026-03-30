package com.nhom.smartparking.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nhom.smartparking.presentation.ui.camera.CameraScreen
import com.nhom.smartparking.presentation.ui.dashboard.DashboardScreen
import com.nhom.smartparking.presentation.ui.history.HistoryScreen
import com.nhom.smartparking.presentation.ui.login.LoginScreen

sealed class Screen(val route: String) {
    object Login     : Screen("login")
    object Dashboard : Screen("dashboard")
    object Camera    : Screen("camera")
    object History   : Screen("history")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCamera  = { navController.navigate(Screen.Camera.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.History.route) {
            HistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}