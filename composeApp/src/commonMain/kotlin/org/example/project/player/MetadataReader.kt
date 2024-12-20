package org.example.project.player

interface MetadataReader {
    fun getMetadata(filePath: String): TrackMetadata
}

data class TrackMetadata(
    val title: String?,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val albumArt: String?
) 