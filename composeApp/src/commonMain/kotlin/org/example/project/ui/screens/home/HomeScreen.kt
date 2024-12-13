package org.example.project.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel
import androidx.compose.ui.graphics.Color
import org.example.project.domain.model.ContentType
import org.example.project.ui.screens.home.components.TrackGroup
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign

@Composable
fun HomeScreen(viewModel: PlayerViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var isGestureInProgress by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    
    // StateFlow를 State로 변환
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()

    // 뉴스 트랙과 날짜 미리 필터링
    val newsTracks = remember(tracks) {
        tracks.filter { it.type == ContentType.NEWS }
    }
    val availableDates = remember(newsTracks) {
        newsTracks.mapNotNull { it.date }.distinct().sortedDescending()
    }
    var selectedNewsDate by remember(availableDates) {
        mutableStateOf(availableDates.firstOrNull() ?: "")
    }

    fun updateSelectedTab(newTab: Int) {
        if (!isGestureInProgress && newTab in 0..2) {
            isGestureInProgress = true
            selectedTab = newTab
        }
    }
    
    val onTrackClick: (Track) -> Unit = { track ->
        if (isError) {
            showErrorSnackbar = true
        } else {
            viewModel.playTrack(track)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.White,
                indicator = { tabPositions -> 
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.White,
                        height = 2.dp
                    )
                }
            ) {
                listOf("홈", "Shorts", "뉴스").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                text = title,
                                color = if (selectedTab == index) 
                                    Color.White 
                                else 
                                    Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (selectedTab == index) 
                                        androidx.compose.ui.text.font.FontWeight.Bold
                                    else 
                                        androidx.compose.ui.text.font.FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = { isGestureInProgress = false },
                            onDragCancel = { isGestureInProgress = false }
                        ) { _, dragAmount ->
                            when {
                                dragAmount > 50 && selectedTab > 0 -> updateSelectedTab(selectedTab - 1)
                                dragAmount < -50 && selectedTab < 2 -> updateSelectedTab(selectedTab + 1)
                            }
                        }
                    }
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val direction = if (targetState > initialState) {
                            AnimatedContentTransitionScope.SlideDirection.Left
                        } else {
                            AnimatedContentTransitionScope.SlideDirection.Right
                        }
                        slideIntoContainer(towards = direction) togetherWith 
                        slideOutOfContainer(towards = direction)
                    }
                ) { targetTab ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        when (targetTab) {
                            0 -> HomeContent(
                                tracks = tracks,
                                viewModel = viewModel,
                                isLoading = isLoading,
                                isError = isError,
                                onTrackClick = onTrackClick
                            )
                            1 -> ShortsContent(
                                tracks = tracks,
                                viewModel = viewModel,
                                isLoading = isLoading,
                                isError = isError,
                                onTrackClick = onTrackClick
                            )
                            2 -> NewsContent(
                                tracks = newsTracks,
                                availableDates = availableDates,
                                selectedDate = selectedNewsDate,
                                onDateSelected = { selectedNewsDate = it },
                                viewModel = viewModel,
                                isLoading = isLoading,
                                isError = isError,
                                onTrackClick = onTrackClick
                            )
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // SnackBar
        if (showErrorSnackbar) {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { showErrorSnackbar = false }) {
                        Text("확인")
                    }
                },
                dismissAction = { showErrorSnackbar = false }
            ) {
                Text("서버에 연결할 수 없습니다\n네트워크 연결을 확인해주세요")
            }
        }
    }
}

@Composable
private fun HomeContent(
    tracks: List<Track>,
    viewModel: PlayerViewModel,
    isLoading: Boolean,
    isError: Boolean,
    onTrackClick: (Track) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("모든 음악") }
    
    if (!isLoading) {
        TrackGroup(
            tracks = tracks,
            viewModel = viewModel,
            showCategoryButtons = true,
            categories = listOf("모든 음악", "최근 추가됨"),
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            showPlayAll = true,
            emptyMessage = if (isError) "서버에 연결할 수 없습니다\n네트워크 연결을 확인해주세요" else "트랙이 없습니다",
            onTrackClick = onTrackClick,
            isError = isError
        )
    }
}

@Composable
private fun ShortsContent(
    tracks: List<Track>,
    viewModel: PlayerViewModel,
    isLoading: Boolean,
    isError: Boolean,
    onTrackClick: (Track) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("인기") }
    
    if (!isLoading) {
        TrackGroup(
            tracks = tracks.filter { it.type == ContentType.SHORTS },
            viewModel = viewModel,
            showCategoryButtons = true,
            categories = listOf("인기", "최근 추가됨"),
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            showPlayAll = true,
            emptyMessage = if (isError) "서버에 연결할 수 없습니다\n네트워크 연결을 확인해주세요" else "Shorts 컨텐츠가 없습니다",
            onTrackClick = onTrackClick,
            isError = isError
        )
    }
}

@Composable
private fun NewsContent(
    tracks: List<Track>,
    availableDates: List<String>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    viewModel: PlayerViewModel,
    isLoading: Boolean,
    isError: Boolean,
    onTrackClick: (Track) -> Unit
) {
    if (!isLoading) {
        TrackGroup(
            tracks = tracks,
            viewModel = viewModel,
            showPlayAll = true,
            showDateFilter = true,
            availableDates = availableDates,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            emptyMessage = if (isError) "서버에 연결할 수 없습니다\n네트워크 연결을 확인해주세요" else "선택한 날짜의 뉴스가 없습니다",
            onTrackClick = onTrackClick,
            isError = isError
        )
    }
}