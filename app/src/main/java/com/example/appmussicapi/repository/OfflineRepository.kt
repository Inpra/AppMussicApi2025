package com.example.appmussicapi.repository

import android.content.Context
import android.util.Log
import com.example.appmussicapi.data.download.DownloadManager
import com.example.appmussicapi.data.model.Song
import com.example.appmussicapi.data.storage.DownloadInfo
import com.example.appmussicapi.data.storage.DownloadStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OfflineRepository(context: Context) {
    private val downloadStorage = DownloadStorage(context)
    private val downloadManager = DownloadManager(context)
    
    fun getAllDownloadedSongs(): Flow<List<DownloadInfo>> = flow {
        emit(downloadStorage.getDownloads())
    }
    
    suspend fun downloadSong(song: Song): Result<String> {
        return try {
            val result = downloadManager.downloadSong(song.id, song.name, song.url)
            
            if (result.isSuccess) {
                val localPath = result.getOrNull()!!
                val downloadInfo = DownloadInfo(
                    songId = song.id,
                    name = song.name,
                    artist = song.artist,
                    localPath = localPath,
                    imageUrl = song.imageUrl
                )
                downloadStorage.saveDownload(downloadInfo)
                Log.d("OfflineRepository", "Song downloaded and saved")
            }
            
            result
        } catch (e: Exception) {
            Log.e("OfflineRepository", "Error downloading song", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteDownload(songId: String): Boolean {
        return try {
            downloadStorage.deleteDownload(songId)
            downloadManager.deleteDownload(songId)
            true
        } catch (e: Exception) {
            Log.e("OfflineRepository", "Error deleting download", e)
            false
        }
    }
    
    suspend fun isDownloaded(songId: String): Boolean = downloadStorage.isDownloaded(songId)
    
    suspend fun getLocalPath(songId: String): String? = downloadStorage.getLocalPath(songId)
    
    fun getDownloadedIds(): Set<String> = downloadStorage.getDownloadedIds()
    
    fun setDownloadProgressListener(listener: (DownloadManager.DownloadProgress) -> Unit) {
        downloadManager.setOnProgressListener(listener)
    }
}

