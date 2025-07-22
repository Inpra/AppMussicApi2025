package com.example.appmussicapi.ui.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.example.appmussicapi.data.model.Song

class MusicPlayer(private val context: Context) {
    private val player = ExoPlayer.Builder(context).build()
    private var currentUrl: String? = null
    private var currentSongIndex = 0
    private var playlist: List<Song> = emptyList()
    private var onSongChangeListener: ((Song) -> Unit)? = null
    private var onProgressUpdateListener: ((Long, Long) -> Unit)? = null
    private var onPlayStateChangeListener: ((Boolean) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null
    
    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        Log.d("MusicPlayer", "Ready to play")
                        startProgressUpdates()
                    }
                    Player.STATE_ENDED -> {
                        Log.d("MusicPlayer", "Playback ended")
                        next()
                    }
                    else -> stopProgressUpdates()
                }
            }
        })
    }
    
    fun play(url: String? = null) {
        url?.let {
            if (currentUrl != it) {
                currentUrl = it
                val mediaItem = MediaItem.fromUri(Uri.parse(it))
                player.setMediaItem(mediaItem)
                player.prepare()
            }
        }
        player.play()
        onPlayStateChangeListener?.invoke(true)
    }
    
    fun pause() {
        player.pause()
        stopProgressUpdates()
        onPlayStateChangeListener?.invoke(false)
    }
    
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        playlist = songs
        currentSongIndex = startIndex
        if (songs.isNotEmpty()) {
            play(songs[startIndex].url)
        }
    }
    
    fun setOnSongChangeListener(listener: (Song) -> Unit) {
        onSongChangeListener = listener
    }
    
    fun setOnProgressUpdateListener(listener: (Long, Long) -> Unit) {
        onProgressUpdateListener = listener
    }
    
    fun setOnPlayStateChangeListener(listener: (Boolean) -> Unit) {
        onPlayStateChangeListener = listener
    }
    
    fun next() {
        if (playlist.isNotEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % playlist.size
            play(playlist[currentSongIndex].url)
            onSongChangeListener?.invoke(playlist[currentSongIndex])
        }
    }
    
    fun previous() {
        if (playlist.isNotEmpty()) {
            currentSongIndex = if (currentSongIndex > 0) currentSongIndex - 1 else playlist.size - 1
            play(playlist[currentSongIndex].url)
            onSongChangeListener?.invoke(playlist[currentSongIndex])
        }
    }
    
    fun seekTo(position: Long) {
        player.seekTo(position)
    }
    
    fun getCurrentPosition(): Long = player.currentPosition
    fun getDuration(): Long = player.duration
    
    private fun startProgressUpdates() {
        progressRunnable = object : Runnable {
            override fun run() {
                if (player.isPlaying) {
                    onProgressUpdateListener?.invoke(player.currentPosition, player.duration)
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(progressRunnable!!)
    }
    
    private fun stopProgressUpdates() {
        progressRunnable?.let { handler.removeCallbacks(it) }
    }
    
    fun release() {
        stopProgressUpdates()
        player.release()
    }
}
