package org.example.project.domain.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val filePath: String,
    val album: String? = null,
    val albumArt: String? = null,
    val duration: Long = 0
)