package org.example.project.player

import org.example.project.domain.model.Track

interface NotificationManager {
    fun updateNotification(track: Track, isPlaying: Boolean)
    fun release()
} 