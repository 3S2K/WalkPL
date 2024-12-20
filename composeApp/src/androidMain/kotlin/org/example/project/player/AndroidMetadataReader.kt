package org.example.project.player

import android.content.Context
import android.media.MediaMetadataRetriever

class AndroidMetadataReader(private val context: Context) : MetadataReader {
    override fun getMetadata(filePath: String): TrackMetadata {
        val retriever = MediaMetadataRetriever()
        
        return try {
            if (filePath.startsWith("android.resource://")) {
                val resourceId = context.resources.getIdentifier(
                    "test_song",
                    "raw",
                    context.packageName
                )
                val afd = context.resources.openRawResourceFd(resourceId)
                retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
            } else {
                retriever.setDataSource(filePath)
            }
            
            TrackMetadata(
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L,
                albumArt = null
            )
        } finally {
            retriever.release()
        }
    }
}