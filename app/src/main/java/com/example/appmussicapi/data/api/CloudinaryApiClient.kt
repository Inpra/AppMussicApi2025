package com.example.appmussicapi.data.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CloudinaryApiClient {
    private const val BASE_URL = "https://api.cloudinary.com/v1_1/dyugji8gz/"
    private const val API_KEY = "499594246135446"  // API Key từ Dashboard
    private const val API_SECRET = "2DmOi7v8c1D5jUhrltPophcAUaM"  // API Secret từ Dashboard
    
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
