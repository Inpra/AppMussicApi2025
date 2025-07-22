package com.example.appmussicapi.data.api

import com.example.appmussicapi.data.model.CloudinaryResponse
import com.example.appmussicapi.data.model.Song
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface CloudinaryApiService {
    @GET("resources/video")
    suspend fun getResources(
        @Query("prefix") prefix: String = "nhac_mp3",
        @Query("max_results") maxResults: Int = 100,
        @Query("resource_type") resourceType: String = "video"
    ): CloudinaryResponse
    
    @GET
    suspend fun getSongsList(@Url url: String): List<Song>
}
