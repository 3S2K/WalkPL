package org.example.project.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val duration: Long,
    val streamUrl: String,
    val albumArtUrl: String? = null,
    val albumArt: ByteArray? = null,
    val type: ContentType = ContentType.CUSTOM,
    val metadata: ContentMetadata? = null,
    val isDownloaded: Boolean = false,
    val localPath: String? = null,
    val date: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Track

        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (duration != other.duration) return false
        if (streamUrl != other.streamUrl) return false
        if (albumArtUrl != other.albumArtUrl) return false
        if (type != other.type) return false
        if (isDownloaded != other.isDownloaded) return false
        if (localPath != other.localPath) return false
        if (!albumArt.contentEquals(other.albumArt)) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (album?.hashCode() ?: 0)
        result = 31 * result + duration.hashCode()
        result = 31 * result + streamUrl.hashCode()
        result = 31 * result + (albumArtUrl?.hashCode() ?: 0)
        result = 31 * result + (albumArt?.contentHashCode() ?: 0)
        result = 31 * result + type.hashCode()
        result = 31 * result + isDownloaded.hashCode()
        result = 31 * result + (localPath?.hashCode() ?: 0)
        result = 31 * result + (date?.hashCode() ?: 0)
        return result
    }
}

@Serializable
enum class ContentType {
    NEWS, CLIP, CUSTOM
}

@Serializable
sealed class ContentMetadata {
    @Serializable
    data class NewsMetadata(
        val sourceUrl: String,
        val sourceName: String,
        val publishedDate: String,
        val summary: String
    ) : ContentMetadata()

    @Serializable
    data class SunoMetadata(
        val prompt: String,
        val generationParams: Map<String, String>
    ) : ContentMetadata()
}