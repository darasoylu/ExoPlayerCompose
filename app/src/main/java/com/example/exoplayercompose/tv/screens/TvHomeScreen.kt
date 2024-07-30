package com.example.exoplayercompose.tv.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.example.exoplayercompose.tv.TvRoutes.TV_PLAYER_SCREEN

@Composable
fun TvHomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier,
            onClick = {
                navController.navigate(route = TV_PLAYER_SCREEN)
            }
        ) {
            Text(text = "Watch")
        }
    }
}