package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Playlist
import org.example.project.domain.model.RepeatMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.example.project.domain.model.Track
import kotlin.math.floor

@Composable
fun FullScreenPlayerScreen(
    track: Track,
    isPlaying: Boolean,
    isBuffering: Boolean,
    progress: Float,
    isLiked: Boolean = false,
    onProgressChange: (Float) -> Unit,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onTrackClick: () -> Unit,
    onLyricsClick: () -> Unit,
    isShuffled: Boolean,
    repeatMode: RepeatMode,
    playlists: List<Playlist>,
    showPlaylistDialog: Boolean,
    onAddToPlaylist: (String) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onDismissPlaylistDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 메인 플레이어 UI만 렌더링
    FullScreenPlayer(
        track = track,
        isPlaying = isPlaying,
        isBuffering = isBuffering,
        progress = progress,
        isLiked = isLiked,
        onProgressChange = onProgressChange,
        onPlayPauseClick = onPlayPauseClick,
        onPreviousClick = onPreviousClick,
        onNextClick = onNextClick,
        onShuffleClick = onShuffleClick,
        onRepeatClick = onRepeatClick,
        onBackClick = onBackClick,
        onMoreClick = onMoreClick,
        onAddToPlaylistClick = onAddToPlaylistClick,
        onLikeClick = onLikeClick,
        onShareClick = onShareClick,
        onTrackClick = onTrackClick,
        onLyricsClick = onLyricsClick,
        isShuffled = isShuffled,
        repeatMode = repeatMode,
        playlists = playlists,
        showPlaylistDialog = showPlaylistDialog,
        onAddToPlaylist = onAddToPlaylist,
        onCreatePlaylist = onCreatePlaylist,
        onDismissPlaylistDialog = onDismissPlaylistDialog,
        modifier = modifier
    )
}

@Composable
private fun PlaylistDialogs(
    showPlaylistDialog: Boolean,
    showNewPlaylistDialog: Boolean,
    playlists: List<Playlist>,
    onDismissPlaylistDialog: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onAddToPlaylist: (String) -> Unit,
    onNewPlaylistDialogDismiss: () -> Unit,
    onShowNewPlaylistDialog: () -> Unit
) {
    if (showPlaylistDialog) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = onDismissPlaylistDialog,
            onCreateNew = onCreatePlaylist,
            onSelectPlaylist = { playlistId ->
                onAddToPlaylist(playlistId)
                onDismissPlaylistDialog()
            },
            showNewPlaylistDialog = showNewPlaylistDialog,
            onNewPlaylistDialogDismiss = onNewPlaylistDialogDismiss,
            onShowNewPlaylistDialog = onShowNewPlaylistDialog
        )
    }

    if (showNewPlaylistDialog) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NewPlaylistDialog(
                onDismiss = onNewPlaylistDialogDismiss,
                onCreateNew = { name ->
                    onCreatePlaylist(name)
                    onNewPlaylistDialogDismiss()
                }
            )
        }
    }
}

@Composable
fun FullScreenPlayer(
    track: Track,
    isPlaying: Boolean,
    isBuffering: Boolean,
    progress: Float,
    isLiked: Boolean = false,
    onProgressChange: (Float) -> Unit,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onTrackClick: () -> Unit,
    onLyricsClick: () -> Unit,
    isShuffled: Boolean,
    repeatMode: RepeatMode,
    playlists: List<Playlist>,
    showPlaylistDialog: Boolean,
    onAddToPlaylist: (String) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onDismissPlaylistDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewPlaylistDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            TopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreClick
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AlbumArt(
                    albumArt = track.albumArt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TrackInfo(
                    title = track.title,
                    artist = track.artist
                )

                Spacer(modifier = Modifier.height(8.dp))

                ActionButtons(
                    isLiked = isLiked,
                    onAddToPlaylistClick = onAddToPlaylistClick,
                    onLikeClick = onLikeClick,
                    onShareClick = onShareClick
                )

                Spacer(modifier = Modifier.height(8.dp))

                PlayerProgress(
                    progress = progress,
                    duration = track.duration,
                    onProgressChange = onProgressChange
                )

                Spacer(modifier = Modifier.height(8.dp))

                PlayerControls(
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    isShuffled = isShuffled,
                    repeatMode = repeatMode,
                    onPlayPauseClick = onPlayPauseClick,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick,
                    onShuffleClick = onShuffleClick,
                    onRepeatClick = onRepeatClick
                )

                Spacer(modifier = Modifier.height(60.dp))

                BottomMenu(
                    onTrackClick = onTrackClick,
                    onLyricsClick = onLyricsClick
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 다이얼로그들을 별도로 분리
        PlaylistDialogs(
            showPlaylistDialog = showPlaylistDialog,
            showNewPlaylistDialog = showNewPlaylistDialog,
            playlists = playlists,
            onDismissPlaylistDialog = onDismissPlaylistDialog,
            onCreatePlaylist = onCreatePlaylist,
            onAddToPlaylist = onAddToPlaylist,
            onNewPlaylistDialogDismiss = { showNewPlaylistDialog = false },
            onShowNewPlaylistDialog = { showNewPlaylistDialog = true }
        )
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = onBackClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "닫기"
            )
        }
        
        Text(
            text = "Now Playing",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        
        FilledIconButton(
            onClick = onMoreClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기"
            )
        }
    }
}

@Composable
private fun TrackInfo(
    title: String,
    artist: String
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Start
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun AlbumArt(
    albumArt: ByteArray?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        if (albumArt != null) {
            // 임시로 기본 아이콘으로 대체
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Album Art",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(128.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Default Album Art",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(128.dp)
            )
        }
    }
}

@Composable
private fun PlayerProgress(
    progress: Float,
    duration: Long,
    onProgressChange: (Float) -> Unit
) {
    var isSliding by remember { mutableStateOf(false) }
    var localProgress by remember { mutableStateOf(progress) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = if (isSliding) localProgress else progress,
            onValueChange = { newProgress ->
                isSliding = true
                localProgress = newProgress
            },
            onValueChangeFinished = {
                isSliding = false
                onProgressChange(localProgress)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth(),
            interactionSource = remember { MutableInteractionSource() }
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration((if (isSliding) localProgress else progress * duration).toLong()),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = formatDuration(duration),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PlayerControls(
    isPlaying: Boolean,
    isBuffering: Boolean,
    isShuffled: Boolean,
    repeatMode: RepeatMode,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = onShuffleClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = if (isShuffled) MaterialTheme.colorScheme.primary else Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle"
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = onPreviousClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(36.dp)
                )
            }
            
            FilledIconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !isBuffering
            ) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "일시정지" else "재생",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            FilledIconButton(
                onClick = onNextClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        
        FilledIconButton(
            onClick = onRepeatClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = if (repeatMode != RepeatMode.NONE) MaterialTheme.colorScheme.primary else Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = when (repeatMode) {
                    RepeatMode.ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                },
                contentDescription = "Repeat"
            )
        }
    }
}

@Composable
private fun BottomMenu(
    onTrackClick: () -> Unit,
    onLyricsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        FilledIconButton(
            onClick = onTrackClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "트랙",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        FilledIconButton(
            onClick = onLyricsClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "가사",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ActionButtons(
    isLiked: Boolean,
    onAddToPlaylistClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = {
                println("재생목록에 추가 버튼 클릭됨")
                onAddToPlaylistClick()
            },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = "재생목록에 추가"
            )
        }
        
        FilledIconButton(
            onClick = onLikeClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = if (isLiked) MaterialTheme.colorScheme.primary else Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isLiked) "좋아요 취소" else "좋아요"
            )
        }
        
        FilledIconButton(
            onClick = onShareClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "공유하기"
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = floor(totalSeconds / 60.0).toInt()
    val seconds = (totalSeconds % 60).toInt()
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}