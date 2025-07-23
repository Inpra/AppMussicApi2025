package com.example.appmussicapi.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appmussicapi.R
import com.example.appmussicapi.databinding.ActivityMainBinding
import com.example.appmussicapi.data.model.Song
import com.example.appmussicapi.ui.adapter.SongAdapter
import com.example.appmussicapi.ui.player.MusicPlayer
import com.example.appmussicapi.repository.OfflineRepository
import com.example.appmussicapi.data.download.DownloadManager
import com.example.appmussicapi.ui.player.FullScreenPlayerActivity
import com.example.appmussicapi.ui.player.MusicPlayerManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.util.Locale
import com.example.appmussicapi.utils.ToastManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SongAdapter
    private lateinit var player: MusicPlayer
    private lateinit var themeManager: ThemeManager
    private lateinit var offlineRepository: OfflineRepository
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize components
        themeManager = ThemeManager(this)
        player = MusicPlayer(this)
        offlineRepository = OfflineRepository(this)
        
        // Initialize MusicPlayerManager
        MusicPlayerManager.initialize(player)
        
        setupRecyclerView()
        setupPlayerControls()
        setupSearchView()
        setupObservers()
        setupDownloadListener()
        
        viewModel.loadSongs()
    }
    
    private fun setupRecyclerView() {
        adapter = SongAdapter(
            onSongClick = { song ->
                val songIndex = viewModel.songs.value?.indexOf(song) ?: 0
                player.setPlaylist(viewModel.songs.value ?: emptyList(), songIndex, true) // true = auto play khi user click
                updateNowPlaying(song)
                ToastManager.showToast(this, "Playing: ${song.name}")
            },
            onDownloadClick = { song ->
                handleDownloadClick(song)
            }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupPlayerControls() {
        val playerControls = binding.playerControls
        
        // Setup listeners để tự động update UI
        player.setOnPlayStateChangeListener { isPlaying ->
            updatePlayPauseButton(isPlaying)
        }
        
        player.setOnShuffleModeChangeListener { isShuffleEnabled ->
            updateShuffleButton()
        }
        
        player.setOnRepeatModeChangeListener { repeatMode ->
            updateRepeatButton()
        }
        
        player.setOnSongChangeListener { song ->
            updateNowPlaying(song)
        }
        
        // Setup progress updates
        player.setOnProgressUpdateListener { currentPos, duration ->
            runOnUiThread {
                updateProgressBar(currentPos, duration)
            }
        }
        
        // Button click listeners
        playerControls.playBtn.setOnClickListener {
            player.play()
        }
        
        playerControls.pauseBtn.setOnClickListener {
            player.pause()
        }
        
        playerControls.prevBtn.setOnClickListener {
            player.previous()
        }
        
        playerControls.nextBtn.setOnClickListener {
            player.next()
        }
        
        playerControls.shuffleBtn.setOnClickListener {
            player.toggleShuffle()
        }
        
        playerControls.repeatBtn.setOnClickListener {
            player.toggleRepeatMode()
        }
        
        // SeekBar setup
        playerControls.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            private var userSeeking = false
            
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change
            }
            
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                userSeeking = true
            }
            
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                userSeeking = false
                val duration = player.getDuration()
                val seekPosition = (seekBar!!.progress.toFloat() / seekBar.max * duration).toLong()
                player.seekTo(seekPosition)
            }
        })
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("MainActivity", "Search submitted: $query")
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("MainActivity", "Search text changed: $newText")
                adapter.filter(newText ?: "")
                return true
            }
        })
        
        binding.searchView.setOnCloseListener {
            adapter.filter("")
            false
        }
    }
    
    private fun setupObservers() {
        viewModel.songs.observe(this) { songs ->
            Log.d("MainActivity", "Songs loaded: ${songs.size}")
            adapter.updateSongs(songs)
            
            if (songs.isNotEmpty()) {
                // Chỉ set playlist, KHÔNG auto play
                player.setPlaylist(songs, 0, false) // false = không auto play
            }
        }
    }
    
    private fun updateNowPlaying(song: Song?) {
        song?.let {
            binding.playerControls.songTitle.text = it.name
            binding.playerControls.artistName.text = it.artist
            Log.d("MainActivity", "Now playing: ${it.name} by ${it.artist}")
            
            // Add click listener to open full screen player
            binding.playerControls.songTitle.setOnClickListener {
                FullScreenPlayerActivity.start(this, player)
            }
            binding.playerControls.artistName.setOnClickListener {
                FullScreenPlayerActivity.start(this, player)
            }
        }
    }
    
    private fun updatePlayPauseButton(isPlaying: Boolean) {
        val playerControls = binding.playerControls
        if (isPlaying) {
            playerControls.playBtn.visibility = android.view.View.GONE
            playerControls.pauseBtn.visibility = android.view.View.VISIBLE
        } else {
            playerControls.playBtn.visibility = android.view.View.VISIBLE
            playerControls.pauseBtn.visibility = android.view.View.GONE
        }
    }
    
    private fun updateShuffleButton() {
        val isShuffleOn = player.getIsShuffleEnabled()
        val playerControls = binding.playerControls
        
        Log.d("MainActivity", "Updating shuffle button - isOn: $isShuffleOn")
        
        if (isShuffleOn) {
            playerControls.shuffleBtn.alpha = 1.0f
            playerControls.shuffleBtn.setColorFilter(ContextCompat.getColor(this, R.color.button_active_cyan))
            Toast.makeText(this, "Shuffle: On", Toast.LENGTH_SHORT).show()
        } else {
            playerControls.shuffleBtn.alpha = 0.5f
            playerControls.shuffleBtn.setColorFilter(ContextCompat.getColor(this, R.color.button_inactive))
            Toast.makeText(this, "Shuffle: Off", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateRepeatButton() {
        val playerControls = binding.playerControls
        
        when (player.getRepeatMode()) {
            MusicPlayer.RepeatMode.OFF -> {
                playerControls.repeatBtn.alpha = 0.5f
                playerControls.repeatBtn.setImageResource(R.drawable.ic_repeat)
                playerControls.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.button_inactive))
                Toast.makeText(this, "Repeat: Off", Toast.LENGTH_SHORT).show()
            }
            MusicPlayer.RepeatMode.ALL -> {
                playerControls.repeatBtn.alpha = 1.0f
                playerControls.repeatBtn.setImageResource(R.drawable.ic_repeat)
                playerControls.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.button_active_cyan))
                Toast.makeText(this, "Repeat: All", Toast.LENGTH_SHORT).show()
            }
            MusicPlayer.RepeatMode.ONE -> {
                playerControls.repeatBtn.alpha = 1.0f
                playerControls.repeatBtn.setImageResource(R.drawable.ic_repeat_one)
                playerControls.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.button_active_orange))
                Toast.makeText(this, "Repeat: One", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showThemeDialog() {
        val themes = arrayOf("Light", "Dark", "System Default")
        val currentTheme = themeManager.getCurrentTheme()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Choose Theme")
            .setSingleChoiceItems(themes, currentTheme) { dialog, which ->
                themeManager.setTheme(which)
                dialog.dismiss()
                recreate()
            }
            .show()
    }
    
    private fun updateProgressBar(currentPos: Long, duration: Long) {
        val playerControls = binding.playerControls
        
        if (duration > 0) {
            val progress = ((currentPos.toFloat() / duration) * 100).toInt()
            playerControls.seekBar.progress = progress
            
            // Update time labels
            playerControls.currentTime.text = formatTime(currentPos)
            playerControls.totalTime.text = formatTime(duration)
        }
    }

    private fun formatTime(timeMs: Long): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
    
    // private fun setupThemeButton() {
    //     // Add theme option to options menu instead
    //     // Or add a simple button in the main layout
    // } // REMOVE THIS FUNCTION
    
    // override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    //     menuInflater.inflate(R.menu.main_menu, menu)
    //     return true
    // }

    // override fun onOptionsItemSelected(item: MenuItem): Boolean {
    //     return when (item.itemId) {
    //         R.id.action_theme -> {
    //             showThemeDialog()
    //             true
    //         }
    //         else -> super.onOptionsItemSelected(item)
    //     }
    // }
    
    private fun setupDownloadListener() {
        offlineRepository.setDownloadProgressListener { progress ->
            runOnUiThread {
                when (progress.status) {
                    DownloadManager.DownloadStatus.DOWNLOADING -> {
                        // Chỉ show toast mỗi 25% progress để tránh spam
                        if (progress.progress % 25 == 0) {
                            ToastManager.showToast(this, "Downloading: ${progress.progress}%")
                        }
                    }
                    DownloadManager.DownloadStatus.COMPLETED -> {
                        ToastManager.showToast(this, "Download completed!")
                        updateDownloadedSongs()
                    }
                    DownloadManager.DownloadStatus.FAILED -> {
                        ToastManager.showToast(this, "Download failed!")
                    }
                    else -> {}
                }
            }
        }
        
        // Load downloaded songs
        lifecycleScope.launch {
            offlineRepository.getAllDownloadedSongs().collect { downloadedSongs ->
                val downloadedIds = downloadedSongs.map { it.songId }.toSet()
                adapter.updateDownloadedSongs(downloadedIds)
            }
        }
    }
    
    private fun handleDownloadClick(song: Song) {
        lifecycleScope.launch {
            if (offlineRepository.isDownloaded(song.id)) {
                showDeleteDownloadDialog(song)
            } else {
                Log.d("MainActivity", "Starting download for: ${song.id} - ${song.name}")
                ToastManager.showToast(this@MainActivity, "Starting download...")
                val result = offlineRepository.downloadSong(song)
                if (result.isFailure) {
                    Log.e("MainActivity", "Download failed: ${result.exceptionOrNull()}")
                    ToastManager.showToast(this@MainActivity, "Download failed!")
                }
            }
        }
    }
    
    private fun showDeleteDownloadDialog(song: Song) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Download")
            .setMessage("Remove downloaded file for ${song.name}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    offlineRepository.deleteDownload(song.id)
                    ToastManager.showToast(this@MainActivity, "Download deleted")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun updateDownloadedSongs() {
        lifecycleScope.launch {
            val downloadedIds = offlineRepository.getDownloadedIds()
            adapter.updateDownloadedSongs(downloadedIds)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
        ToastManager.cancelToast()
    }
}
















































