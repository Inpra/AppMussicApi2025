package com.example.appmussicapi.ui.player

object MusicPlayerManager {
    private var musicPlayerInstance: MusicPlayer? = null
    
    fun initialize(musicPlayer: MusicPlayer) {
        musicPlayerInstance = musicPlayer
    }
    
    fun getInstance(): MusicPlayer {
        return musicPlayerInstance ?: throw IllegalStateException("MusicPlayer not initialized")
    }
}