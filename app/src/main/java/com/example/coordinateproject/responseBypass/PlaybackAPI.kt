package com.example.coordinateproject.responseBypass

import com.example.coordinateproject.response.APIService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaybackAPI {
    object PBRetrofit {
        private const val BASE_URL = "https://api.scu.co.id/vtms/pbvts/"
        private const val AUTH_TOKEN = "73ob73y64nt3n653k4l1"
        private val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer $AUTH_TOKEN")
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        val apiService: APIService = retrofit.create(APIService::class.java)
    }
}