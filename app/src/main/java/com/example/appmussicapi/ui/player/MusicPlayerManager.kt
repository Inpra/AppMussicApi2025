package com.example.appmussicapi.ui.player

import android.content.Context

object MusicPlayerManager {
    private var musicPlayer: MusicPlayer? = null
    
    fun initialize(context: Context) {
        if (musicPlayer == null) {
            musicPlayer = MusicPlayer(context.applicationContext)
        }
    }
    
    fun getInstance(): MusicPlayer {
        return musicPlayer ?: throw IllegalStateException("MusicPlayerManager not initialized")
    }
    
    fun release() {
        musicPlayer?.release()
        musicPlayer = null
    }
}
