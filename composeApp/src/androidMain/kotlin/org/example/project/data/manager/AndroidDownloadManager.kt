package org.example.project.data.manager

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.remote.ContentApi
import org.example.project.domain.manager.DownloadManager
import org.example.project.domain.model.Track
import org.example.project.domain.repository.LocalStorageRepository

class AndroidDownloadManager(
    private val contentApi: ContentApi,
    private val localStorageRepository: LocalStorageRepository
) : DownloadManager {
    private val downloadJobs = mutableMapOf<String, Job>()
    private val downloadProgress = mutableMapOf<String, MutableStateFlow<Float>>()

    override suspend fun downloadTrack(track: Track): Result<Boolean> {
        return try {
            downloadProgress.getOrPut(track.id) { MutableStateFlow(0f) }
            
            downloadJobs[track.id] = coroutineScope {
                launch {
                    val audioData = contentApi.downloadTrack(track.streamUrl) { progress ->
                        downloadProgress[track.id]?.value = progress
                    }
                    
                    localStorageRepository.saveTrack(
                        track.copy(
                            isDownloaded = true,
                            localPath = "file://${track.id}.mp3"
                        ),
                        audioData
                    )
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelDownload(trackId: String) {
        downloadJobs[trackId]?.cancel()
        downloadJobs.remove(trackId)
        downloadProgress.remove(trackId)
    }

    override fun observeDownloadProgress(trackId: String): Flow<Float> {
        return downloadProgress.getOrPut(trackId) { MutableStateFlow(0f) }
    }

    override suspend fun deleteDownloadedTrack(trackId: String): Boolean {
        return localStorageRepository.deleteTrack(trackId)
    }
} 