package com.example.appmussicapi.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.appmussicapi.R
import com.example.appmussicapi.data.model.Song
import com.example.appmussicapi.databinding.ActivityFullScreenPlayerBinding
import java.util.Locale

class FullScreenPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFullScreenPlayerBinding
    private lateinit var musicPlayer: MusicPlayer
    
    companion object {
        fun start(context: Context, musicPlayer: MusicPlayer) {
            val intent = Intent(context, FullScreenPlayerActivity::class.java)
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get MusicPlayer instance from MainActivity
        musicPlayer = MusicPlayerManager.getInstance()
        
        setupUI()
        setupListeners()
        updateCurrentSong()
    }
    
    private fun setupUI() {
        // Setup listeners
        musicPlayer.setOnSongChangeListener { song ->
            updateSongInfo(song)
        }
        
        musicPlayer.setOnPlayStateChangeListener { isPlaying ->
            updatePlayPauseButton(isPlaying)
        }
        
        musicPlayer.setOnProgressUpdateListener { currentPos, duration ->
            runOnUiThread {
                updateProgressBar(currentPos, duration)
            }
        }
        
        musicPlayer.setOnShuffleModeChangeListener { 
            updateShuffleButton()
        }
        
        musicPlayer.setOnRepeatModeChangeListener { 
            updateRepeatButton()
        }
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
        
        binding.previousButton.setOnClickListener {
            musicPlayer.previous()
        }
        
        binding.nextButton.setOnClickListener {
            musicPlayer.next()
        }
        
        binding.shuffleButton.setOnClickListener {
            musicPlayer.toggleShuffle()
        }
        
        binding.repeatButton.setOnClickListener {
            musicPlayer.toggleRepeatMode()
        }
        
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val duration = musicPlayer.getDuration()
                val seekPosition = (seekBar!!.progress.toFloat() / seekBar.max * duration).toLong()
                musicPlayer.seekTo(seekPosition)
            }
        })
    }
    
    private fun updateCurrentSong() {
        musicPlayer.getCurrentSong()?.let { song ->
            updateSongInfo(song)
        }
        updatePlayPauseButton(musicPlayer.isPlaying())
        updateShuffleButton()
        updateRepeatButton()
    }
    
    private fun updateSongInfo(song: Song) {
        binding.songTitle.text = song.name
        binding.artistName.text = song.artist
        
        Glide.with(this)
            .load(song.imageUrl)
            .placeholder(R.drawable.default_album_art)
            .error(R.drawable.default_album_art)
            .transform(RoundedCorners(32))
            .into(binding.albumArt)
    }
    
    private fun updatePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.ic_play)
        }
        // Tint màu trắng cho nút play/pause
        binding.playPauseButton.setColorFilter(ContextCompat.getColor(this, R.color.white))
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
    
    private fun updateProgressBar(currentPos: Long, duration: Long) {
        if (duration > 0) {
            val progress = ((currentPos.toFloat() / duration) * 100).toInt()
            binding.seekBar.progress = progress
            
            binding.currentTime.text = formatTime(currentPos)
            binding.totalTime.text = formatTime(duration)
        }
    }
    
    private fun formatTime(timeMs: Long): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}
