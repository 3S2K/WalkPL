package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.BottomNavigationBar
import org.example.project.ui.components.MiniPlayer
import org.example.project.ui.components.TopBar
import org.example.project.ui.screens.home.HomeScreen
import org.example.project.ui.theme.AppTheme
import org.example.project.viewmodel.PlayerViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(viewModel: PlayerViewModel) {
    var currentRoute by remember { mutableStateOf("home") }

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
            
            // MiniPlayer를 Scaffold 밖에서 렌더링
            if (viewModel.currentTrack.value != null) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    MiniPlayer(
                        viewModel = viewModel,
                        onPlayerClick = { /* 전체화면 플레이어로 이동 */ }
                    )
                    Spacer(modifier = Modifier.height(56.dp)) // BottomNavigationBar의 높이만큼 여백
                }
            }
        }
    }
}

@Composable
expect fun AndroidSystemBars()