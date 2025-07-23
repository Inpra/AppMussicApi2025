package com.example.appmussicapi.ui.queue

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appmussicapi.R
import com.example.appmussicapi.data.model.QueueItem
import java.util.*

class QueueAdapter(
    private val onItemClick: (QueueItem) -> Unit,
    private val onRemoveClick: (QueueItem) -> Unit,
    private val onStartDrag: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {
    
    private var queueItems: MutableList<QueueItem> = mutableListOf()
    
    fun updateQueue(newItems: List<QueueItem>) {
        queueItems.clear()
        queueItems.addAll(newItems)
        notifyDataSetChanged()
    }
    
    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(queueItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        holder.bind(queueItems[position], position)
    }
    
    override fun getItemCount(): Int = queueItems.size
    
    inner class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val albumArt: ImageView = itemView.findViewById(R.id.album_art)
        private val songTitle: TextView = itemView.findViewById(R.id.song_title)
        private val artistName: TextView = itemView.findViewById(R.id.artist_name)
        private val removeButton: ImageView = itemView.findViewById(R.id.remove_button)
        private val dragHandle: ImageView = itemView.findViewById(R.id.drag_handle)
        private val nowPlayingIndicator: View = itemView.findViewById(R.id.now_playing_indicator)
        
        fun bind(queueItem: QueueItem, position: Int) {
            val song = queueItem.song
            
            songTitle.text = song.name
            artistName.text = song.artist
            
            // Load album art
            Glide.with(itemView.context)
                .load(song.imageUrl)
                .placeholder(R.drawable.default_album_art)
                .into(albumArt)
            
            // Show/hide now playing indicator
            if (queueItem.isCurrentPlaying) {
                nowPlayingIndicator.visibility = View.VISIBLE
                songTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary_color))
                artistName.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary_color))
            } else {
                nowPlayingIndicator.visibility = View.GONE
                songTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary))
                artistName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_secondary))
            }
            
            // Click listeners
            itemView.setOnClickListener {
                onItemClick(queueItem)
            }
            
            removeButton.setOnClickListener {
                onRemoveClick(queueItem)
            }
            
            // Drag handle
            dragHandle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onStartDrag(this)
                }
                false
            }
        }
    }
}