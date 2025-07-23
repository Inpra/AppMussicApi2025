package com.example.appmussicapi.ui.queue

import android.util.Log
import com.example.appmussicapi.data.model.QueueItem
import com.example.appmussicapi.data.model.Song
import com.example.appmussicapi.data.model.PlayQueue

class QueueManager {
    private val playQueue = PlayQueue()
    private var onQueueChangedListener: ((List<QueueItem>) -> Unit)? = null
    private var onCurrentIndexChangedListener: ((Int) -> Unit)? = null
    
    fun setOnQueueChangedListener(listener: (List<QueueItem>) -> Unit) {
        onQueueChangedListener = listener
    }
    
    fun setOnCurrentIndexChangedListener(listener: (Int) -> Unit) {
        onCurrentIndexChangedListener = listener
    }
    
    // Set initial playlist
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        playQueue.items.clear()
        playQueue.originalPlaylist = songs
        
        songs.forEachIndexed { index, song ->
            playQueue.items.add(
                QueueItem(
                    song = song,
                    isCurrentPlaying = index == startIndex
                )
            )
        }
        
        playQueue.currentIndex = startIndex
        notifyQueueChanged()
        Log.d("QueueManager", "Playlist set with ${songs.size} songs")
    }
    
    // Add song to end of queue
    fun addToQueue(song: Song) {
        val queueItem = QueueItem(song = song)
        playQueue.items.add(queueItem)
        notifyQueueChanged()
        Log.d("QueueManager", "Added to queue: ${song.name}")
    }
    
    // Add song after current playing song
    fun playNext(song: Song) {
        val queueItem = QueueItem(song = song)
        val insertIndex = playQueue.currentIndex + 1
        
        if (insertIndex < playQueue.items.size) {
            playQueue.items.add(insertIndex, queueItem)
        } else {
            playQueue.items.add(queueItem)
        }
        
        notifyQueueChanged()
        Log.d("QueueManager", "Added to play next: ${song.name}")
    }
    
    // Remove item from queue
    fun removeFromQueue(queueItemId: String) {
        val index = playQueue.items.indexOfFirst { it.id == queueItemId }
        if (index != -1) {
            playQueue.items.removeAt(index)
            
            // Adjust current index if needed
            if (index < playQueue.currentIndex) {
                playQueue.currentIndex--
            } else if (index == playQueue.currentIndex && playQueue.items.isNotEmpty()) {
                // If removed current song, don't change index (next song will be at same index)
                if (playQueue.currentIndex >= playQueue.items.size) {
                    playQueue.currentIndex = playQueue.items.size - 1
                }
            }
            
            notifyQueueChanged()
            onCurrentIndexChangedListener?.invoke(playQueue.currentIndex)
        }
    }
    
    // Move item in queue (drag & drop)
    fun moveItem(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex || fromIndex < 0 || toIndex < 0 || 
            fromIndex >= playQueue.items.size || toIndex >= playQueue.items.size) {
            return
        }
        
        val item = playQueue.items.removeAt(fromIndex)
        playQueue.items.add(toIndex, item)
        
        // Update current index
        when {
            fromIndex == playQueue.currentIndex -> {
                playQueue.currentIndex = toIndex
            }
            fromIndex < playQueue.currentIndex && toIndex >= playQueue.currentIndex -> {
                playQueue.currentIndex--
            }
            fromIndex > playQueue.currentIndex && toIndex <= playQueue.currentIndex -> {
                playQueue.currentIndex++
            }
        }
        
        notifyQueueChanged()
        onCurrentIndexChangedListener?.invoke(playQueue.currentIndex)
        Log.d("QueueManager", "Moved item from $fromIndex to $toIndex")
    }
    
    // Get current queue
    fun getQueue(): List<QueueItem> = playQueue.items.toList()
    
    // Get current playing song
    fun getCurrentSong(): Song? {
        return if (playQueue.currentIndex >= 0 && playQueue.currentIndex < playQueue.items.size) {
            playQueue.items[playQueue.currentIndex].song
        } else null
    }
    
    // Get next song
    fun getNextSong(): Song? {
        val nextIndex = playQueue.currentIndex + 1
        return if (nextIndex < playQueue.items.size) {
            playQueue.items[nextIndex].song
        } else null
    }
    
    // Move to next song
    fun moveToNext(): Song? {
        if (playQueue.currentIndex + 1 < playQueue.items.size) {
            updateCurrentIndex(playQueue.currentIndex + 1)
            return getCurrentSong()
        }
        return null
    }
    
    // Move to previous song
    fun moveToPrevious(): Song? {
        if (playQueue.currentIndex > 0) {
            updateCurrentIndex(playQueue.currentIndex - 1)
            return getCurrentSong()
        }
        return null
    }
    
    // Jump to specific song in queue
    fun jumpToSong(queueItemId: String): Song? {
        val index = playQueue.items.indexOfFirst { it.id == queueItemId }
        if (index != -1) {
            updateCurrentIndex(index)
            return getCurrentSong()
        }
        return null
    }
    
    // Clear queue
    fun clearQueue() {
        playQueue.items.clear()
        playQueue.currentIndex = -1
        notifyQueueChanged()
    }
    
    // Shuffle queue
    fun shuffleQueue() {
        if (playQueue.items.isEmpty()) return
        
        val currentSong = getCurrentSong()
        val remainingItems = playQueue.items.drop(playQueue.currentIndex + 1).shuffled()
        
        // Keep current and previous songs in place, shuffle the rest
        val newQueue = mutableListOf<QueueItem>()
        newQueue.addAll(playQueue.items.take(playQueue.currentIndex + 1))
        newQueue.addAll(remainingItems)
        
        playQueue.items.clear()
        playQueue.items.addAll(newQueue)
        playQueue.isShuffled = true
        
        notifyQueueChanged()
    }
    
    // Get queue size
    fun getQueueSize(): Int = playQueue.items.size
    
    // Get current index
    fun getCurrentIndex(): Int = playQueue.currentIndex
    
    private fun updateCurrentIndex(newIndex: Int) {
        // Update old current item
        if (playQueue.currentIndex >= 0 && playQueue.currentIndex < playQueue.items.size) {
            val oldItem = playQueue.items[playQueue.currentIndex]
            playQueue.items[playQueue.currentIndex] = oldItem.copy(isCurrentPlaying = false)
        }
        
        // Update new current item
        playQueue.currentIndex = newIndex
        if (newIndex >= 0 && newIndex < playQueue.items.size) {
            val newItem = playQueue.items[newIndex]
            playQueue.items[newIndex] = newItem.copy(isCurrentPlaying = true)
        }
        
        notifyQueueChanged()
        onCurrentIndexChangedListener?.invoke(playQueue.currentIndex)
    }
    
    private fun notifyQueueChanged() {
        onQueueChangedListener?.invoke(playQueue.items.toList())
    }
}
