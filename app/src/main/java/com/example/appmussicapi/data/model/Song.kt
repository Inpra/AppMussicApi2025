package com.example.appmussicapi.data.model

data class Song(
    val id: String,
    val name: String,
    val url: String,
    val artist: String = "Unknown Artist",
    val duration: Long = 0L
)
