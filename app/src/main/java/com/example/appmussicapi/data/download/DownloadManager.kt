package com.example.appmussicapi.data.download

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadManager(private val context: Context) {
    private val client = OkHttpClient()
    private val downloadJobs = mutableMapOf<String, Job>()
    
    data class DownloadProgress(
        val songId: String,
        val progress: Int,
        val status: DownloadStatus
    )
    
    enum class DownloadStatus {
        PENDING, DOWNLOADING, COMPLETED, FAILED, CANCELLED
    }
    
    private var onProgressListener: ((DownloadProgress) -> Unit)? = null
    
    fun setOnProgressListener(listener: (DownloadProgress) -> Unit) {
        onProgressListener = listener
    }
    
    suspend fun downloadSong(
        songId: String,
        songName: String,
        url: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Clean filename - remove special characters
            val cleanSongName = songName.replace("[^a-zA-Z0-9\\s]".toRegex(), "")
                .replace("\\s+".toRegex(), "_")
                .take(50) // Limit length
            
            val fileName = "${songId.replace("/", "_")}_${cleanSongName}.mp3"
            
            // Create downloads directory
            val downloadDir = File(context.getExternalFilesDir(null), "downloads")
            if (!downloadDir.exists()) {
                val created = downloadDir.mkdirs()
                Log.d("DownloadManager", "Download directory created: $created")
            }
            
            val file = File(downloadDir, fileName)
            Log.d("DownloadManager", "Download path: ${file.absolutePath}")
            
            if (file.exists()) {
                Log.d("DownloadManager", "File already exists")
                return@withContext Result.success(file.absolutePath)
            }
            
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                throw IOException("Download failed: ${response.code}")
            }
            
            val body = response.body ?: throw IOException("Empty response body")
            val contentLength = body.contentLength()
            
            onProgressListener?.invoke(
                DownloadProgress(songId, 0, DownloadStatus.DOWNLOADING)
            )
            
            // Create file if parent directory exists
            if (!file.parentFile?.exists()!!) {
                file.parentFile?.mkdirs()
            }
            
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            
            val buffer = ByteArray(8192)
            var totalBytesRead = 0L
            var bytesRead: Int
            var lastProgressReported = -1
            
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                
                if (contentLength > 0) {
                    val progress = ((totalBytesRead * 100) / contentLength).toInt()
                    
                    // Chỉ report progress mỗi 10% để tránh spam
                    if (progress != lastProgressReported && progress % 10 == 0) {
                        onProgressListener?.invoke(
                            DownloadProgress(songId, progress, DownloadStatus.DOWNLOADING)
                        )
                        lastProgressReported = progress
                    }
                }
            }
            
            outputStream.close()
            inputStream.close()
            
            onProgressListener?.invoke(
                DownloadProgress(songId, 100, DownloadStatus.COMPLETED)
            )
            
            Log.d("DownloadManager", "Download completed: ${file.absolutePath}")
            Result.success(file.absolutePath)
            
        } catch (e: Exception) {
            Log.e("DownloadManager", "Download failed for $songId", e)
            onProgressListener?.invoke(
                DownloadProgress(songId, 0, DownloadStatus.FAILED)
            )
            Result.failure(e)
        }
    }
    
    fun startDownload(songId: String, songName: String, url: String) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            downloadSong(songId, songName, url)
        }
        downloadJobs[songId] = job
    }
    
    fun cancelDownload(songId: String) {
        downloadJobs[songId]?.cancel()
        downloadJobs.remove(songId)
        onProgressListener?.invoke(
            DownloadProgress(songId, 0, DownloadStatus.CANCELLED)
        )
    }
    
    fun isDownloaded(songId: String): Boolean {
        val downloadDir = File(context.getExternalFilesDir(null), "downloads")
        if (!downloadDir.exists()) return false
        
        val cleanSongId = songId.replace("/", "_")
        return downloadDir.listFiles()?.any { 
            it.name.startsWith(cleanSongId) 
        } ?: false
    }
    
    fun getDownloadedPath(songId: String): String? {
        val downloadDir = File(context.getExternalFilesDir(null), "downloads")
        if (!downloadDir.exists()) return null
        
        val cleanSongId = songId.replace("/", "_")
        val file = downloadDir.listFiles()?.find { 
            it.name.startsWith(cleanSongId) 
        }
        return file?.absolutePath
    }
    
    fun deleteDownload(songId: String): Boolean {
        val downloadDir = File(context.getExternalFilesDir(null), "downloads")
        if (!downloadDir.exists()) return false
        
        val cleanSongId = songId.replace("/", "_")
        val file = downloadDir.listFiles()?.find { 
            it.name.startsWith(cleanSongId) 
        }
        return file?.delete() ?: false
    }
}


