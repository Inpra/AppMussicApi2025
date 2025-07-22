package com.example.appmussicapi.ui.main

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appmussicapi.R
import com.example.appmussicapi.data.model.Song
import com.example.appmussicapi.databinding.ActivityMainBinding
import com.example.appmussicapi.ui.adapter.SongAdapter
import com.example.appmussicapi.ui.player.MusicPlayer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: SongAdapter
    private lateinit var player: MusicPlayer
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ẩn ActionBar để không bị che
        supportActionBar?.hide()
        
        try {
            Log.d("MainActivity", "Starting onCreate")
            
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            viewModel = ViewModelProvider(this)[MainViewModel::class.java]
            player = MusicPlayer(this)
            
            setupRecyclerView()
            setupObservers()
            setupClickListeners()
            setupSearchView()
            setupSeekBar()
            
            viewModel.loadSongs()
            
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        
        // Setup player listeners
        player.setOnSongChangeListener { song ->
            updateNowPlaying(song)
        }
        
        player.setOnProgressUpdateListener { currentPos, duration ->
            updateProgress(currentPos, duration)
        }

        player.setOnPlayStateChangeListener { isPlaying ->
            if (isPlaying) {
                showPauseButton()
            } else {
                showPlayButton()
            }
        }
    }
    
    private fun setupRecyclerView() {
        adapter = SongAdapter { song ->
            val songIndex = viewModel.songs.value?.indexOf(song) ?: 0
            player.setPlaylist(viewModel.songs.value ?: emptyList(), songIndex)
            updateNowPlaying(song)
            Toast.makeText(this, "Playing: ${song.name}", Toast.LENGTH_SHORT).show()
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.songs.observe(this) { songs ->
            adapter.updateSongs(songs)
        }
    }
    
    private fun setupClickListeners() {
        binding.playBtn.setOnClickListener {
            player.play()
            showPauseButton()
        }
        
        binding.pauseBtn.setOnClickListener {
            player.pause()
            showPlayButton()
        }
        
        binding.prevBtn.setOnClickListener {
            player.previous()
        }
        
        binding.nextBtn.setOnClickListener {
            player.next()
        }
    }

    private fun showPlayButton() {
        binding.playBtn.visibility = View.VISIBLE
        binding.pauseBtn.visibility = View.GONE
    }

    private fun showPauseButton() {
        binding.playBtn.visibility = View.GONE
        binding.pauseBtn.visibility = View.VISIBLE
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }
    
    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = player.getDuration()
                    val newPosition = (progress * duration / 100).toLong()
                    binding.currentTime.text = formatTime(newPosition)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
                val duration = player.getDuration()
                val newPosition = (seekBar!!.progress * duration / 100).toLong()
                player.seekTo(newPosition)
            }
        })
    }
    
    private fun updateNowPlaying(song: Song) {
        binding.songTitle.text = "${getString(R.string.now_playing)} ${song.name}"
        showPauseButton()
    }
    
    private fun updateProgress(currentPos: Long, duration: Long) {
        if (!isUserSeeking && duration > 0) {
            val progress = ((currentPos * 100) / duration).toInt()
            binding.seekBar.progress = progress
            
            binding.currentTime.text = formatTime(currentPos)
            binding.totalTime.text = formatTime(duration)
        }
    }
    
    private fun formatTime(timeMs: Long): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}








