package org.example.project.domain.manager

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Track

interface DownloadManager {
    suspend fun downloadTrack(track: Track): Result<Boolean>
    suspend fun cancelDownload(trackId: String)
    fun observeDownloadProgress(trackId: String): Flow<Float>
    suspend fun deleteDownloadedTrack(trackId: String): Boolean
}