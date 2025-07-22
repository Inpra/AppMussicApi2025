package com.example.appmussicapi.data.model

data class Song(
    val id: String,
    val name: String,
    val artist: String = "Unknown Artist",
    val url: String,
    val imageUrl: String? = null,
    val duration: Long = 0L
)
