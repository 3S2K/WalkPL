package org.example.project.domain.repository

import org.example.project.domain.model.Track
import org.example.project.domain.model.ContentType

interface MusicRepository {
    suspend fun getAllTracks(): List<Track>
    suspend fun getTrackMetadata(streamUrl: String): Track
    suspend fun getAlbumArt(streamUrl: String): ByteArray?
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getTracksByType(type: ContentType): List<Track>
    suspend fun getTracksByAlbum(albumName: String): List<Track>
    suspend fun getTracksByArtist(artistName: String): List<Track>
}