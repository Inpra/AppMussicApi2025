package com.example.appmussicapi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmussicapi.R
import com.example.appmussicapi.data.model.Song

class SongAdapter(
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    
    private var allSongs: List<Song> = emptyList()
    private var filteredSongs: List<Song> = emptyList()

    fun updateSongs(newSongs: List<Song>) {
        allSongs = newSongs
        filteredSongs = newSongs
        notifyDataSetChanged()
    }
    
    fun filter(query: String) {
        filteredSongs = if (query.isEmpty()) {
            allSongs
        } else {
            allSongs.filter { song ->
                song.name.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true)
            }
        }
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

        fun bind(song: Song) {
            songName.text = "${song.name} - ${song.artist}"
            itemView.setOnClickListener {
                onSongClick(song)
            }
        }
    }
}
