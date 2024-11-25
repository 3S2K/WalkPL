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
import org.example.project.util.viewModelScope

class PlayerViewModel(
    private val audioPlayer: AudioPlayer,
    private val playlistManager: PlaylistManager,
    private val metadataReader: MetadataReader,
    val testSongUri: String? = null
) : ViewModel() {
    private val _currentTrack = mutableStateOf<Track?>(null)
    val currentTrack: State<Track?> = _currentTrack

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentPosition = mutableStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private var positionUpdateJob: Job? = null

    init {
        startPositionUpdates()
    }

    private fun startPositionUpdates() {
        positionUpdateJob = viewModelScope.launch {
            while (isActive) {
                _currentPosition.value = audioPlayer.getCurrentPosition()
                delay(16) // 약 60fps로 업데이트 (더 부드러운 움직임)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        positionUpdateJob?.cancel()
    }

    fun playTrack(filePath: String) {
        val metadata = metadataReader.getMetadata(filePath)
        val track = Track(
            id = filePath,
            title = metadata.title ?: "Unknown Title",
            artist = metadata.artist ?: "Unknown Artist",
            filePath = filePath,
            album = metadata.album,
            duration = metadata.duration
        )
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
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(position: Long) {
        audioPlayer.seekTo(position)
        _currentPosition.value = position
    }

    fun skipToNext() {
        // PlaylistManager를 통해 다음 곡으로 이동
        playlistManager.skipToNext()?.let { nextTrack ->
            playTrack(nextTrack.filePath)
        }
    }

    fun getDuration(): Long = audioPlayer.getDuration()

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        playlistManager.setPlaylist(tracks, startIndex)
        playlistManager.getCurrentTrack()?.let { track ->
            playTrack(track.filePath)
        }
    }

    fun skipToPrevious() {
        playlistManager.skipToPrevious()?.let { previousTrack ->
            playTrack(previousTrack.filePath)
        }
    }
}