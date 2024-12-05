package org.example.project.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel

@Composable
fun HomeScreen(viewModel: PlayerViewModel) {
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            tracks = viewModel.loadTracks()
            println("로드된 트랙 수: ${tracks.size}")  // 디버그 로그
            tracks.forEach { track ->
                println("트랙 정보: ${track.title} - ${track.artist}")  // 각 트랙 정보 출력
            }
        } catch (e: Exception) {
            error = e.message
            println("트랙 로딩 에러: ${e.message}")  // 에러 로그
            e.printStackTrace()
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
            error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "에러 발생")
                    Text(text = error ?: "알 수 없는 에러", 
                         style = MaterialTheme.typography.bodySmall)
                }
            }
            tracks.isEmpty() -> {
                Text(
                    text = "트랙이 없습니다\n'music' 폴더에 MP3 파일을 추가해주세요",
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
private fun TrackItem(
    track: Track,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onTrackClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Now Playing",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}