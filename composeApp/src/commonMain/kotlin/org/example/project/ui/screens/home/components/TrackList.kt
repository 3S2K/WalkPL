package org.example.project.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel

@Composable
fun TrackList(
    tracks: List<Track>,
    viewModel: PlayerViewModel,
    emptyMessage: String = "컨텐츠가 없습니다",
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (tracks.isEmpty()) {
            Text(
                text = emptyMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
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