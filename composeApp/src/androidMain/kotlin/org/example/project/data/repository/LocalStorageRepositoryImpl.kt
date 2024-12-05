package org.example.project.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.domain.model.Track
import org.example.project.domain.repository.LocalStorageRepository
import java.io.File

class LocalStorageRepositoryImpl(
    private val context: Context
) : LocalStorageRepository {
    private val musicDir: File
        get() = File(context.filesDir, "music").apply { mkdirs() }

    override suspend fun saveTrack(track: Track, audioData: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            File(musicDir, "${track.id}.mp3").writeBytes(audioData)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getLocalTrack(trackId: String): Track? = withContext(Dispatchers.IO) {
        try {
            val file = File(musicDir, "$trackId.mp3")
            if (!file.exists()) return@withContext null

            // MediaMetadataRetriever를 사용하여 메타데이터 읽기
            val retriever = android.media.MediaMetadataRetriever().apply {
                setDataSource(file.absolutePath)
            }

            Track(
                id = trackId,
                title = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown",
                artist = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown",
                album = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM),
                albumArt = retriever.embeddedPicture,
                duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L,
                streamUrl = file.absolutePath
            ).also {
                retriever.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun isTrackDownloaded(trackId: String): Boolean = withContext(Dispatchers.IO) {
        File(musicDir, "$trackId.mp3").exists()
    }

    override suspend fun deleteTrack(trackId: String): Boolean = withContext(Dispatchers.IO) {
        File(musicDir, "$trackId.mp3").delete()
    }

    override suspend fun getAllDownloadedTracks(): List<Track> = withContext(Dispatchers.IO) {
        try {
            musicDir.listFiles()
                ?.filter { it.extension == "mp3" }
                ?.mapNotNull { file ->
                    getLocalTrack(file.nameWithoutExtension)
                } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTrackFile(trackId: String): ByteArray? = withContext(Dispatchers.IO) {
        val file = File(musicDir, "$trackId.mp3")
        if (file.exists()) file.readBytes() else null
    }
} 