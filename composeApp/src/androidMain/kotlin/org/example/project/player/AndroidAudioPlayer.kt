package org.example.project.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.example.project.domain.model.Track

class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var audioService: BackgroundAudioService? = null
    private var isBound = false
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BackgroundAudioService.LocalBinder
            audioService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            isBound = false
        }
    }

    init {
        bindService()
    }

    private fun bindService() {
        val intent = Intent(context, BackgroundAudioService::class.java)
        context.startService(intent) // 서비스 시작
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun play(track: Track) {
        if (!isBound) bindService()
        audioService?.playTrack(track)
    }

    override fun pause() {
        audioService?.togglePlayPause()
    }

    override fun resume() {
        if (!isBound) bindService()
        audioService?.togglePlayPause()
    }

    override fun stop() {
        audioService?.stop()
    }

    override fun seekTo(position: Long) {
        audioService?.seekTo(position)
    }

    override fun getCurrentPosition(): Long = 
        audioService?.getCurrentPosition() ?: 0L

    override fun getDuration(): Long = 
        audioService?.getDuration() ?: 0L

    override fun isPlaying(): Boolean = 
        audioService?.isPlaying() ?: false
} 