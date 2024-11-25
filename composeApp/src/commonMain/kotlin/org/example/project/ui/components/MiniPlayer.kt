package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.viewmodel.PlayerViewModel

@Composable
fun MiniPlayer(
    viewModel: PlayerViewModel,
    onPlayerClick: () -> Unit
) {
    val currentTrack = viewModel.currentTrack.value
    val isPlaying = viewModel.isPlaying.value
    val currentPosition = viewModel.currentPosition.value
    val duration = viewModel.getDuration()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Player Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(onClick = onPlayerClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (currentTrack?.albumArt == null) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Default album art",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Track Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = currentTrack?.title ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = currentTrack?.artist ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            // Control Buttons
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }

            IconButton(onClick = { viewModel.skipToNext() }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next track button",
                    tint = Color.White
                )
            }
        }

        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
            
            // Progress track with fixed spacing
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (duration > 0) currentPosition.toFloat() / duration else 0f)
                    .fillMaxHeight()
                    .padding(end = 2.dp)  // 고정된 간격 추가
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}