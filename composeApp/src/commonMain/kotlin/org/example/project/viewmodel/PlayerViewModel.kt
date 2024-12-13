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
import org.example.project.player.NotificationManager
import org.example.project.data.remote.ContentApi
import org.example.project.domain.model.RepeatMode
import org.example.project.domain.model.Playlist
import androidx.compose.runtime.mutableStateListOf

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

    private val _isShuffled = mutableStateOf(false)
    val isShuffled: State<Boolean> = _isShuffled

    private val _repeatMode = mutableStateOf(RepeatMode.NONE)
    val repeatMode: State<RepeatMode> = _repeatMode

    private var positionUpdateJob: Job? = null

    private var notificationManager: NotificationManager? = null

    private val _playlists = mutableStateOf<List<Playlist>>(emptyList())
    val playlists: State<List<Playlist>> = _playlists

    private val _showPlaylistDialog = mutableStateOf(false)
    val showPlaylistDialog: State<Boolean> = _showPlaylistDialog

    private val _isLiked = mutableStateOf(false)
    val isLiked: State<Boolean> = _isLiked

    private val _likedTracks = mutableStateListOf<Track>()

    private val _isBuffering = mutableStateOf(false)
    val isBuffering: State<Boolean> = _isBuffering

    init {
        startPositionUpdates()
        updatePlaylists()
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
                delay(100)
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
        checkIsLiked(track)
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
        viewModelScope.launch {
            try {
                _isBuffering.value = true
                _isPlaying.value = false
                
                audioPlayer.seekTo(position)
                _currentPosition.value = position
                
                while (!audioPlayer.isPlaying()) {
                    delay(50)
                }
                
                _isBuffering.value = false
                _isPlaying.value = true
                
                currentTrack.value?.let { track ->
                    notificationManager?.updateNotification(track, true)
                }
            } catch (e: Exception) {
                println("시크 작업 실패: ${e.message}")
                _isBuffering.value = false
                _isPlaying.value = audioPlayer.isPlaying()
            }
        }
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

    fun toggleShuffle() {
        playlistManager.toggleShuffle()
        _isShuffled.value = playlistManager.isShuffled()
    }

    fun toggleRepeatMode() {
        val nextMode = when (_repeatMode.value) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
        playlistManager.setRepeatMode(nextMode)
        _repeatMode.value = nextMode
    }

    fun createPlaylist(name: String) {
        playlistManager.createPlaylist(name)
        updatePlaylists()
    }

    fun addToPlaylist(playlistId: String, track: Track) {
        playlistManager.addToPlaylist(playlistId, track)
        if (playlistId == PlaylistManager.LIKES_PLAYLIST_ID) {
            _isLiked.value = true
        }
        updatePlaylists()
    }

    fun removeFromPlaylist(playlistId: String, track: Track) {
        playlistManager.removeFromPlaylist(playlistId, track)
        if (playlistId == PlaylistManager.LIKES_PLAYLIST_ID) {
            _isLiked.value = false
        }
        updatePlaylists()
    }

    fun showAddToPlaylistDialog() {
        _showPlaylistDialog.value = true
    }

    fun hideAddToPlaylistDialog() {
        _showPlaylistDialog.value = false
    }

    fun toggleLike() {
        currentTrack.value?.let { track ->
            val isCurrentlyLiked = playlistManager.getPlaylist(PlaylistManager.LIKES_PLAYLIST_ID)
                ?.tracks
                ?.contains(track) ?: false
            
            if (!isCurrentlyLiked) {
                playlistManager.addToPlaylist(PlaylistManager.LIKES_PLAYLIST_ID, track)
            } else {
                playlistManager.removeFromPlaylist(PlaylistManager.LIKES_PLAYLIST_ID, track)
            }
            
            _isLiked.value = !isCurrentlyLiked
            updatePlaylists()
        }
    }

    private fun checkIsLiked(track: Track) {
        _isLiked.value = playlistManager.getPlaylist(PlaylistManager.LIKES_PLAYLIST_ID)
            ?.tracks
            ?.contains(track) ?: false
    }

    private fun updatePlaylists() {
        _playlists.value = playlistManager.getPlaylists()
    }

    fun playPlaylist(playlist: Playlist) {
        if (playlist.tracks.isNotEmpty()) {
            setPlaylist(playlist.tracks)
            playTrack(playlist.tracks.first())
        }
    }

    // AudioPlayer의 상태 변경을 감지하는 콜백 추가
    fun onPlaybackStateChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
        _isBuffering.value = false
        
        currentTrack.value?.let { track ->
            notificationManager?.updateNotification(track, isPlaying)
        }
    }
}