package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit
) {
    val items = listOf(
        NavigationItem(
            title = "홈",
            route = "home",
            icon = Icons.Default.Home
        ),
        NavigationItem(
            title = "만들기",
            route = "create",
            icon = Icons.Default.Add
        ),
        NavigationItem(
            title = "걸음",
            route = "walk",
            icon = Icons.AutoMirrored.Filled.DirectionsWalk
        ),
        NavigationItem(
            title = "보관함",
            route = "library",
            icon = Icons.Default.LibraryMusic
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.height(64.dp)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 0.dp)
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = if (currentRoute == item.route) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.title,
                                color = if (currentRoute == item.route) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                label = null,
                selected = currentRoute == item.route,
                onClick = { onNavigateToRoute(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

private data class NavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

// 터치 효과를 제거하기 위한 커스텀 InteractionSource
private class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()
    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction) = true
}

