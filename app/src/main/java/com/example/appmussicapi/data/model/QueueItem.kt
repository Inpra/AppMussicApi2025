package com.example.appmussicapi.data.model

data class QueueItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val song: Song,
    val addedAt: Long = System.currentTimeMillis(),
    val isCurrentPlaying: Boolean = false
)

class PlayQueue {
    val items: MutableList<QueueItem> = mutableListOf()
    var currentIndex: Int = -1
    var originalPlaylist: List<Song> = emptyList()
    var isShuffled: Boolean = false
}
