package com.example.exoplayercompose.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exoplayercompose.tv.TvRoutes.TV_HOME_SCREEN
import com.example.exoplayercompose.tv.screens.TvHomeScreen

object TvRoutes {
    const val TV_HOME_SCREEN = "TV_HOME"
    const val TV_PLAYER_SCREEN = "TV_PLAYER"
}

@Composable
fun TvNavGraph() {
    val navController = rememberNavController()

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = TV_HOME_SCREEN,
            modifier = Modifier.padding(it)
        ) {
            composable(TV_HOME_SCREEN) {
                TvHomeScreen(navController)
            }
        }
    }
}