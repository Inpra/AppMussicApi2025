package com.example.appmussicapi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.appmussicapi.R
import com.example.appmussicapi.data.model.Song
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import android.util.Log
import androidx.core.content.ContextCompat
import android.widget.PopupMenu

class SongAdapter(
    private val onSongClick: (Song) -> Unit,
    private val onDownloadClick: (Song) -> Unit = {},
    private val onAddToQueue: (Song) -> Unit = {},
    private val onPlayNext: (Song) -> Unit = {}
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    
    private var allSongs: List<Song> = emptyList()
    private var filteredSongs: List<Song> = emptyList()
    private val downloadedSongs = mutableSetOf<String>()
    
    fun updateSongs(newSongs: List<Song>) {
        Log.d("SongAdapter", "Updating songs: ${newSongs.size}")
        allSongs = newSongs
        filteredSongs = newSongs
        notifyDataSetChanged()
    }
    
    fun filter(query: String) {
        Log.d("SongAdapter", "Filtering with query: '$query'")
        Log.d("SongAdapter", "All songs count: ${allSongs.size}")
        
        filteredSongs = if (query.isEmpty()) {
            allSongs
        } else {
            allSongs.filter { song ->
                val nameMatch = song.name.contains(query, ignoreCase = true)
                val artistMatch = song.artist.contains(query, ignoreCase = true)
                Log.d("SongAdapter", "Song: ${song.name}, Name match: $nameMatch, Artist match: $artistMatch")
                nameMatch || artistMatch
            }
        }
        
        Log.d("SongAdapter", "Filtered songs count: ${filteredSongs.size}")
        notifyDataSetChanged()
    }

    fun updateDownloadedSongs(downloadedIds: Set<String>) {
        downloadedSongs.clear()
        downloadedSongs.addAll(downloadedIds)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(filteredSongs[position])
    }

    override fun getItemCount(): Int = filteredSongs.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName: TextView = itemView.findViewById(R.id.songName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val songDuration: TextView = itemView.findViewById(R.id.songDuration)
        private val songThumbnail: ImageView = itemView.findViewById(R.id.songThumbnail)
        private val playButton: ImageButton = itemView.findViewById(R.id.playButton)
        private val downloadButton: ImageView = itemView.findViewById(R.id.downloadButton)

        fun bind(song: Song) {
            songName.text = song.name
            artistName.text = song.artist
            songDuration.text = formatDuration(song.duration)
            
            // Update download button state
            if (downloadedSongs.contains(song.id)) {
                downloadButton.setImageResource(R.drawable.ic_downloaded)
                downloadButton.setColorFilter(ContextCompat.getColor(itemView.context, R.color.button_active_cyan))
            } else {
                downloadButton.setImageResource(R.drawable.ic_download)
                downloadButton.setColorFilter(ContextCompat.getColor(itemView.context, R.color.button_inactive))
            }
            
            // Load image with Glide
            Glide.with(itemView.context)
                .load(song.imageUrl)
                .placeholder(R.drawable.default_album_art)
                .error(R.drawable.default_album_art)
                .transform(RoundedCorners(16))
                .into(songThumbnail)
            
            itemView.setOnClickListener { onSongClick(song) }
            playButton.setOnClickListener { onSongClick(song) }
            downloadButton.setOnClickListener { onDownloadClick(song) }
            setupContextMenu(song)
        }
        
        private fun formatDuration(durationMs: Long): String {
            if (durationMs <= 0) return "3:45" // Default duration
            val seconds = (durationMs / 1000) % 60
            val minutes = (durationMs / (1000 * 60)) % 60
            return String.format("%d:%02d", minutes, seconds)
        }

        private fun setupContextMenu(song: Song) {
            itemView.setOnLongClickListener {
                showContextMenu(song)
                true
            }
        }
        
        private fun showContextMenu(song: Song) {
            val popup = PopupMenu(itemView.context, itemView)
            popup.menuInflater.inflate(R.menu.song_context_menu, popup.menu)
            
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_add_to_queue -> {
                        onAddToQueue(song)
                        true
                    }
                    R.id.action_play_next -> {
                        onPlayNext(song)
                        true
                    }
                    else -> false
                }
            }
            
            popup.show()
        }
    }
}
