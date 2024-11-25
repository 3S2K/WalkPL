package org.example.project.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.core.app.NotificationCompat
import android.support.v4.media.session.MediaSessionCompat
import org.example.project.MainActivity
import org.example.project.R
import org.example.project.domain.model.Track

class PlayerNotificationManager(
    private val context: Context,
    private val player: Player,
    private val mediaSession: MediaSession
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "music_playback_channel"
    private val mediaSessionCompat = MediaSessionCompat(context, "PlayerNotificationManager")

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(track: Track, isPlaying: Boolean) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(MediaControlReceiver.ACTION_PLAY_PAUSE),
            PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            context, 1,
            Intent(MediaControlReceiver.ACTION_NEXT),
            PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getBroadcast(
            context, 2,
            Intent(MediaControlReceiver.ACTION_PREVIOUS),
            PendingIntent.FLAG_IMMUTABLE
        )

        val mediaStyle = MediaStyle()
            .setMediaSession(mediaSessionCompat.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.music_note_24px)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setContentIntent(pendingIntent)
            .setStyle(mediaStyle)
            .addAction(R.drawable.skip_previous_24px, "Previous", prevIntent)
            .addAction(
                if (isPlaying) R.drawable.pause_24px else R.drawable.play_arrow_24px,
                if (isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            .addAction(R.drawable.skip_next_24px, "Next", nextIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .setOngoing(isPlaying)
            .build()

        notificationManager.notify(1, notification)
    }
} 