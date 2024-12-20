package org.example.project.domain.repository

import org.example.project.domain.model.Track

interface LocalStorageRepository {
    suspend fun saveTrack(track: Track, audioData: ByteArray): Boolean
    suspend fun getLocalTrack(trackId: String): Track?
    suspend fun isTrackDownloaded(trackId: String): Boolean
    suspend fun deleteTrack(trackId: String): Boolean
    suspend fun getAllDownloadedTracks(): List<Track>
    suspend fun getTrackFile(trackId: String): ByteArray?
} 