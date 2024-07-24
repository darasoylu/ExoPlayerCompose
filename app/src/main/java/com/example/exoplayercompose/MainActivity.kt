package com.example.exoplayercompose

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.exoplayercompose.mobile.MobileNavGraph
import com.example.exoplayercompose.tv.TvNavGraph
import com.example.exoplayercompose.ui.theme.ExoPlayerComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExoPlayerComposeTheme {
                val context = LocalContext.current

                if (isTvDevice(context)) {
                    TvMainApp()
                } else {
                    MobileMainApp()
                }
            }
        }
    }
}

private fun isTvDevice(context: Context): Boolean {
    val uiModeManager = ContextCompat.getSystemService(context, UiModeManager::class.java)!!
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

@Composable
fun MobileMainApp() {
    MobileNavGraph()
}

@Composable
fun TvMainApp() {
    TvNavGraph()
}
