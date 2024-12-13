package org.example.project.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector
import org.example.project.domain.model.Playlist
import org.example.project.domain.model.Track
import org.example.project.domain.model.RepeatMode
import kotlinx.datetime.Clock

class PlaylistManager {
    private var playlist: List<Track> = emptyList()
    private var originalPlaylist: List<Track> = emptyList()
    private var currentIndex: Int = -1
    private var isShuffled: Boolean = false
    private var repeatMode: RepeatMode = RepeatMode.NONE
    private val playlists = mutableMapOf<String, Playlist>()
    
    companion object {
        const val LIKES_PLAYLIST_ID = "likes"
        const val MY_MUSIC_PLAYLIST_ID = "my_music"

        val DEFAULT_PLAYLISTS = listOf(
            Playlist(LIKES_PLAYLIST_ID, "좋아요 표시한 음악", emptyList()),
            Playlist(MY_MUSIC_PLAYLIST_ID, "만든 음악", emptyList()),
        )

        fun getPlaylistIcon(playlistId: String): ImageVector {
            return when (playlistId) {
                LIKES_PLAYLIST_ID -> Icons.Default.Favorite
                MY_MUSIC_PLAYLIST_ID -> Icons.Default.Build
                else -> Icons.Default.Album
            }
        }

        fun isSelectablePlaylist(playlistId: String): Boolean {
            return playlistId != MY_MUSIC_PLAYLIST_ID
        }
    }

    init {
        createDefaultPlaylists()
    }

    private fun createDefaultPlaylists() {
        DEFAULT_PLAYLISTS.forEach { playlist ->
            if (!playlists.containsKey(playlist.id)) {
                playlists[playlist.id] = playlist
            }
        }
    }

    // 플레이리스트 관리 메서드들
    fun getPlaylists(): List<Playlist> {
        return DEFAULT_PLAYLISTS.map { default ->
            playlists[default.id] ?: default
        } + playlists.values.filter { it.id !in DEFAULT_PLAYLISTS.map { it.id } }
    }

    fun createPlaylist(name: String, id: String = generateUUID()): Playlist {
        val playlist = Playlist(id = id, name = name, tracks = emptyList())
        playlists[id] = playlist
        return playlist
    }

    fun addToPlaylist(playlistId: String, track: Track) {
        playlists[playlistId]?.let { playlist ->
            if (!playlist.tracks.contains(track)) {
                playlists[playlistId] = playlist.copy(tracks = playlist.tracks + track)
            }
        }
    }

    fun removeFromPlaylist(playlistId: String, track: Track) {
        playlists[playlistId]?.let { playlist ->
            playlists[playlistId] = playlist.copy(tracks = playlist.tracks - track)
        }
    }

    fun getPlaylist(id: String): Playlist? = playlists[id] ?: DEFAULT_PLAYLISTS.find { it.id == id }

    // 재생 관련 메서드들
    fun playTrack(track: Track) {
        if (track !in playlist) {
            playlist = listOf(track)
            currentIndex = 0
        } else {
            currentIndex = playlist.indexOf(track)
        }
    }

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        originalPlaylist = tracks
        playlist = if (isShuffled) tracks.shuffled() else tracks
        currentIndex = startIndex.coerceIn(-1, tracks.lastIndex)
    }

    fun toggleShuffle() {
        isShuffled = !isShuffled
        if (isShuffled) {
            val currentTrack = getCurrentTrack()
            playlist = if (currentTrack != null) {
                (originalPlaylist - currentTrack).shuffled() + listOf(currentTrack)
            } else {
                originalPlaylist.shuffled()
            }
            currentIndex = if (currentTrack != null) playlist.lastIndex else 0
        } else {
            val currentTrack = getCurrentTrack()
            playlist = originalPlaylist
            currentIndex = if (currentTrack != null) {
                playlist.indexOf(currentTrack)
            } else {
                0
            }
        }
    }

    fun setRepeatMode(mode: RepeatMode) {
        repeatMode = mode
    }

    fun skipToNext(): Track? {
        if (playlist.isEmpty()) return null
        
        when (repeatMode) {
            RepeatMode.ONE -> return getCurrentTrack()
            RepeatMode.ALL -> {
                currentIndex = (currentIndex + 1) % playlist.size
            }
            RepeatMode.NONE -> {
                if (currentIndex < playlist.lastIndex) {
                    currentIndex++
                } else {
                    return null
                }
            }
        }
        return getCurrentTrack()
    }

    fun skipToPrevious(): Track? {
        if (playlist.isEmpty()) return null
        
        when (repeatMode) {
            RepeatMode.ONE -> return getCurrentTrack()
            RepeatMode.ALL -> {
                currentIndex = if (currentIndex <= 0) playlist.lastIndex else currentIndex - 1
            }
            RepeatMode.NONE -> {
                if (currentIndex > 0) {
                    currentIndex--
                } else {
                    return null
                }
            }
        }
        return getCurrentTrack()
    }

    fun getCurrentTrack(): Track? =
        if (currentIndex in playlist.indices) playlist[currentIndex] else null

    fun isShuffled() = isShuffled
    fun getRepeatMode() = repeatMode

    // UUID 생성을 위한 간단한 함수
    private fun generateUUID(): String {
        return Clock.System.now().toEpochMilliseconds().toString() + "_" + (0..999999).random()
    }

    // 선택 가능한 플레이리스트만 반환하는 메서드 추가
    fun getSelectablePlaylists(): List<Playlist> {
        return getPlaylists().filter { isSelectablePlaylist(it.id) }
    }

    // 현재 재생목록 설정 및 재생 시작
    fun playTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        
        // 현재 재생목록 설정
        setPlaylist(tracks)
        // 첫 번째 트랙부터 재생 시작
        playTrack(tracks.first())
    }
}