package com.example.appmussicapi.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import com.example.appmussicapi.R
import com.example.appmussicapi.databinding.DialogSleepTimerBinding

class SleepTimerDialog(
    context: Context,
    private val onTimerSet: (Long) -> Unit,
    private val onTimerCancel: () -> Unit
) : Dialog(context) {
    
    private lateinit var binding: DialogSleepTimerBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSleepTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNumberPickers()
        setupButtons()
    }
    
    private fun setupNumberPickers() {
        // Hours picker (0-23)
        binding.hoursPicker.apply {
            minValue = 0
            maxValue = 23
            value = 0
        }
        
        // Minutes picker (0-59)
        binding.minutesPicker.apply {
            minValue = 0
            maxValue = 59
            value = 15 // Default 15 minutes
        }
        
        // Quick preset buttons
        binding.preset5min.setOnClickListener {
            binding.hoursPicker.value = 0
            binding.minutesPicker.value = 5
        }
        
        binding.preset15min.setOnClickListener {
            binding.hoursPicker.value = 0
            binding.minutesPicker.value = 15
        }
        
        binding.preset30min.setOnClickListener {
            binding.hoursPicker.value = 0
            binding.minutesPicker.value = 30
        }
        
        binding.preset1hour.setOnClickListener {
            binding.hoursPicker.value = 1
            binding.minutesPicker.value = 0
        }
    }
    
    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        binding.btnSet.setOnClickListener {
            val hours = binding.hoursPicker.value
            val minutes = binding.minutesPicker.value
            val totalMs = (hours * 3600 + minutes * 60) * 1000L
            
            if (totalMs > 0) {
                onTimerSet(totalMs)
                dismiss()
            }
        }
        
        binding.btnStop.setOnClickListener {
            onTimerCancel()
            dismiss()
        }
    }
}