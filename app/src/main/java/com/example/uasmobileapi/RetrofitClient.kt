package com.example.uasmobileapi

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL SUDAH BENAR: Diakhiri '/' dan tanpa 'api.php'
    private const val BASE_URL = "http://104.248.153.158/event-api/"

    val instance: ApiService by lazy {

        // Setup Logging Interceptor (Agar bisa lihat error di Logcat)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        // Build Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Pasang client logger
            .build()

        retrofit.create(ApiService::class.java)
    }
}