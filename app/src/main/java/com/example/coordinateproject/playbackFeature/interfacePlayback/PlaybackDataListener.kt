package com.example.coordinateproject.playbackFeature.interfacePlayback

import android.util.Log
import com.example.coordinateproject.response.PlayBackData

interface PlaybackDataListener {
    fun onDataReceived(yourDataListOnPlayback: List<PlayBackData>){
        Log.d("Data", "catched")
    }
}