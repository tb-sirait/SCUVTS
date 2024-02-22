package com.example.coordinateproject.response

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface APIService {
    @GET("https://api.scu.co.id/vtms/wmo")
    fun getData(@Header("Authorization") token: String): Call<ApiResponse>

    @GET("https://api.scu.co.id/vtms/wmoarea")
    fun getAllDataKapal(@Header("Authorization") token: String): Call<wmoarea>
}