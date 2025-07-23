package com.example.appmussicapi.ui.queue

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class QueueItemTouchHelper(
    private val onItemMoved: (Int, Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    0
) {
    
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        
        (recyclerView.adapter as? QueueAdapter)?.moveItem(fromPosition, toPosition)
        onItemMoved(fromPosition, toPosition)
        
        return true
    }
    
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Not used
    }
    
    override fun isLongPressDragEnabled(): Boolean = false
    
    override fun isItemViewSwipeEnabled(): Boolean = false
}