package org.example.project.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaControlReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_PLAY_PAUSE = "org.example.project.PLAY_PAUSE"
        const val ACTION_NEXT = "org.example.project.NEXT"
        const val ACTION_PREVIOUS = "org.example.project.PREVIOUS"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, BackgroundAudioService::class.java)
        serviceIntent.action = intent.action
        context.startService(serviceIntent)
    }
} 