package com.example.exoplayercompose.tv.screens

import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import com.example.exoplayercompose.R
import kotlinx.coroutines.delay

@Composable
fun TvPlayerScreen(navController: NavController) {

    val context = LocalContext.current

    val player = remember { ExoPlayer.Builder(context).build() }
    val mediaItem = MediaItem.fromUri("https://berlin-ak.ftp.media.ccc.de/congress/2019/h264-hd/36c3-11235-eng-deu-fra-36C3_Infrastructure_Review_hd.mp4")
    var progress by remember { mutableStateOf(0F) }
    var maxProgress by remember { mutableStateOf(1F) }
    var formattedRemainingTime by remember { mutableStateOf("00:00") }
    var isPlayerReady by remember { mutableStateOf(false) }
    var showController by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {

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
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                    descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                    //this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
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
    var isFocusedSubtitle by remember { mutableStateOf(false) }
    var isFocusedZoom by remember { mutableStateOf(false) }
    var isFocusedAudio by remember { mutableStateOf(false) }
    var isFocusedSpeed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                    .size(40.dp),
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Video Title",
                color = Color.White,
                fontSize = 24.sp,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .onFocusChanged { focusState ->
                        isFocusedSpeed = focusState.isFocused
                    },
            ) {
                if (isFocusedSpeed) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = Color.White,
                        text = "Speed",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }

                IconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_speed),
                        contentDescription = "Speed"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .onFocusChanged { focusState ->
                        isFocusedAudio = focusState.isFocused
                    },
            ) {
                if (isFocusedAudio) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = Color.White,
                        text = "Audio",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }

                IconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sound),
                        contentDescription = "Audio"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .onFocusChanged { focusState ->
                        isFocusedSubtitle = focusState.isFocused
                    },
            ) {
                if (isFocusedSubtitle) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = Color.White,
                        text = "Subtitles",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }

                IconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_comment),
                        contentDescription = "Comment"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .onFocusChanged { focusState ->
                        isFocusedZoom = focusState.isFocused
                    },
            ) {
                if (isFocusedZoom) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = Color.White,
                        text = "Zoom",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }

                IconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_zoom),
                        contentDescription = "Zoom"
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.DirectionLeft -> {
                                    player.seekTo(player.currentPosition - 15000)
                                    true
                                }

                                Key.DirectionRight -> {
                                    player.seekTo(player.currentPosition + 15000)
                                    true
                                }

                                else -> false
                            }
                        } else {
                            false
                        }
                    },
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                ),
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

            Slider(
                value = progress,
                onValueChange = { newValue ->
                    player.seekTo(newValue.toLong())
                },
                enabled = false,
                valueRange = 0f..maxProgress,
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Red,
                    disabledActiveTrackColor = Color.Red,
                    disabledInactiveTrackColor = Color.Gray
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            )

            Text(
                text = formattedRemainingTime,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}


fun formatRemainingTime(currentPosition: Long, duration: Long): String {
    val remainingMillis = duration - currentPosition
    val minutes = (remainingMillis / 60000).toInt()
    val seconds = (remainingMillis % 60000 / 1000).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}






