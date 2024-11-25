package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import walkpl.composeapp.generated.resources.Res
import walkpl.composeapp.generated.resources.compose_multiplatform
import androidx.compose.material3.Scaffold
import org.example.project.ui.components.BottomNavigationBar
import org.example.project.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    var currentRoute by remember { mutableStateOf("home") }

    AppTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
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