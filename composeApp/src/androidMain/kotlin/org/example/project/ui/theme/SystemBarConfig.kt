package org.example.project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ConfigureSystemBars() {
    val systemUiController = rememberSystemUiController()
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    SideEffect {
        systemUiController.setStatusBarColor(
            color = surfaceColor,
            darkIcons = false // 다크 테마용
        )
        
        // 네비게이션 바도 같이 설정하려면 추가
        systemUiController.setNavigationBarColor(
            color = surfaceColor,
            darkIcons = false
        )
    }
} 