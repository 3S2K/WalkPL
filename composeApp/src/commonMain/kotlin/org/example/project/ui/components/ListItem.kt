package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.Track
import org.example.project.domain.model.ContentType
import org.example.project.domain.model.Playlist
import org.example.project.player.PlaylistManager

@Composable
fun ListItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit,
    onMoreClick: (() -> Unit)? = null,
    isGridView: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isGridView) {
            GridContent(title, subtitle, icon)
        } else {
            ListContent(title, subtitle, icon, onMoreClick)
        }
    }
}

@Composable
private fun GridContent(
    title: String,
    subtitle: String?,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
        }
        ItemInfo(
            title = title,
            subtitle = subtitle,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun ListContent(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    onMoreClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        ItemInfo(
            title = title,
            subtitle = subtitle,
            modifier = Modifier.weight(1f)
        )
        if (onMoreClick != null) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onMoreClick)
            )
        }
    }
}

@Composable
private fun ItemInfo(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// 확장 함수로 Track과 Playlist에 대한 편의 메서드 제공
@Composable
fun Track.toListItem(
    onClick: () -> Unit,
    onMoreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val icon = when (type) {
        ContentType.SHORTS -> Icons.Default.PlayCircle
        ContentType.NEWS -> Icons.AutoMirrored.Filled.Article
        else -> Icons.Default.MusicNote
    }
    
    ListItem(
        title = title,
        subtitle = artist,
        icon = icon,
        onClick = onClick,
        onMoreClick = onMoreClick,
        modifier = modifier
    )
}

@Composable
fun Playlist.toListItem(
    onClick: () -> Unit,
    onMoreClick: (() -> Unit)? = null,
    isGridView: Boolean = false,
    modifier: Modifier = Modifier
) {
    ListItem(
        title = name,
        subtitle = "트랙 ${tracks.size}개",
        icon = PlaylistManager.getPlaylistIcon(id),
        onClick = onClick,
        onMoreClick = onMoreClick,
        isGridView = isGridView,
        modifier = modifier
    )
} 