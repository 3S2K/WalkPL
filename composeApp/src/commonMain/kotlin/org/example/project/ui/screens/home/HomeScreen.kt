package org.example.project.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel
import androidx.compose.ui.graphics.Color
import org.example.project.domain.model.ContentType

@Composable
fun HomeScreen(viewModel: PlayerViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.White,
                indicator = { tabPositions -> 
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.White
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("홈", color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Shorts", color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("뉴스", color = if (selectedTab == 2) Color.White else Color.White.copy(alpha = 0.6f)) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    when (selectedTab) {
                        0 -> TracksList(viewModel)
                        1 -> ShortsContent(viewModel)
                        2 -> NewsContent(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun TracksList(
    viewModel: PlayerViewModel,
    contentType: ContentType? = null
) {
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        try {
            val allTracks = viewModel.loadTracks()
            tracks = when (contentType) {
                ContentType.SHORTS -> allTracks.filter { it.type == ContentType.SHORTS }
                ContentType.NEWS -> allTracks.filter { it.type == ContentType.NEWS }
                null -> allTracks
                else -> allTracks
            }
        } catch (e: Exception) {
            println("트랙 로딩 실패: ${e.message}")
        } finally {
            isLoading = false
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            tracks.isEmpty() -> {
                val message = when (contentType) {
                    ContentType.SHORTS -> "Shorts 컨텐츠가 없습니다"
                    ContentType.NEWS -> "뉴스 컨텐츠가 없습니다"
                    else -> "트랙이 없습니다\n'music' 폴더에 MP3 파일을 추가해주세요"
                }
                Text(
                    text = message,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyColumn {
                    items(tracks) { track ->
                        TrackItem(
                            track = track,
                            onTrackClick = { viewModel.playTrack(track) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortsContent(viewModel: PlayerViewModel) {
    TracksList(viewModel = viewModel, contentType = ContentType.SHORTS)
}

@Composable
private fun NewsContent(viewModel: PlayerViewModel) {
    TracksList(viewModel = viewModel, contentType = ContentType.NEWS)
}

@Composable
private fun TrackItem(
    track: Track,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onTrackClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (track.type) {
                    ContentType.SHORTS -> Icons.Default.PlayCircle
                    ContentType.NEWS -> Icons.Default.Article
                    else -> Icons.Default.MusicNote
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기",
                tint = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}