package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.example.project.ui.components.BottomNavigationBar
import org.example.project.ui.components.TopBar
import org.example.project.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var currentRoute by remember { mutableStateOf("home") }

    AppTheme {
        // Android 플랫폼에서만 실행되는 코드
        AndroidSystemBars()
        
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
                    "home" -> { /* 홈 화면 컴포넌트 */ }
                    "create" -> { /* 만들기 화면 컴포넌트 */ }
                    "walk" -> { /* 걸음 화면 컴포넌트 */ }
                    "library" -> { /* 보관함 화면 컴포넌트 */ }
                }
            }
        }
    }
}

@Composable
expect fun AndroidSystemBars()