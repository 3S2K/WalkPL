package org.example.project.player

import org.example.project.domain.model.Track

class PlaylistManager {
    private var currentPlaylist: List<Track> = emptyList()
    private var currentIndex: Int = -1

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        currentPlaylist = tracks
        currentIndex = startIndex.coerceIn(-1, tracks.lastIndex)
    }

    fun getCurrentTrack(): Track? {
        return if (currentIndex in currentPlaylist.indices) {
            currentPlaylist[currentIndex]
        } else {
            null
        }
    }

    fun skipToNext(): Track? {
        if (currentPlaylist.isEmpty()) return null
        currentIndex = (currentIndex + 1) % currentPlaylist.size
        return getCurrentTrack()
    }

    fun skipToPrevious(): Track? {
        if (currentPlaylist.isEmpty()) return null
        currentIndex = if (currentIndex <= 0) currentPlaylist.lastIndex else currentIndex - 1
        return getCurrentTrack()
    }

    fun getNextTrack(): Track? {
        if (currentPlaylist.isEmpty()) return null
        val nextIndex = (currentIndex + 1) % currentPlaylist.size
        return currentPlaylist[nextIndex]
    }

    fun getPreviousTrack(): Track? {
        if (currentPlaylist.isEmpty()) return null
        val previousIndex = if (currentIndex <= 0) currentPlaylist.lastIndex else currentIndex - 1
        return currentPlaylist[previousIndex]
    }

    fun getCurrentIndex(): Int = currentIndex

    fun getPlaylistSize(): Int = currentPlaylist.size

    fun clearPlaylist() {
        currentPlaylist = emptyList()
        currentIndex = -1
    }
}