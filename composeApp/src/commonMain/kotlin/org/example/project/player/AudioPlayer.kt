package org.example.project.player

import org.example.project.domain.model.Track

interface AudioPlayer {
    fun play(track: Track)
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(position: Long)
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun isPlaying(): Boolean
}