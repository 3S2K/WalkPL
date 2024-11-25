package org.example.project.player

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import org.example.project.domain.model.Track

class BackgroundAudioService : Service() {
    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private var currentTrack: Track? = null
    private lateinit var mediaSession: MediaSession
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var notificationManager: PlayerNotificationManager
    
    inner class LocalBinder : Binder() {
        fun getService(): BackgroundAudioService = this@BackgroundAudioService
    }
    
    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSessionCompat = MediaSessionCompat(this, "BackgroundAudioService")
        mediaSession = MediaSession.Builder(this, player).build()
        notificationManager = PlayerNotificationManager(this, player, mediaSession)
        player.apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_ENDED -> {
                            // 재생 완료 시 처리
                        }
                        Player.STATE_READY -> {
                            // 재생 준비 완료 시 처리
                        }
                    }
                }
            })
        }
    }

    fun playTrack(track: Track) {
        currentTrack = track
        val mediaItem = MediaItem.fromUri(track.filePath)
        player.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        notificationManager.updateNotification(track, true)
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
        player.clearMediaItems()
        currentTrack?.let { track ->
            notificationManager.updateNotification(track, false)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        mediaSessionCompat.release()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MediaControlReceiver.ACTION_PLAY_PAUSE -> togglePlayPause()
            MediaControlReceiver.ACTION_NEXT -> skipToNext()
            MediaControlReceiver.ACTION_PREVIOUS -> skipToPrevious()
        }
        return START_NOT_STICKY
    }

    private fun skipToNext() {
        // Implement next track logic
    }

    private fun skipToPrevious() {
        // Implement previous track logic
    }
} 



