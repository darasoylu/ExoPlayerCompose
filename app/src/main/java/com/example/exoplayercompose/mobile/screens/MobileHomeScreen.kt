package com.example.exoplayercompose.mobile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.exoplayercompose.mobile.MobileRoutes.MOBILE_PLAYER_SCREEN

@Composable
fun MobileHomeScreen(navController: NavController) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier,
            onClick = {
                navController.navigate(route = MOBILE_PLAYER_SCREEN)
            }
        ) {
            Text(text = "Watch")
        }
    }
}