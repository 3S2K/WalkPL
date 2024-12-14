package org.example.project.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Playlist
import org.example.project.player.PlaylistManager
import org.example.project.ui.components.AddToPlaylistDialog
import org.example.project.ui.components.NewPlaylistDialog
import org.example.project.ui.components.ListItem
import org.example.project.ui.components.toListItem
import org.example.project.viewmodel.PlayerViewModel

@Composable
fun LibraryScreen(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    var isGridView by remember { mutableStateOf(false) }
    val playlists = viewModel.playlists.value
    var showNewPlaylistDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { /* TODO: 정렬 메뉴 표시 */ }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "정렬",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = { isGridView = !isGridView }
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                        contentDescription = if (isGridView) "리스트 보기" else "그리드 보기",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(playlists) { playlist ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            playlist.toListItem(
                                onClick = { viewModel.playPlaylist(playlist) },
                                onMoreClick = { /* 더보기 메뉴 처리 */ },
                                isGridView = true
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(playlists) { playlist ->
                        playlist.toListItem(
                            onClick = { viewModel.playPlaylist(playlist) },
                            onMoreClick = { /* 더보기 메뉴 처리 */ }
                        )
                    }
                }
            }
        }
        
        // 재생목록 추가 버튼
        AddPlaylistButton(
            onClick = { showNewPlaylistDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    // 새 재생목록 만들기 다이얼로그
    if (showNewPlaylistDialog) {
        NewPlaylistDialog(
            onDismiss = { showNewPlaylistDialog = false },
            onCreateNew = { name ->
                viewModel.createPlaylist(name)
            }
        )
    }
}

@Composable
private fun AddPlaylistButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "플레이리스트 추가",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun formatTrackCount(count: Int): String {
    return "트랙 ${count}개"
}