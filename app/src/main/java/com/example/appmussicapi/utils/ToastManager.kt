package com.example.appmussicapi.utils

import android.content.Context
import android.widget.Toast

object ToastManager {
    private var currentToast: Toast? = null
    
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        // Cancel previous toast if exists
        currentToast?.cancel()
        
        // Create and show new toast
        currentToast = Toast.makeText(context, message, duration)
        currentToast?.show()
    }
    
    fun cancelToast() {
        currentToast?.cancel()
        currentToast = null
    }
}
