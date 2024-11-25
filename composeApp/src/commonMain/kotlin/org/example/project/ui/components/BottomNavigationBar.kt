package org.example.project.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


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

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigateToRoute(item.route) }
            )
        }
    }
}

private data class NavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

