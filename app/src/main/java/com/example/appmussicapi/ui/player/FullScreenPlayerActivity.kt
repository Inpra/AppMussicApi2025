package com.example.appmussicapi.ui.player

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.appmussicapi.R
import com.example.appmussicapi.databinding.ActivityFullScreenPlayerBinding
import com.example.appmussicapi.utils.ToastManager
import com.example.appmussicapi.ui.dialog.SleepTimerDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class FullScreenPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFullScreenPlayerBinding
    private lateinit var musicPlayer: MusicPlayer
    
    companion object {
        fun start(context: android.content.Context, musicPlayer: MusicPlayer) {
            val intent = android.content.Intent(context, FullScreenPlayerActivity::class.java)
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        musicPlayer = MusicPlayerManager.getInstance()
        
        setupUI()
        setupListeners()
        setupMusicPlayerListeners()
        updateCurrentSong()
        setupSleepTimer()
    }
    
    private fun setupUI() {
        updatePlayPauseButton(musicPlayer.isPlaying())
        updateShuffleButton()
        updateRepeatButton()
    }
    
    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
        
        binding.playPauseButton.setOnClickListener {
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause()
            } else {
                musicPlayer.play()
            }
        }
        
        binding.nextButton.setOnClickListener {
            musicPlayer.next()
        }
        
        binding.previousButton.setOnClickListener {
            musicPlayer.previous()
        }
        
        binding.shuffleButton.setOnClickListener {
            musicPlayer.toggleShuffle()
            updateShuffleButton()
        }
        
        binding.repeatButton.setOnClickListener {
            musicPlayer.toggleRepeatMode()
            updateRepeatButton()
        }
        
        binding.moreButton.setOnClickListener {
            showMoreOptionsMenu()
        }
        
        // Setup SeekBar - Fix để tránh auto reset
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var userSeeking = false
            
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && userSeeking) {
                    val duration = musicPlayer.getDuration()
                    val newPosition = (progress.toFloat() / 100f * duration).toLong()
                    musicPlayer.seekTo(newPosition)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userSeeking = true
            }
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userSeeking = false
            }
        })
    }
    
    private fun setupMusicPlayerListeners() {
        // Listen for song changes
        musicPlayer.setOnSongChangeListener { song ->
            runOnUiThread {
                updateCurrentSong()
            }
        }
        
        // Listen for play state changes
        musicPlayer.setOnPlayStateChangeListener { isPlaying ->
            runOnUiThread {
                updatePlayPauseButton(isPlaying)
            }
        }
        
        // Listen for progress updates
        musicPlayer.setOnProgressUpdateListener { currentPosition, duration ->
            runOnUiThread {
                updateProgress(currentPosition, duration)
            }
        }
        
        // Listen for shuffle mode changes
        musicPlayer.setOnShuffleModeChangeListener { isShuffleEnabled ->
            runOnUiThread {
                updateShuffleButton()
            }
        }
        
        // Listen for repeat mode changes
        musicPlayer.setOnRepeatModeChangeListener { repeatMode ->
            runOnUiThread {
                updateRepeatButton()
            }
        }
    }
    
    private fun updateCurrentSong() {
        val currentSong = musicPlayer.getCurrentSong()
        currentSong?.let { song ->
            binding.songTitle.text = song.name
            binding.artistName.text = song.artist
            
            // Load album art
            Glide.with(this)
                .load(song.imageUrl)
                .placeholder(R.drawable.default_album_art)
                .error(R.drawable.default_album_art)
                .transform(RoundedCorners(32))
                .into(binding.albumArt)
        } ?: run {
            binding.songTitle.text = "No song playing"
            binding.artistName.text = "Unknown artist"
            binding.albumArt.setImageResource(R.drawable.default_album_art)
        }
    }
    
    private fun updateProgress(currentPosition: Long, duration: Long) {
        if (duration > 0) {
            // Chỉ update progress nếu user không đang kéo seekbar
            if (!binding.seekBar.isPressed) {
                val progress = ((currentPosition.toFloat() / duration.toFloat()) * 100).toInt()
                binding.seekBar.progress = progress
            }
            
            binding.currentTime.text = formatTime(currentPosition)
            binding.totalTime.text = formatTime(duration)
        }
    }
    
    private fun formatTime(timeMs: Long): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    private fun setupSleepTimer() {
        // Setup sleep timer listeners
        musicPlayer.getSleepTimer().setOnTimerUpdateListener { remainingMs ->
            runOnUiThread {
                updateSleepTimerStatus(remainingMs)
            }
        }
        
        musicPlayer.getSleepTimer().setOnTimerFinishedListener {
            runOnUiThread {
                binding.sleepTimerStatus.visibility = View.GONE
                updatePlayPauseButton(false) // Update UI to show paused state
                ToastManager.showToast(this@FullScreenPlayerActivity, "Sleep timer finished")
            }
        }
        
        // Update initial state
        if (musicPlayer.isSleepTimerActive()) {
            updateSleepTimerStatus(musicPlayer.getSleepTimerRemainingTime())
        }
    }
    
    private fun showMoreOptionsMenu() {
        val popup = PopupMenu(this, binding.moreButton)
        popup.menuInflater.inflate(R.menu.player_more_menu, popup.menu)
        
        // Update sleep timer menu item
        val sleepTimerItem = popup.menu.findItem(R.id.action_sleep_timer)
        if (musicPlayer.isSleepTimerActive()) {
            sleepTimerItem.title = "Sleep Timer (${musicPlayer.getSleepTimer().formatTime(musicPlayer.getSleepTimerRemainingTime())})"
        } else {
            sleepTimerItem.title = "Sleep Timer"
        }
        
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sleep_timer -> {
                    showSleepTimerDialog()
                    true
                }
                else -> false
            }
        }
        
        popup.show()
    }
    
    private fun showSleepTimerDialog() {
        val dialog = SleepTimerDialog(
            context = this,
            onTimerSet = { durationMs ->
                musicPlayer.startSleepTimer(durationMs)
                ToastManager.showToast(this@FullScreenPlayerActivity, "Sleep timer set for ${musicPlayer.getSleepTimer().formatTime(durationMs)}")
            },
            onTimerCancel = {
                musicPlayer.stopSleepTimer()
                binding.sleepTimerStatus.visibility = View.GONE
                ToastManager.showToast(this@FullScreenPlayerActivity, "Sleep timer cancelled")
            }
        )
        dialog.show()
    }
    
    private fun updateSleepTimerStatus(remainingMs: Long) {
        if (remainingMs > 0) {
            binding.sleepTimerStatus.visibility = View.VISIBLE
            binding.sleepTimerStatus.text = "Sleep: ${musicPlayer.getSleepTimer().formatTime(remainingMs)}"
        } else {
            binding.sleepTimerStatus.visibility = View.GONE
        }
    }
    
    private fun updateShuffleButton() {
        val isShuffleOn = musicPlayer.getIsShuffleEnabled()
        if (isShuffleOn) {
            binding.shuffleButton.setColorFilter(ContextCompat.getColor(this, R.color.button_active_cyan))
            binding.shuffleButton.alpha = 1.0f
        } else {
            binding.shuffleButton.setColorFilter(ContextCompat.getColor(this, R.color.button_inactive))
            binding.shuffleButton.alpha = 0.6f
        }
    }
    
    private fun updateRepeatButton() {
        when (musicPlayer.getRepeatMode()) {
            MusicPlayer.RepeatMode.OFF -> {
                binding.repeatButton.setColorFilter(ContextCompat.getColor(this, R.color.button_inactive))
                binding.repeatButton.alpha = 0.6f
                binding.repeatButton.setImageResource(R.drawable.ic_repeat)
            }
            MusicPlayer.RepeatMode.ALL -> {
                binding.repeatButton.setColorFilter(ContextCompat.getColor(this, R.color.button_active_cyan))
                binding.repeatButton.alpha = 1.0f
                binding.repeatButton.setImageResource(R.drawable.ic_repeat)
            }
            MusicPlayer.RepeatMode.ONE -> {
                binding.repeatButton.setColorFilter(ContextCompat.getColor(this, R.color.button_active_orange))
                binding.repeatButton.alpha = 1.0f
                binding.repeatButton.setImageResource(R.drawable.ic_repeat_one)
            }
        }
    }
    
    private fun updatePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.ic_play)
        }
        binding.playPauseButton.setColorFilter(ContextCompat.getColor(this, R.color.white))
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear listeners to prevent memory leaks
        musicPlayer.setOnSongChangeListener(null)
        musicPlayer.setOnPlayStateChangeListener(null)
        musicPlayer.setOnProgressUpdateListener(null)
    }
}







