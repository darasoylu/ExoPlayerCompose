package com.example.exoplayercompose.mobile.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.exoplayercompose.R
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun MobilePlayerScreen(navController: NavController) {

    val context = LocalContext.current
    val activity = context as Activity

    val player = remember { ExoPlayer.Builder(context).build() }
    val mediaItem = MediaItem.fromUri("https://berlin-ak.ftp.media.ccc.de/congress/2019/h264-hd/36c3-11235-eng-deu-fra-36C3_Infrastructure_Review_hd.mp4")
    var progress by remember { mutableStateOf(0F) }
    var maxProgress by remember { mutableStateOf(1F) }
    var formattedRemainingTime by remember { mutableStateOf("00:00") }
    var isPlayerReady by remember { mutableStateOf(false) }
    var showController by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )

        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    isPlayerReady = true
                    maxProgress = player.duration.toFloat()
                    formattedRemainingTime =
                        formatRemainingTime(player.currentPosition, player.duration)
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (isPlayerReady) {
                    progress = player.currentPosition.toFloat()
                    formattedRemainingTime =
                        formatRemainingTime(player.currentPosition, player.duration)
                }
            }
        }
        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    if (showController) {
        LaunchedEffect(Unit) {
            delay(5000)
            showController = false
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (player.isPlaying && player.duration > 0) {
                progress = player.currentPosition.toFloat()
                formattedRemainingTime =
                    formatRemainingTime(player.currentPosition, player.duration)
            }
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                showController = !showController
            }
    ) {

        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                    this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
        )

        if (isPlayerReady) {
            AnimatedVisibility(
                visible = showController,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CustomPlayerController(
                    navController = navController,
                    player = player,
                    progress = progress,
                    maxProgress = maxProgress,
                    formattedRemainingTime = formattedRemainingTime
                )
            }

        } else {
            CircularProgressIndicator(
                color = Color.Red,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CustomPlayerController(
    navController: NavController,
    player: ExoPlayer,
    progress: Float,
    maxProgress: Float,
    formattedRemainingTime: String
) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            IconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Text(
                text = "Video Title",
                color = Color.White,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {

            Spacer(modifier = Modifier.width(250.dp))

            IconButton(
                modifier = Modifier
                    .size(50.dp),
                onClick = {
                    val newPosition = (player.currentPosition - 10000)
                    player.seekTo(newPosition)
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_replay),
                    contentDescription = "Replay",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }

            IconButton(
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f),
                onClick = {
                    if (player.isPlaying) {
                        player.pause()
                        isPlaying = false
                    } else {
                        player.play()
                        isPlaying = true
                    }
                }
            ) {
                if (isPlaying) {
                    Icon(
                        painterResource(id = R.drawable.ic_pause),
                        contentDescription = "Pause",
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                } else {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                }
            }

            IconButton(
                modifier = Modifier
                    .size(50.dp),
                onClick = {
                    val newPosition = (player.currentPosition + 10000)
                    player.seekTo(newPosition)
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_forward),
                    contentDescription = "Forward",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.width(250.dp))

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Slider(
                value = progress,
                onValueChange = { newValue ->
                    player.seekTo(newValue.toLong())
                },
                valueRange = 0f..maxProgress,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Red,
                    activeTrackColor = Color.Red,
                    inactiveTrackColor = Color.Gray
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            Text(
                text = formattedRemainingTime,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(16.dp))

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = {

                        },
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier,
                    onClick = {},
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_speed),
                        contentDescription = "Speed",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Speed",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = {

                        },
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier,
                    onClick = {},
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Lock",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Lock",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = {

                        },
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier,
                    onClick = {},
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_comment),
                        contentDescription = "Comment",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Audio & Subtitles",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = {

                        },
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier,
                    onClick = {

                    },
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_zoom),
                        contentDescription = "Zoom",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Zoom",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun formatRemainingTime(currentPosition: Long, duration: Long): String {
    val remainingMillis = duration - currentPosition
    val minutes = (remainingMillis / 60000).toInt()
    val seconds = (remainingMillis % 60000 / 1000).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

fun rotateAndFullScreen() {
    /**
    >=Android 11 (API level 30)

    val window = context.window

    val originalOrientation = activity.requestedOrientation
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    val controller = window.insetsController
    controller?.let {
    it.hide(WindowInsets.Type.systemBars())
    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    controller?.show(WindowInsets.Type.systemBars())
    activity.requestedOrientation = originalOrientation
     **/
}
