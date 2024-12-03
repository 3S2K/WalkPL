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
        player = ExoPlayer.Builder(this).build()
        mediaSessionCompat = MediaSessionCompat(this, "BackgroundAudioService")
        mediaSession = MediaSession.Builder(this, player).build()

        player.apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_ENDED -> {
                            playerViewModel.updatePlaybackState(false)
                            currentTrack?.let { track ->
                                notificationManager.updateNotification(track, false)
                            }
                        }
                        Player.STATE_READY -> {
                            playerViewModel.updatePlaybackState(player.isPlaying)
                            currentTrack?.let { track ->
                                notificationManager.updateNotification(track, player.isPlaying)
                            }
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    playerViewModel.updatePlaybackState(isPlaying)
                    currentTrack?.let { track ->
                        notificationManager.updateNotification(track, isPlaying)
                    }
                }
            })
        }
    }

    fun initialize(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
        if (!::notificationManager.isInitialized) {
            notificationManager = PlayerNotificationManager(this, player, mediaSession, playerViewModel)
            playerViewModel.setNotificationManager(notificationManager)
            
            currentTrack?.let { track ->
                notificationManager.updateNotification(track, player.isPlaying)
            }
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



