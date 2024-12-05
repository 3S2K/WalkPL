package org.example.project.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.example.project.core.Constants
import org.example.project.domain.model.Track
import org.example.project.domain.repository.LocalStorageRepository
import org.example.project.viewmodel.PlayerViewModel

class AndroidAudioPlayer(
    private val context: Context,
    private val localStorageRepository: LocalStorageRepository
) : AudioPlayer {
    private var audioService: BackgroundAudioService? = null
    private var isBound = false
    private lateinit var playerViewModel: PlayerViewModel
    
    fun setViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
        bindService()
    }

    override fun play(track: Track) {
        println("재생 시도: ${track.streamUrl}")
        
        if (!isBound) {
            println("서비스 바인딩 안됨, 바인딩 시도")
            bindService()
        }
        
        val fullUrl = if (!track.streamUrl.startsWith("http")) {
            "${Constants.BASE_URL}${track.streamUrl}"
        } else {
            track.streamUrl
        }
        println("최종 재생 URL: $fullUrl")
        
        audioService?.playTrack(track.copy(streamUrl = fullUrl))
    }

    override fun pause() {
        println("일시정지 시도")
        audioService?.togglePlayPause()
    }

    override fun resume() {
        println("재생 재개 시도")
        if (!isBound) bindService()
        audioService?.togglePlayPause()
    }

    override fun stop() {
        println("정지 시도")
        audioService?.stop()
    }

    private fun bindService() {
        val intent = Intent(context, BackgroundAudioService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            println("서비스 연결됨")
            val binder = service as BackgroundAudioService.LocalBinder
            audioService = binder.getService()
            audioService?.initialize(playerViewModel)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            println("서비스 연결 해제됨")
            audioService = null
            isBound = false
        }
    }

    override fun seekTo(position: Long) {
        if (!isBound) bindService()
        audioService?.seekTo(position)
    }

    override fun getCurrentPosition(): Long = 
        if (isBound) audioService?.getCurrentPosition() ?: 0L else 0L

    override fun getDuration(): Long = 
        if (isBound) audioService?.getDuration() ?: 0L else 0L

    override fun isPlaying(): Boolean = 
        if (isBound) audioService?.isPlaying() ?: false else false

    override fun onAppStateChanged(isInRecentTasks: Boolean) {
        println("앱 상태 변경: 최근 작업에 있음 = $isInRecentTasks")  // 디버그 로그
        if (!isInRecentTasks) {
            stop()  // 앱이 최근 작업에 없으면 재생 중지
        }
    }

    fun unbindService() {
        if (isBound) {
            try {
                context.unbindService(serviceConnection)
                audioService = null
                isBound = false
                println("서비스 바인딩 해제 완료")
            } catch (e: Exception) {
                println("서비스 바인딩 해제 실패: ${e.message}")
            }
        }
    }
} 