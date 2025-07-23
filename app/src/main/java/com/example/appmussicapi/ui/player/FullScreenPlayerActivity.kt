package com.example.appmussicapi.ui.player

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
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
        setupSleepTimer()
        updateCurrentSong()
    }
    
    private fun setupUI() {
        updatePlayPauseButton(musicPlayer.isPlaying())
        updateShuffleButton()
        updateRepeatButton()
        
        // Đảm bảo các control buttons có màu đúng
        binding.nextButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
        binding.previousButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
        binding.shuffleButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
        binding.repeatButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
        binding.moreButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
    }
    
    private fun setupListeners() {
        Log.d("FullScreenPlayer", "Setting up listeners")
        
        // Play/Pause button
        binding.playPauseButton.setOnClickListener {
            Log.d("FullScreenPlayer", "Play/Pause clicked")
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause()
            } else {
                musicPlayer.play()
            }
        }
        
        // Next button
        binding.nextButton.setOnClickListener {
            Log.d("FullScreenPlayer", "Next button clicked")
            musicPlayer.next()
        }
        
        // Previous button
        binding.previousButton.setOnClickListener {
            Log.d("FullScreenPlayer", "Previous button clicked")
            musicPlayer.previous()
        }
        
        // Shuffle button
        binding.shuffleButton.setOnClickListener {
            Log.d("FullScreenPlayer", "Shuffle button clicked")
            musicPlayer.toggleShuffle()
            updateShuffleButton()
        }
        
        // Repeat button
        binding.repeatButton.setOnClickListener {
            Log.d("FullScreenPlayer", "Repeat button clicked")
            musicPlayer.toggleRepeatMode()
            updateRepeatButton()
        }
        
        // More button
        binding.moreButton.setOnClickListener {
            Log.d("FullScreenPlayer", "More button clicked")
            showMoreOptionsMenu()
        }
        
        // Setup SeekBar
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
        // Listener khi bài hát thay đổi
        musicPlayer.setOnSongChangeListener { song ->
            runOnUiThread {
                Log.d("FullScreenPlayer", "Song changed to: ${song?.name}")
                updateCurrentSong()
            }
        }
        
        // Listener khi trạng thái play/pause thay đổi
        musicPlayer.setOnPlayStateChangeListener { isPlaying ->
            runOnUiThread {
                Log.d("FullScreenPlayer", "Play state changed: $isPlaying")
                updatePlayPauseButton(isPlaying)
            }
        }
        
        // Listener cập nhật progress
        musicPlayer.setOnProgressUpdateListener { currentPos, duration ->
            runOnUiThread {
                updateProgress(currentPos, duration)
            }
        }
        
        // Listener shuffle mode thay đổi
        musicPlayer.setOnShuffleModeChangeListener { isShuffleEnabled ->
            runOnUiThread {
                updateShuffleButton()
            }
        }
        
        // Listener repeat mode thay đổi
        musicPlayer.setOnRepeatModeChangeListener { repeatMode ->
            runOnUiThread {
                updateRepeatButton()
            }
        }
    }
    
    private fun updateCurrentSong() {
        val currentSong = musicPlayer.getCurrentSong()
        Log.d("FullScreenPlayer", "Updating current song: ${currentSong?.name}")
        
        currentSong?.let { song ->
            binding.songTitle.text = song.name
            binding.artistName.text = song.artist
            
            // Load album art với Glide
            Glide.with(this)
                .load(song.imageUrl)
                .placeholder(R.drawable.default_album_art)
                .error(R.drawable.default_album_art)
                .transform(RoundedCorners(32))
                .into(binding.albumArt)
                
            Log.d("FullScreenPlayer", "Updated UI for song: ${song.name} by ${song.artist}")
        } ?: run {
            binding.songTitle.text = "No song playing"
            binding.artistName.text = "Unknown artist"
            binding.albumArt.setImageResource(R.drawable.default_album_art)
            Log.d("FullScreenPlayer", "No current song")
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
                updatePlayPauseButton(false)
                ToastManager.showToast(this@FullScreenPlayerActivity, "Sleep timer finished")
            }
        }
        
        // Update initial state
        if (musicPlayer.isSleepTimerActive()) {
            updateSleepTimerStatus(musicPlayer.getSleepTimerRemainingTime())
        }
    }
    
    private fun showMoreOptionsMenu() {
        Log.d("FullScreenPlayer", "Showing more options menu")
        try {
            val popup = PopupMenu(this, binding.moreButton)
            
            // Inflate menu resource
            popup.menuInflater.inflate(R.menu.player_more_menu, popup.menu)
            
            // Update sleep timer menu item
            val sleepTimerItem = popup.menu.findItem(R.id.action_sleep_timer)
            if (musicPlayer.isSleepTimerActive()) {
                val remainingTime = musicPlayer.getSleepTimerRemainingTime()
                sleepTimerItem.title = "Sleep Timer (${formatTime(remainingTime)})"
            } else {
                sleepTimerItem.title = "Sleep Timer"
            }
            
            // Set click listener
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_sleep_timer -> {
                        Log.d("FullScreenPlayer", "Sleep timer menu item clicked")
                        showSleepTimerDialog()
                        true
                    }
                    else -> false
                }
            }
            
            // Show the popup menu
            popup.show()
            Log.d("FullScreenPlayer", "Popup menu shown successfully")
        } catch (e: Exception) {
            Log.e("FullScreenPlayer", "Error showing popup menu", e)
            ToastManager.showToast(this, "Error showing menu: ${e.message}")
        }
    }
    
    private fun showSleepTimerDialog() {
        try {
            val dialog = SleepTimerDialog(
                context = this,
                onTimerSet = { durationMs ->
                    musicPlayer.startSleepTimer(durationMs)
                    ToastManager.showToast(this@FullScreenPlayerActivity, "Sleep timer set for ${formatTime(durationMs)}")
                },
                onTimerCancel = {
                    musicPlayer.stopSleepTimer()
                    binding.sleepTimerStatus.visibility = View.GONE
                    ToastManager.showToast(this@FullScreenPlayerActivity, "Sleep timer cancelled")
                }
            )
            dialog.show()
        } catch (e: Exception) {
            Log.e("FullScreenPlayer", "Error showing sleep timer dialog", e)
            ToastManager.showToast(this, "Error: ${e.message}")
        }
    }
    
    private fun updateSleepTimerStatus(remainingMs: Long) {
        if (remainingMs > 0) {
            binding.sleepTimerStatus.text = "Sleep: ${formatTime(remainingMs)}"
            binding.sleepTimerStatus.visibility = View.VISIBLE
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
            binding.shuffleButton.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            binding.shuffleButton.alpha = 0.6f
        }
    }
    
    private fun updateRepeatButton() {
        when (musicPlayer.getRepeatMode()) {
            MusicPlayer.RepeatMode.OFF -> {
                binding.repeatButton.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
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
        // Đảm bảo icon có màu trắng nổi bật trên background tím
        binding.playPauseButton.setColorFilter(ContextCompat.getColor(this, R.color.white))
        binding.playPauseButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
    }
    
    override fun onResume() {
        super.onResume()
        // Update UI khi quay lại activity
        updatePlayPauseButton(musicPlayer.isPlaying())
        updateShuffleButton()
        updateRepeatButton()
        updateCurrentSong()
        
        // Update progress
        val currentPos = musicPlayer.getCurrentPosition()
        val duration = musicPlayer.getDuration()
        updateProgress(currentPos, duration)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Không clear listeners để MainActivity có thể tiếp tục hoạt động
    }
}







