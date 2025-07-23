package com.example.appmussicapi.ui.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmussicapi.R
import com.example.appmussicapi.data.model.QueueItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QueueBottomSheet(
    private val queueManager: QueueManager,
    private val onSongSelected: (QueueItem) -> Unit
) : BottomSheetDialogFragment() {
    
    private lateinit var queueTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: View
    private lateinit var clearAllButton: ImageButton
    private lateinit var shuffleButton: ImageButton
    
    private lateinit var adapter: QueueAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_queue, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupRecyclerView()
        setupListeners()
        updateQueueInfo()
    }
    
    private fun initViews(view: View) {
        queueTitle = view.findViewById(R.id.queue_title)
        recyclerView = view.findViewById(R.id.recycler_view)
        emptyState = view.findViewById(R.id.empty_state)
        clearAllButton = view.findViewById(R.id.clear_all_button)
        shuffleButton = view.findViewById(R.id.shuffle_button)
    }
    
    private fun setupRecyclerView() {
        adapter = QueueAdapter(
            onItemClick = { queueItem ->
                onSongSelected(queueItem)
                dismiss()
            },
            onRemoveClick = { queueItem ->
                queueManager.removeFromQueue(queueItem.id)
            },
            onStartDrag = { viewHolder ->
                itemTouchHelper.startDrag(viewHolder)
            }
        )
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        
        // Setup drag & drop
        val callback = QueueItemTouchHelper { fromPos, toPos ->
            queueManager.moveItem(fromPos, toPos)
        }
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        
        // Load initial queue
        adapter.updateQueue(queueManager.getQueue())
    }
    
    private fun setupListeners() {
        clearAllButton.setOnClickListener {
            queueManager.clearQueue()
            dismiss()
        }
        
        shuffleButton.setOnClickListener {
            queueManager.shuffleQueue()
        }
        
        // Listen for queue changes
        queueManager.setOnQueueChangedListener { queueItems ->
            adapter.updateQueue(queueItems)
            updateQueueInfo()
        }
    }
    
    private fun updateQueueInfo() {
        val queueSize = queueManager.getQueueSize()
        queueTitle.text = "Queue ($queueSize songs)"
        
        if (queueSize == 0) {
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
