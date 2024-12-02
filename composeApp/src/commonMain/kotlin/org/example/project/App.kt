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
import org.example.project.ui.theme.AppTheme
import org.example.project.viewmodel.PlayerViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(viewModel: PlayerViewModel) {
    var currentRoute by remember { mutableStateOf("home") }
    var showFullScreenPlayer by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    AppTheme {
        AndroidSystemBars()
        
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = { TopBar() },
                bottomBar = { 
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigateToRoute = { route ->
                            currentRoute = route
                        }
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    when (currentRoute) {
                        "home" -> HomeScreen(viewModel)
                        "create" -> { /* 만들기 화면 컴포넌트 */ }
                        "walk" -> { /* 걸음 화면 컴포넌트 */ }
                        "library" -> { /* 보관함 화면 컴포넌트 */ }
                    }
                }
            }
            
            // Player 영역
            if (viewModel.currentTrack.value != null) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    // MiniPlayer
                    Column {
                        MiniPlayer(
                            track = viewModel.currentTrack.value!!,
                            isPlaying = viewModel.isPlaying.value,
                            progress = viewModel.progress.value,
                            currentPosition = viewModel.currentPosition.value,
                            duration = viewModel.duration.value,
                            onPlayPauseClick = { viewModel.togglePlayPause() },
                            onNextClick = { viewModel.skipToNext() },
                            onPlayerClick = { showFullScreenPlayer = true }
                        )
                        Spacer(modifier = Modifier.height(56.dp))
                    }

                    // FullScreenPlayer as ModalBottomSheet
                    if (showFullScreenPlayer) {
                        val sheetState = rememberModalBottomSheetState(
                            skipPartiallyExpanded = true
                        )
                        val scope = rememberCoroutineScope()

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
                                    isLiked = false,
                                    onProgressChange = { progress -> 
                                        viewModel.seekTo((progress * viewModel.duration.value).toLong())
                                    },
                                    onPlayPauseClick = { viewModel.togglePlayPause() },
                                    onPreviousClick = { viewModel.skipToPrevious() },
                                    onNextClick = { viewModel.skipToNext() },
                                    onShuffleClick = { /* TODO */ },
                                    onRepeatClick = { /* TODO */ },
                                    onBackClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            showFullScreenPlayer = false
                                        }
                                    },
                                    onMoreClick = { /* TODO */ },
                                    onAddToPlaylistClick = { /* TODO */ },
                                    onLikeClick = { /* TODO */ },
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