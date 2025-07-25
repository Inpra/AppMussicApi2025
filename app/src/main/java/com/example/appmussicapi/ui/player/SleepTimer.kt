package com.example.appmussicapi.ui.player

import android.os.CountDownTimer
import android.util.Log

class SleepTimer {
    private var countDownTimer: CountDownTimer? = null
    private var musicPlayer: MusicPlayer? = null
    private var onTimerUpdateListener: ((Long) -> Unit)? = null
    private var onTimerFinishedListener: (() -> Unit)? = null
    private var isActive = false
    private var remainingTime: Long = 0
    
    fun setMusicPlayer(player: MusicPlayer) {
        this.musicPlayer = player
    }
    
    fun setOnTimerUpdateListener(listener: (Long) -> Unit) {
        onTimerUpdateListener = listener
    }
    
    fun setOnTimerFinishedListener(listener: () -> Unit) {
        onTimerFinishedListener = listener
    }
    
    fun startTimer(durationMs: Long) {
        Log.d("SleepTimer", "Starting timer for ${durationMs}ms")
        stopTimer()
        
        if (musicPlayer == null) {
            Log.e("SleepTimer", "MusicPlayer is null!")
            return
        }
        
        countDownTimer = object : CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                Log.d("SleepTimer", "Timer tick: ${formatTime(millisUntilFinished)}")
                onTimerUpdateListener?.invoke(millisUntilFinished)
                
                // Fade out volume in last 10 seconds
                if (millisUntilFinished <= 10000) {
                    val volume = (millisUntilFinished / 10000f).coerceIn(0f, 1f)
                    musicPlayer?.getPlayer()?.volume = volume
                    Log.d("SleepTimer", "Fading volume: $volume")
                }
            }
            
            override fun onFinish() {
                Log.d("SleepTimer", "Timer finished - pausing music")
                musicPlayer?.pause()
                musicPlayer?.getPlayer()?.volume = 1.0f // Reset volume
                isActive = false
                remainingTime = 0
                onTimerFinishedListener?.invoke()
                Log.d("SleepTimer", "Sleep timer finished")
            }
        }
        
        countDownTimer?.start()
        isActive = true
        Log.d("SleepTimer", "Sleep timer started successfully")
    }
    
    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        isActive = false
        remainingTime = 0
        
        // Reset volume
        musicPlayer?.getPlayer()?.volume = 1.0f
        Log.d("SleepTimer", "Sleep timer stopped")
    }
    
    fun isActive(): Boolean = isActive
    
    fun getRemainingTime(): Long = remainingTime
    
    fun formatTime(timeMs: Long): String {
        val minutes = (timeMs / 1000) / 60
        val seconds = (timeMs / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

