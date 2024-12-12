package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.ui.components.BottomNavigationBar
import org.example.project.ui.components.FullScreenPlayer
import org.example.project.ui.components.MiniPlayer
import org.example.project.ui.components.TopBar
import org.example.project.ui.screens.home.HomeScreen
import org.example.project.ui.screens.library.LibraryScreen
import org.example.project.ui.theme.AppTheme
import org.example.project.viewmodel.PlayerViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(viewModel: PlayerViewModel) {
    var currentRoute by remember { mutableStateOf("home") }
    var showFullScreenPlayer by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AppTheme {
        AndroidSystemBars()
        
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = { TopBar(currentRoute = currentRoute) },
                bottomBar = {
                    Column {
                        if (viewModel.currentTrack.value != null) {
                            MiniPlayer(
                                track = viewModel.currentTrack.value!!,
                                isPlaying = viewModel.isPlaying.value,
                                currentPosition = viewModel.currentPosition.value,
                                duration = viewModel.duration.value,
                                onPlayPauseClick = { viewModel.togglePlayPause() },
                                onNextClick = { viewModel.skipToNext() },
                                onPlayerClick = { showFullScreenPlayer = true }
                            )
                        }
                        BottomNavigationBar(
                            currentRoute = currentRoute,
                            onNavigateToRoute = { route ->
                                currentRoute = route
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    when (currentRoute) {
                        "home" -> HomeScreen(viewModel)
                        "create" -> { /* 만들기 화면 컴포넌트 */ }
                        "walk" -> { /* 걸음 화면 컴포넌트 */ }
                        "library" -> LibraryScreen(viewModel)
                    }
                }
            }
            
            if (viewModel.currentTrack.value != null) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    if (showFullScreenPlayer) {
                        ModalBottomSheet(
                            onDismissRequest = { 
                                scope.launch {
                                    sheetState.hide()
                                    showFullScreenPlayer = false
                                }
                            },
                            sheetState = sheetState,
                            containerColor = Color.Black,
                            dragHandle = null,
                            tonalElevation = 0.dp,
                            scrimColor = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            viewModel.currentTrack.value?.let { track ->
                                FullScreenPlayer(
                                    track = track,
                                    isPlaying = viewModel.isPlaying.value,
                                    progress = viewModel.progress.value,
                                    isLiked = viewModel.isLiked.value,
                                    isShuffled = viewModel.isShuffled.value,
                                    repeatMode = viewModel.repeatMode.value,
                                    playlists = viewModel.playlists.value,
                                    showPlaylistDialog = viewModel.showPlaylistDialog.value,
                                    onProgressChange = { progress -> 
                                        viewModel.seekTo((progress * viewModel.duration.value).toLong())
                                    },
                                    onPlayPauseClick = { viewModel.togglePlayPause() },
                                    onPreviousClick = { viewModel.skipToPrevious() },
                                    onNextClick = { viewModel.skipToNext() },
                                    onShuffleClick = { viewModel.toggleShuffle() },
                                    onRepeatClick = { viewModel.toggleRepeatMode() },
                                    onLikeClick = { viewModel.toggleLike() },
                                    onAddToPlaylistClick = { viewModel.showAddToPlaylistDialog() },
                                    onAddToPlaylist = { playlistId -> 
                                        viewModel.addToPlaylist(playlistId, track)
                                        viewModel.hideAddToPlaylistDialog()
                                    },
                                    onCreatePlaylist = { name -> 
                                        viewModel.createPlaylist(name)
                                    },
                                    onDismissPlaylistDialog = { viewModel.hideAddToPlaylistDialog() },
                                    onBackClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            showFullScreenPlayer = false
                                        }
                                    },
                                    onMoreClick = { /* TODO */ },
                                    onShareClick = { /* TODO */ },
                                    onTrackClick = { /* TODO */ },
                                    onLyricsClick = { /* TODO */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
expect fun AndroidSystemBars()