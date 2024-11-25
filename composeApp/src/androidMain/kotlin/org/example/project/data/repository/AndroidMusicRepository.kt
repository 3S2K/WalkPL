package org.example.project.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.domain.model.Track
import org.example.project.domain.repository.MusicRepository
import org.example.project.player.AndroidMetadataReader

class AndroidMusicRepository(
    private val context: Context
) : MusicRepository {
    private val metadataReader = AndroidMetadataReader(context)
    
    override suspend fun getAllTracks(): List<Track> {
        return withContext(Dispatchers.IO) {
            val tracks = mutableListOf<Track>()
            
            val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
            val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
            
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
            )
            
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val filePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (cursor.moveToNext()) {
                    val track = Track(
                        id = cursor.getString(idColumn),
                        title = cursor.getString(titleColumn) ?: "Unknown Title",
                        artist = cursor.getString(artistColumn) ?: "Unknown Artist",
                        album = cursor.getString(albumColumn) ?: "Unknown Album",
                        duration = cursor.getLong(durationColumn),
                        filePath = cursor.getString(filePathColumn),
                        albumArt = null,
                    )
                    tracks.add(track)
                }
            }
            
            tracks
        }
    }

    override suspend fun getTrackMetadata(filePath: String): Track {
        return withContext(Dispatchers.IO) {
            val metadata = metadataReader.getMetadata(filePath)
            Track(
                id = filePath.hashCode().toString(),
                title = metadata.title ?: "Unknown Title",
                artist = metadata.artist ?: "Unknown Artist",
                album = metadata.album,
                albumArt = metadata.albumArt,
                duration = metadata.duration,
                filePath = filePath
            )
        }
    }

    override suspend fun getAlbumArt(filePath: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(filePath)
                retriever.embeddedPicture
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