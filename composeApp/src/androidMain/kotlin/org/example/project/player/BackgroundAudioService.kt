package org.example.project.player

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import android.support.v4.media.session.MediaSessionCompat
import androidx.media3.common.PlaybackException
import org.example.project.R
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel

class BackgroundAudioService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 1
    }

    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private var currentTrack: Track? = null
    private lateinit var mediaSession: MediaSession
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var notificationManager: PlayerNotificationManager
    private lateinit var playerViewModel: PlayerViewModel
    private var isAppInRecentTasks = true
    
    inner class LocalBinder : Binder() {
        fun getService(): BackgroundAudioService = this@BackgroundAudioService
    }
    
    override fun onCreate() {
        super.onCreate()
        println("서비스 생성됨")
        
        // ExoPlayer 초기화
        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    println("재생 상태 변경: $state")
                    when (state) {
                        Player.STATE_READY -> println("준비됨")
                        Player.STATE_BUFFERING -> println("버퍼링")
                        Player.STATE_ENDED -> println("종료됨")
                        Player.STATE_IDLE -> println("대기")
                    }
                }
                
                override fun onPlayerError(error: PlaybackException) {
                    println("재생 에러: ${error.message}")
                    error.printStackTrace()
                }
            })
        }
        
        // MediaSession 초기화
        mediaSession = MediaSession.Builder(this, player).build()
        
        // MediaSessionCompat 초기화
        mediaSessionCompat = MediaSessionCompat(this, "BackgroundAudioService").apply {
            isActive = true
        }
    }

    fun initialize(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
        
        // NotificationManager는 playerViewModel이 설정된 후에 초기화
        notificationManager = PlayerNotificationManager(this, player, mediaSession, playerViewModel)
        
        currentTrack?.let { track ->
            notificationManager.updateNotification(track, player.isPlaying)
        }
    }

    fun playTrack(track: Track) {
        println("트랙 재생 시도: ${track.streamUrl}")  // 디버그 로그
        
        currentTrack = track
        val mediaItem = MediaItem.fromUri(track.streamUrl)
        player.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        startForeground(NOTIFICATION_ID, notificationManager.createNotification(track, true))
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
        currentTrack?.let { track ->
            notificationManager.updateNotification(track, player.isPlaying)
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun getCurrentPosition(): Long = player.currentPosition

    fun getDuration(): Long = player.duration

    fun isPlaying(): Boolean = player.isPlaying

    fun stop() {
        player.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mediaSession.release()
        mediaSessionCompat.release()
    }
} 



