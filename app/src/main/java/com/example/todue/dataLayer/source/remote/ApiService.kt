package com.example.todue.dataLayer.source.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Fixed and adjusted with help from LLM

class ApiService {
    object RetrofitInstance {
        private const val BASE_URL = "https://api.giphy.com/v1/"
        const val API_KEY = "VxfAmibxWfDM0BWtRbcLkmUJS29e9Uyu"

        val giphyApi: GiphyApiService by lazy {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(GiphyApiService::class.java)
        }
    }
}