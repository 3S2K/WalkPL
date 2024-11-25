package org.example.project.domain.repository

import org.example.project.domain.model.Track

interface MusicRepository {
    suspend fun getAllTracks(): List<Track>
    suspend fun getTrackMetadata(filePath: String): Track
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getAlbumArt(filePath: String): ByteArray?
    suspend fun getTracksByAlbum(albumName: String): List<Track>
    suspend fun getTracksByArtist(artistName: String): List<Track>
}