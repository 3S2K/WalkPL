package org.example.project.data.repository

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.example.project.domain.model.Track
import org.example.project.domain.repository.LocalStorageRepository

class AndroidLocalStorageRepository(
    private val context: Context
) : LocalStorageRepository {
    private val audioDir = File(context.filesDir, "audio")
    private val metadataDir = File(context.filesDir, "metadata")

    init {
        audioDir.mkdirs()
        metadataDir.mkdirs()
    }

    override suspend fun saveTrack(track: Track, audioData: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            // 오디오 파일 저장
            val audioFile = File(audioDir, "${track.id}.mp3")
            audioFile.writeBytes(audioData)

            // 메타데이터 저장
            val metadataFile = File(metadataDir, "${track.id}.json")
            val metadata = Json.encodeToString(track)
            metadataFile.writeText(metadata)
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getLocalTrack(trackId: String): Track? = withContext(Dispatchers.IO) {
        try {
            val metadataFile = File(metadataDir, "$trackId.json")
            if (!metadataFile.exists()) return@withContext null
            
            Json.decodeFromString<Track>(metadataFile.readText())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun isTrackDownloaded(trackId: String): Boolean = withContext(Dispatchers.IO) {
        File(audioDir, "$trackId.mp3").exists() && 
        File(metadataDir, "$trackId.json").exists()
    }

    override suspend fun deleteTrack(trackId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            File(audioDir, "$trackId.mp3").delete() &&
            File(metadataDir, "$trackId.json").delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getAllDownloadedTracks(): List<Track> = withContext(Dispatchers.IO) {
        try {
            metadataDir.listFiles()?.mapNotNull { file ->
                Json.decodeFromString<Track>(file.readText())
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTrackFile(trackId: String): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val file = File(audioDir, "$trackId.mp3")
            if (file.exists()) file.readBytes() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
} 