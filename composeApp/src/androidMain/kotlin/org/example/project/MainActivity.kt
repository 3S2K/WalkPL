package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.example.project.player.AudioPlayer
import org.example.project.player.AndroidAudioPlayer
import org.example.project.player.AndroidMetadataReader
import org.example.project.player.PlaylistManager
import org.example.project.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var playlistManager: PlaylistManager
    private lateinit var playerViewModel: PlayerViewModel
    private val permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 요청
        requestPermissions(permissions, 1)

        // raw 리소스 URI 생성
        val testSongUri = "android.resource://${packageName}/raw/test_song"

        // 필요한 인스턴스들 초기화
        playlistManager = PlaylistManager()
        val metadataReader = AndroidMetadataReader(this)
        audioPlayer = AndroidAudioPlayer(this)
        playerViewModel = PlayerViewModel(
            audioPlayer = audioPlayer,
            playlistManager = playlistManager,
            metadataReader = metadataReader,
            testSongUri = testSongUri
        )

        setContent {
            App(viewModel = playerViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (audioPlayer as? AndroidAudioPlayer)?.unbindService()
    }
}