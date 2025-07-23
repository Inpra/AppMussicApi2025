package com.example.appmussicapi.ui.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.C
import com.example.appmussicapi.data.model.Song

class MusicPlayer(private val context: Context) {
    private val player = ExoPlayer.Builder(context)
        .setWakeMode(C.WAKE_MODE_LOCAL)
        .setHandleAudioBecomingNoisy(true)
        .build()
    private var currentUrl: String? = null
    private var currentSongIndex = 0
    private var playlist: List<Song> = emptyList()
    private var shuffledPlaylist: List<Song> = emptyList()
    private var isShuffleEnabled = false
    private var repeatMode = RepeatMode.OFF

    // Getter methods
    fun isPlaying(): Boolean = player.isPlaying
    fun getIsShuffleEnabled(): Boolean = isShuffleEnabled
    fun getPlayer(): ExoPlayer = player
    fun getRepeatMode(): RepeatMode = repeatMode

    // Add missing variables
    private val handler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null

    enum class RepeatMode {
        OFF, ALL, ONE
    }
    
    // Listeners
    private var onSongChangeListener: ((Song) -> Unit)? = null
    private var onProgressUpdateListener: ((Long, Long) -> Unit)? = null
    private var onPlayStateChangeListener: ((Boolean) -> Unit)? = null
    private var onShuffleModeChangeListener: ((Boolean) -> Unit)? = null
    private var onRepeatModeChangeListener: ((RepeatMode) -> Unit)? = null

    // Add sleep timer
    private val sleepTimer = SleepTimer()
    
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
                        stopProgressUpdates()
                        handlePlaybackEnded()
                    }
                }
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    startProgressUpdates()
                } else {
                    stopProgressUpdates()
                }
                onPlayStateChangeListener?.invoke(isPlaying)
            }
        })

        // Initialize sleep timer
        sleepTimer.setMusicPlayer(this)
    }
    
    fun play(url: String? = null) {
        // Reset volume nếu đang bằng 0 (sau sleep timer)
        if (player.volume == 0f) {
            player.volume = 1.0f
            Log.d("MusicPlayer", "Volume reset to 1.0 after sleep timer")
        }
        
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
        player.pause()                         // Fix: exoPlayer -> player
        stopProgressUpdates()
        onPlayStateChangeListener?.invoke(false)
    }
    
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0, autoPlay: Boolean = true) {
        this.playlist = songs
        this.currentSongIndex = startIndex  // Fix: currentIndex -> currentSongIndex
        
        if (songs.isNotEmpty() && startIndex < songs.size) {
            val song = songs[startIndex]
            val mediaItem = MediaItem.fromUri(song.url)
            
            player.setMediaItem(mediaItem)  // Fix: exoPlayer -> player
            player.prepare()                // Fix: exoPlayer -> player
            
            if (autoPlay) {
                player.play()               // Fix: exoPlayer -> player
            }
            
            onSongChangeListener?.invoke(song)
            Log.d("MusicPlayer", "Playlist set with ${songs.size} songs, current: ${song.name}")
        }
    }
    
    fun setOnSongChangeListener(listener: ((Song) -> Unit)?) {
        onSongChangeListener = listener
    }
    
    fun setOnProgressUpdateListener(listener: ((Long, Long) -> Unit)?) {
        onProgressUpdateListener = listener
    }
    
    fun setOnPlayStateChangeListener(listener: ((Boolean) -> Unit)?) {
        onPlayStateChangeListener = listener
    }
    
    fun next() {
        val currentPlaylist = if (isShuffleEnabled) shuffledPlaylist else playlist
        if (currentPlaylist.isNotEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % currentPlaylist.size
            Log.d("MusicPlayer", "Next song - Index: $currentSongIndex, Shuffle: $isShuffleEnabled")
            play(currentPlaylist[currentSongIndex].url)
            onSongChangeListener?.invoke(currentPlaylist[currentSongIndex])
        }
    }
    
    fun previous() {
        val currentPlaylist = if (isShuffleEnabled) shuffledPlaylist else playlist
        if (currentPlaylist.isNotEmpty()) {
            currentSongIndex = if (currentSongIndex > 0) currentSongIndex - 1 else currentPlaylist.size - 1
            Log.d("MusicPlayer", "Previous song - Index: $currentSongIndex, Shuffle: $isShuffleEnabled")
            play(currentPlaylist[currentSongIndex].url)
            onSongChangeListener?.invoke(currentPlaylist[currentSongIndex])
        }
    }
    
    fun seekTo(positionMs: Long) {
        if (player.isCommandAvailable(Player.COMMAND_SEEK_TO_DEFAULT_POSITION)) {
            // Reset volume nếu đang bằng 0 (sau sleep timer)
            if (player.volume == 0f) {
                player.volume = 1.0f
                Log.d("MusicPlayer", "Volume reset to 1.0 during seek")
            }
            
            player.seekTo(positionMs)
            Log.d("MusicPlayer", "Seeking to: ${positionMs}ms")
        }
    }
    
    fun getCurrentPosition(): Long = player.currentPosition

    fun getDuration(): Long = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressRunnable = object : Runnable {
            override fun run() {
                if (player.isPlaying) {
                    val currentPos = getCurrentPosition()
                    val duration = getDuration()
                    onProgressUpdateListener?.invoke(currentPos, duration)
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }
        handler.post(progressRunnable!!)
    }
    
    private fun stopProgressUpdates() {
        progressRunnable?.let { handler.removeCallbacks(it) }
        progressRunnable = null
    }
    
    fun release() {
        stopProgressUpdates()
        player.release()
    }
    
    fun toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled
        Log.d("MusicPlayer", "Shuffle toggled: $isShuffleEnabled")
        
        if (isShuffleEnabled) {
            // Create shuffled playlist
            shuffledPlaylist = playlist.shuffled()
            Log.d("MusicPlayer", "Created shuffled playlist with ${shuffledPlaylist.size} songs")
            
            // Find current song in shuffled playlist
            val currentSong = getCurrentSong()
            if (currentSong != null) {
                currentSongIndex = shuffledPlaylist.indexOf(currentSong)
                if (currentSongIndex == -1) {
                    currentSongIndex = 0
                }
            } else {
                currentSongIndex = 0
            }
            Log.d("MusicPlayer", "Current index in shuffled playlist: $currentSongIndex")
        } else {
            // Find current song in original playlist
            val currentSong = getCurrentSong()
            if (currentSong != null) {
                currentSongIndex = playlist.indexOf(currentSong)
                if (currentSongIndex == -1) {
                    currentSongIndex = 0
                }
            } else {
                currentSongIndex = 0
            }
            Log.d("MusicPlayer", "Current index in original playlist: $currentSongIndex")
        }
        
        onShuffleModeChangeListener?.invoke(isShuffleEnabled)
    }
    
    fun toggleRepeatMode() {
        repeatMode = when (repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        
        // Set ExoPlayer repeat mode
        when (repeatMode) {
            RepeatMode.OFF -> player.repeatMode = Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> player.repeatMode = Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> player.repeatMode = Player.REPEAT_MODE_ONE
        }
        
        onRepeatModeChangeListener?.invoke(repeatMode)
    }
    
    fun setOnShuffleModeChangeListener(listener: ((Boolean) -> Unit)?) {
        onShuffleModeChangeListener = listener
    }
    
    fun setOnRepeatModeChangeListener(listener: ((RepeatMode) -> Unit)?) {
        onRepeatModeChangeListener = listener
    }

    fun isRepeatOneEnabled(): Boolean = repeatMode == RepeatMode.ONE

    fun getCurrentSong(): Song? {
        val currentPlaylist = if (isShuffleEnabled) shuffledPlaylist else playlist
        return if (currentPlaylist.isNotEmpty() && currentSongIndex >= 0 && currentSongIndex < currentPlaylist.size) {
            currentPlaylist[currentSongIndex]
        } else null
    }

    private fun handlePlaybackEnded() {
        val currentPlaylist = if (isShuffleEnabled) shuffledPlaylist else playlist
        if (currentPlaylist.isNotEmpty()) {
            when (repeatMode) {
                RepeatMode.ONE -> {
                    // Replay current song from beginning
                    Log.d("MusicPlayer", "Repeating current song")
                    seekTo(0)
                    play()
                    onSongChangeListener?.invoke(currentPlaylist[currentSongIndex])
                }
                RepeatMode.ALL -> {
                    // Go to next song, loop back to start if at end
                    currentSongIndex = (currentSongIndex + 1) % currentPlaylist.size
                    Log.d("MusicPlayer", "Repeat All - Next song: ${currentSongIndex}")
                    play(currentPlaylist[currentSongIndex].url)
                    onSongChangeListener?.invoke(currentPlaylist[currentSongIndex])
                }
                RepeatMode.OFF -> {
                    if (currentSongIndex < currentPlaylist.size - 1) {
                        // Play next song
                        currentSongIndex++
                        Log.d("MusicPlayer", "Auto next song: ${currentSongIndex}")
                        play(currentPlaylist[currentSongIndex].url)
                        onSongChangeListener?.invoke(currentPlaylist[currentSongIndex])
                    } else {
                        // End of playlist, stop playing
                        Log.d("MusicPlayer", "End of playlist")
                        pause()
                    }
                }
            }
        }
    }

    // Sleep timer methods
    fun getSleepTimer(): SleepTimer = sleepTimer
    
    fun startSleepTimer(durationMs: Long) {
        sleepTimer.startTimer(durationMs)
    }
    
    fun stopSleepTimer() {
        sleepTimer.stopTimer()
    }
    
    fun isSleepTimerActive(): Boolean = sleepTimer.isActive()
    
    fun getSleepTimerRemainingTime(): Long = sleepTimer.getRemainingTime()
}
