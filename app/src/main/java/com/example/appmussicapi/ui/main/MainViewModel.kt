package com.example.appmussicapi.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmussicapi.data.model.Song
import com.example.appmussicapi.repository.SongRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = SongRepository()
    
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs
    
    fun loadSongs() {
        viewModelScope.launch {
            try {
                _songs.value = repository.getSongs()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading songs: ${e.message}", e)
                _songs.value = emptyList()
            }
        }
    }
}
