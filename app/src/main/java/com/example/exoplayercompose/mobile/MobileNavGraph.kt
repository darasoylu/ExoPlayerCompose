package com.example.exoplayercompose.mobile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exoplayercompose.mobile.MobileRoutes.MOBILE_HOME_SCREEN
import com.example.exoplayercompose.mobile.MobileRoutes.MOBILE_PLAYER_SCREEN
import com.example.exoplayercompose.mobile.screens.MobileHomeScreen
import com.example.exoplayercompose.mobile.screens.MobilePlayerScreen

object MobileRoutes {
    const val MOBILE_HOME_SCREEN = "MOBILE_HOME"
    const val MOBILE_PLAYER_SCREEN = "MOBILE_PLAYER"
}

@Composable
fun MobileNavGraph() {
    val navController = rememberNavController()

    Scaffold {
        NavHost(
            navController = navController,
            startDestination = MOBILE_HOME_SCREEN,
            modifier = Modifier.padding(it)
        ) {
            composable(MOBILE_HOME_SCREEN) {
                MobileHomeScreen(navController)
            }
            composable(MOBILE_PLAYER_SCREEN) {
                MobilePlayerScreen(navController)
            }
        }
    }
}