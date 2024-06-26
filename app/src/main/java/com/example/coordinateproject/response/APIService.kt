package com.example.coordinateproject.response

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface APIService {
    @GET("https://api.scu.co.id/vtms/wmo") // Kapal OSES
    fun getData(@Header("Authorization") token: String): Call<ApiResponse> // Data

    @GET("https://api.scu.co.id/vtms/wmoarea") // Kapal Lainnya
    fun getAllDataKapal(@Header("Authorization") token: String): Call<wmoarea> // DataXX

    @GET("https://api.scu.co.id/vtms/wmo/poi") // Point of Interest (POI)
    fun getPOI(@Header("Authorization") token: String): Call<POIData> // DataXXX

    @GET("https://api.scu.co.id/vtms/wmo/pbvts") // Playback
    fun getPBVTSData(
        @Query("mmsi") mmsi: String,
        @Query("from") fromDate: String,
        @Query("to") toDate: String,
        @Header("Authorization") token: String
    ): Call<PBVTS>
}