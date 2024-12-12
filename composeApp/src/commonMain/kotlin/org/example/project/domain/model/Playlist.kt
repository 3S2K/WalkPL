package org.example.project.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val tracks: List<Track>,
    val currentIndex: Int = -1,
    val isShuffled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE
)

enum class RepeatMode {
    NONE,       // 반복 없음
    ONE,        // 한 곡 반복
    ALL         // 전체 반복
}