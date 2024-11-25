package org.example.project.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.example.project.viewmodel.PlayerViewModel

@Composable
fun HomeScreen(viewModel: PlayerViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                viewModel.testSongUri?.let { uri ->
                    viewModel.playTrack(uri)
                }
            }
        ) {
            Text("Play Test Song")
        }
    }
}