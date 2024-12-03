package org.example.project.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel

class AndroidAudioPlayer(
    private val context: Context
) : AudioPlayer {
    private var audioService: BackgroundAudioService? = null
    private var isBound = false
    private lateinit var playerViewModel: PlayerViewModel
    private var isAppInRecentTasks = true  // 앱 상태 추적
    
    fun setViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
        bindService()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BackgroundAudioService.LocalBinder
            audioService = binder.getService()
            audioService?.initialize(playerViewModel)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            isBound = false
        }
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

    override fun onAppStateChanged(isInRecentTasks: Boolean) {
        this.isAppInRecentTasks = isInRecentTasks
        if (!isInRecentTasks) {
            // 앱이 최근 앱에서 제거되면 재생 중지
            stop()
        }
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun play(track: Track) {
        if (!isAppInRecentTasks) return  // 최근 앱에 없으면 재생하지 않음
        if (!isBound) bindService()
        audioService?.playTrack(track)
    }

    override fun pause() {
        audioService?.togglePlayPause()
    }

    override fun resume() {
        if (!isAppInRecentTasks) return  // 최근 앱에 없으면 재생하지 않음
        if (!isBound) bindService()
        audioService?.togglePlayPause()
    }

    override fun stop() {
        audioService?.stop()
    }

    override fun seekTo(position: Long) {
        if (!isAppInRecentTasks) return
        audioService?.seekTo(position)
    }

    override fun getCurrentPosition(): Long = 
        if (isAppInRecentTasks) audioService?.getCurrentPosition() ?: 0L else 0L

    override fun getDuration(): Long = 
        if (isAppInRecentTasks) audioService?.getDuration() ?: 0L else 0L

    override fun isPlaying(): Boolean = 
        if (isAppInRecentTasks) audioService?.isPlaying() ?: false else false
} 