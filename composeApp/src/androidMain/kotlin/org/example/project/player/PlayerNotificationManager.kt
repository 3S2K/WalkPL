package org.example.project.player

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import kotlinx.coroutines.*
import org.example.project.MainActivity
import org.example.project.R
import org.example.project.domain.model.Track
import org.example.project.viewmodel.PlayerViewModel

class PlayerNotificationManager(
    private val context: Context,
    private val player: Player,
    private val mediaSession: MediaSession,
    private val playerViewModel: PlayerViewModel
) : NotificationManager {
    private val androidNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    private val channelId = "music_playback_channel"
    private val mediaSessionCompat = MediaSessionCompat(context, "PlayerNotificationManager")

    init {
        createNotificationChannel()
        setupMediaSession()
    }

    private fun setupMediaSession() {
        mediaSessionCompat.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                playerViewModel.togglePlayPause()
                updateNotification(playerViewModel.currentTrack.value!!, playerViewModel.isPlaying.value)
            }

            override fun onPause() {
                playerViewModel.togglePlayPause()
                updateNotification(playerViewModel.currentTrack.value!!, playerViewModel.isPlaying.value)
            }

            override fun onSkipToNext() {
                playerViewModel.skipToNext()
                playerViewModel.currentTrack.value?.let { track ->
                    updateNotification(track, playerViewModel.isPlaying.value)
                }
            }

            override fun onSkipToPrevious() {
                playerViewModel.skipToPrevious()
                playerViewModel.currentTrack.value?.let { track ->
                    updateNotification(track, playerViewModel.isPlaying.value)
                }
            }
        })
        
        mediaSessionCompat.isActive = true
    }

    private var progressUpdateJob: Job? = null

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                playerViewModel.currentTrack.value?.let { track ->
                    updateNotificationWithProgress(
                        track,
                        playerViewModel.isPlaying.value,
                        playerViewModel.currentPosition.value
                    )
                }
                delay(200)
            }
        }
    }

    override fun updateNotification(track: Track, isPlaying: Boolean) {
        updateNotificationWithProgress(track, isPlaying, playerViewModel.currentPosition.value)
        startProgressUpdates()
    }

    private fun updateNotificationWithProgress(track: Track, isPlaying: Boolean, currentPosition: Long) {
        // MediaSession 상태 업데이트
        val stateBuilder = PlaybackStateCompat.Builder()
            .setState(
                if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                currentPosition,
                1.0f
            )
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        mediaSessionCompat.setPlaybackState(stateBuilder.build())
        
        // MediaSession 메타데이터 업데이트
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, playerViewModel.duration.value)
            .build()
        mediaSessionCompat.setMetadata(metadata)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = PendingIntent.getService(
            context, 0,
            Intent(context, BackgroundAudioService::class.java).apply {
                action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            context, 1,
            Intent(context, MediaButtonReceiver::class.java).apply {
                action = ACTION_NEXT
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getBroadcast(
            context, 2,
            Intent(context, MediaButtonReceiver::class.java).apply {
                action = ACTION_PREVIOUS
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, channelId)
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(context)
        }.apply {
            setSmallIcon(R.drawable.ic_notification)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_music_note))
            setContentTitle(track.title)
            setContentText(track.artist)
            setContentIntent(pendingIntent)
            setStyle(
                MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(true)
            setProgress(
                playerViewModel.duration.value.toInt(),
                currentPosition.toInt(),
                false
            )
            setColorized(true)
            setColor(0xFF000000.toInt())
            addAction(R.drawable.ic_skip_previous, "Previous", prevIntent)
            addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow,
                if (isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            addAction(R.drawable.ic_skip_next, "Next", nextIntent)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSilent(true)
            setOngoing(isPlaying)
        }.build()

        androidNotificationManager.notify(NOTIFICATION_ID, notification)
        
        // ViewModel 상태 업데이트
        playerViewModel.updatePlaybackState(isPlaying)
    }

    fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    override fun release() {
        stopProgressUpdates()
        mediaSessionCompat.release()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Playback",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }
            androidNotificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(track: Track, isPlaying: Boolean): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = PendingIntent.getService(
            context, 0,
            Intent(context, BackgroundAudioService::class.java).apply {
                action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            context, 1,
            Intent(context, MediaButtonReceiver::class.java).apply {
                action = ACTION_NEXT
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getBroadcast(
            context, 2,
            Intent(context, MediaButtonReceiver::class.java).apply {
                action = ACTION_PREVIOUS
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, channelId)
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(context)
        }.apply {
            setSmallIcon(R.drawable.ic_notification)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_music_note))
            setContentTitle(track.title)
            setContentText(track.artist)
            setContentIntent(pendingIntent)
            setStyle(
                MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(true)
            setProgress(
                playerViewModel.duration.value.toInt(),
                playerViewModel.currentPosition.value.toInt(),
                false
            )
            setColorized(true)
            setColor(0xFF000000.toInt())
            addAction(R.drawable.ic_skip_previous, "Previous", prevIntent)
            addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow,
                if (isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            addAction(R.drawable.ic_skip_next, "Next", nextIntent)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSilent(true)
            setOngoing(isPlaying)
        }.build()
    }
}

class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, BackgroundAudioService::class.java).apply {
            action = intent.action
        }
        context.startService(serviceIntent)
    }
} 