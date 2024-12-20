package org.example.project.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.data.remote.ContentApi
import org.example.project.domain.model.Track
import org.example.project.domain.repository.MusicRepository
import org.example.project.player.AndroidMetadataReader
import org.example.project.domain.model.ContentType

class AndroidMusicRepository(
    private val context: Context,
    private val contentApi: ContentApi
) : MusicRepository {
    private val metadataReader = AndroidMetadataReader(context)
    
    override suspend fun getAllTracks(): List<Track> {
        return contentApi.getContents()
    }
    
    override suspend fun getTrackMetadata(streamUrl: String): Track {
        val metadata = metadataReader.getMetadata(streamUrl)
        val albumArt = getAlbumArt(streamUrl)
        
        return Track(
            id = streamUrl.hashCode().toString(),
            title = metadata.title ?: "Unknown Title",
            artist = metadata.artist ?: "Unknown Artist",
            album = metadata.album,
            albumArt = albumArt,
            duration = metadata.duration,
            streamUrl = streamUrl
        )
    }

    override suspend fun getTracksByType(type: ContentType): List<Track> {
        return contentApi.getContents(type)
    }

    override suspend fun getAlbumArt(streamUrl: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                MediaMetadataRetriever().use { retriever ->
                    retriever.setDataSource(streamUrl)
                    retriever.embeddedPicture
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun searchTracks(query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            getAllTracks().filter { track ->
                track.title.contains(query, ignoreCase = true) ||
                track.artist.contains(query, ignoreCase = true) ||
                track.album?.contains(query, ignoreCase = true) == true
            }
        }
    }

    override suspend fun getTracksByAlbum(albumName: String): List<Track> {
        return withContext(Dispatchers.IO) {
            getAllTracks().filter { it.album == albumName }
        }
    }

    override suspend fun getTracksByArtist(artistName: String): List<Track> {
        return withContext(Dispatchers.IO) {
            getAllTracks().filter { it.artist == artistName }
        }
    }
}