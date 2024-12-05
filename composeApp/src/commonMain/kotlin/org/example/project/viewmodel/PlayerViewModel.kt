package org.example.project.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.example.project.domain.model.Track
import org.example.project.player.AudioPlayer
import org.example.project.player.PlaylistManager
import org.example.project.player.MetadataReader
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.project.domain.model.ContentType
import org.example.project.util.viewModelScope
import org.example.project.player.NotificationManager
import org.example.project.data.remote.ContentApi

class PlayerViewModel(
    private val audioPlayer: AudioPlayer,
    private val playlistManager: PlaylistManager,
    private val metadataReader: MetadataReader,
    private val contentApi: ContentApi,
    val testSongUri: String? = null
) : ViewModel() {
    private val _currentTrack = mutableStateOf<Track?>(null)
    val currentTrack: State<Track?> = _currentTrack

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _progress = mutableStateOf(0f)
    val progress: State<Float> = _progress

    private val _currentPosition = mutableStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _duration = mutableStateOf(0L)
    val duration: State<Long> = _duration

    private var positionUpdateJob: Job? = null

    private var notificationManager: NotificationManager? = null

    init {
        startPositionUpdates()
    }

    private fun startPositionUpdates() {
        positionUpdateJob = viewModelScope.launch {
            while (isActive) {
                _currentPosition.value = audioPlayer.getCurrentPosition()
                _duration.value = audioPlayer.getDuration()
                _progress.value = if (_duration.value > 0) {
                    _currentPosition.value.toFloat() / _duration.value
                } else {
                    0f
                }
                delay(100) // 100ms마다 업데이트
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        positionUpdateJob?.cancel()
    }

    fun playTrack(track: Track) {
        _currentTrack.value = track
        audioPlayer.play(track)
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            audioPlayer.pause()
        } else {
            audioPlayer.resume()
        }
        
        _isPlaying.value = audioPlayer.isPlaying()
        
        currentTrack.value?.let { track ->
            notificationManager?.updateNotification(track, _isPlaying.value)
        }
    }

    fun seekTo(position: Long) {
        audioPlayer.seekTo(position)
        _currentPosition.value = position
    }

    fun skipToNext() {
        playlistManager.skipToNext()?.let { nextTrack ->
            playTrack(nextTrack)
        }
    }

    fun getDuration(): Long = audioPlayer.getDuration()

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        playlistManager.setPlaylist(tracks, startIndex)
        playlistManager.getCurrentTrack()?.let { track ->
            playTrack(track)
        }
    }

    fun skipToPrevious() {
        playlistManager.skipToPrevious()?.let { previousTrack ->
            playTrack(previousTrack)
        }
    }

    fun updatePlaybackState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun setNotificationManager(manager: NotificationManager) {
        notificationManager = manager
    }

    suspend fun loadTracks(): List<Track> {
        return try {
            contentApi.getContents()
        } catch (e: Exception) {
            println("트랙 로딩 실패: ${e.message}")
            emptyList()
        }
    }
}