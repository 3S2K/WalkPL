package org.example.project

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.player.AudioPlayer
import org.example.project.player.AndroidAudioPlayer
import org.example.project.player.AndroidMetadataReader
import org.example.project.player.PlaylistManager
import org.example.project.data.remote.ContentApi
import org.example.project.data.remote.ContentApiImpl
import org.example.project.data.repository.AndroidLocalStorageRepository
import org.example.project.viewmodel.PlayerViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var playlistManager: PlaylistManager
    private lateinit var playerViewModel: PlayerViewModel
    private val mainScope = MainScope()
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_MAIN) {
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }

        // 권한 요청
        requestPermissions(permissions, 1)

        // raw 리소스 URI 생성
        val testSongUri = "android.resource://${packageName}/raw/test_song"

        // HTTP 클라이언트 설정
        val client = HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        // ContentApi 초기화
        val contentApi = ContentApiImpl(client)

        // 코루틴 스코프에서 초기화 작업 수행
        mainScope.launch {
            // LocalStorageRepository 초기화
            val localStorageRepository = AndroidLocalStorageRepository(this@MainActivity)

            // 필요한 인스턴스들 초기화
            playlistManager = PlaylistManager()
            val metadataReader = AndroidMetadataReader(this@MainActivity)
            audioPlayer = AndroidAudioPlayer(
                context = this@MainActivity,
                localStorageRepository = localStorageRepository
            )

            playerViewModel = PlayerViewModel(
                audioPlayer = audioPlayer,
                playlistManager = playlistManager,
                metadataReader = metadataReader,
                contentApi = contentApi,
                testSongUri = testSongUri
            )

            // ViewModel 초기화 후 AudioPlayer에 전달
            (audioPlayer as AndroidAudioPlayer).setViewModel(playerViewModel)

            setContent {
                App(viewModel = playerViewModel)
            }
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN) {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val tasks = activityManager.appTasks
            if (tasks.isNotEmpty()) {
                tasks[0].moveToFront()
            }
        }
        setIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
        (audioPlayer as? AndroidAudioPlayer)?.unbindService()
    }

    // 무거운 작업은 IO 스레드로 이동
    private fun loadData() {
        ioScope.launch {
            // IO 작업 수행
            withContext(Dispatchers.Main) {
                // UI 업데이트
            }
        }
    }
}