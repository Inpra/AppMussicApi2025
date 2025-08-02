package com.example.appmussicapi.data.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CloudinaryApiClient {
    private const val BASE_URL = "......"
    private const val API_KEY = "....."  // API Key từ Dashboard
    private const val API_SECRET = "......"  // API Secret từ Dashboard
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", Credentials.basic(API_KEY, API_SECRET))
                .build()
            chain.proceed(request)
        }
        .build()
    
    val apiService: CloudinaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApiService::class.java)
    }
}
