package com.example.appmussicapi.ui.player

import android.os.CountDownTimer
import android.util.Log

class SleepTimer {
    private var countDownTimer: CountDownTimer? = null
    private var fadeOutTimer: CountDownTimer? = null
    private var musicPlayer: MusicPlayer? = null
    private var originalVolume: Float = 1.0f
    private var isActive = false
    private var remainingTimeMs: Long = 0
    private var isFadingOut = false
    
    // Callbacks
    private var onTimerUpdateListener: ((Long) -> Unit)? = null
    private var onTimerFinishedListener: (() -> Unit)? = null
    
    fun setMusicPlayer(player: MusicPlayer) {
        this.musicPlayer = player
        this.originalVolume = player.getPlayer().volume
    }
    
    fun startTimer(durationMs: Long) {
        stopTimer()
        
        isActive = true
        isFadingOut = false
        remainingTimeMs = durationMs
        
        // Reset volume to original when starting new timer
        resetVolume()
        
        countDownTimer = object : CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMs = millisUntilFinished
                onTimerUpdateListener?.invoke(millisUntilFinished)
                
                // Bắt đầu fade out trong 30 giây cuối
                if (millisUntilFinished <= 30000 && !isFadingOut) {
                    startFadeOut(millisUntilFinished)
                }
            }
            
            override fun onFinish() {
                Log.d("SleepTimer", "Timer finished")
                musicPlayer?.pause()
                // DON'T reset volume here - keep it at 0
                isActive = false
                isFadingOut = false
                onTimerFinishedListener?.invoke()
            }
        }
        
        countDownTimer?.start()
        Log.d("SleepTimer", "Sleep timer started for ${durationMs}ms")
    }
    
    private fun startFadeOut(remainingMs: Long) {
        isFadingOut = true
        val fadeSteps = (remainingMs / 1000).toInt() // 1 step per second
        val volumeStep = originalVolume / fadeSteps
        
        fadeOutTimer = object : CountDownTimer(remainingMs, 1000) {
            private var currentStep = 0
            
            override fun onTick(millisUntilFinished: Long) {
                currentStep++
                val newVolume = originalVolume - (volumeStep * currentStep)
                val clampedVolume = maxOf(0f, newVolume)
                
                musicPlayer?.getPlayer()?.volume = clampedVolume
                Log.d("SleepTimer", "Fade out: volume = $clampedVolume")
            }
            
            override fun onFinish() {
                musicPlayer?.getPlayer()?.volume = 0f
            }
        }
        
        fadeOutTimer?.start()
    }
    
    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        
        fadeOutTimer?.cancel()
        fadeOutTimer = null
        
        // Always reset volume when stopping timer
        resetVolume()
        isActive = false
        isFadingOut = false
        remainingTimeMs = 0
        
        Log.d("SleepTimer", "Sleep timer stopped")
    }
    
    private fun resetVolume() {
        musicPlayer?.getPlayer()?.volume = originalVolume
        Log.d("SleepTimer", "Volume reset to: $originalVolume")
    }
    
    fun isActive(): Boolean = isActive
    
    fun getRemainingTime(): Long = remainingTimeMs
    
    fun setOnTimerUpdateListener(listener: (Long) -> Unit) {
        onTimerUpdateListener = listener
    }
    
    fun setOnTimerFinishedListener(listener: () -> Unit) {
        onTimerFinishedListener = listener
    }
    
    fun formatTime(timeMs: Long): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        val hours = (timeMs / (1000 * 60 * 60)) % 24
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
}
