package com.example.coordinateproject.response

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface APIService {
    @GET("https://api.scu.co.id/vtms/wmo") // Kapal OSES
    fun getData(@Header("Authorization") token: String): Call<ApiResponse> // Data

    @GET("https://api.scu.co.id/vtms/wmoarea") // Kapal Lainnya
    fun getAllDataKapal(@Header("Authorization") token: String): Call<wmoarea> // DataXX

    @GET("https://api.scu.co.id/vtms/wmo/poi") // Point of Interest (POI)
    fun getPOI(@Header("Authorization") token: String): Call<POIData> // DataXXX

//    @GET("https://api.scu.co.id/vtms/wmo/pbvts?mmsi={$mmsi}&from={$fromTime}&to={$toTime}")
//    fun getPBVTS(
//        @Header("Authorization") token: String,
//        fromTime: String,
//        toTime: String,
//        mmsi: String
//    )
}