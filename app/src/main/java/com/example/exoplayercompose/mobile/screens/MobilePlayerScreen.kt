package com.example.exoplayercompose.mobile.screens

import androidx.annotation.OptIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController

@OptIn(UnstableApi::class)
@Composable
fun MobilePlayerScreen(navController: NavController) {
    Text(text = "MobilePlayerScreen")
}
