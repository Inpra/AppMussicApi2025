package com.example.appmussicapi.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class DownloadInfo(
    val songId: String,
    val name: String,
    val artist: String,
    val localPath: String,
    val imageUrl: String?,
    val downloadedAt: Long = System.currentTimeMillis()
)

class DownloadStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("downloads", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveDownload(download: DownloadInfo) {
        val downloads = getDownloads().toMutableList()
        downloads.removeAll { it.songId == download.songId }
        downloads.add(download)
        
        val json = gson.toJson(downloads)
        prefs.edit().putString("download_list", json).apply()
    }
    
    fun getDownloads(): List<DownloadInfo> {
        return try {
            val json = prefs.getString("download_list", "[]")
            val type = object : TypeToken<List<DownloadInfo>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun deleteDownload(songId: String) {
        val downloads = getDownloads().toMutableList()
        downloads.removeAll { it.songId == songId }
        
        val json = gson.toJson(downloads)
        prefs.edit().putString("download_list", json).apply()
    }
    
    fun isDownloaded(songId: String): Boolean {
        return getDownloads().any { it.songId == songId }
    }
    
    fun getLocalPath(songId: String): String? {
        return getDownloads().find { it.songId == songId }?.localPath
    }
    
    fun getDownloadedIds(): Set<String> {
        return getDownloads().map { it.songId }.toSet()
    }
}