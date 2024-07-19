package com.example.exoplayercompose

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import com.example.exoplayercompose.mobile.MainMobileActivity
import com.example.exoplayercompose.tv.MainTvActivity
import com.example.exoplayercompose.ui.theme.ExoPlayerComposeTheme

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ExoPlayerComposeTheme {
                val context = LocalContext.current
                if (isTVDevice(context)) {
                    val intent = Intent(this, MainTvActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, MainMobileActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
        }
    }
}

private fun isTVDevice(context: Context): Boolean {
    val uiModeManager = getSystemService(context, UiModeManager::class.java) as UiModeManager
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}
