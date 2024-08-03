package com.example.coordinateproject.playbackFeature.interfacePlayback

import com.example.coordinateproject.response.PlayBackData

interface PlaybackMapsUpdater {
    fun updateMarkers(data: List<PlayBackData>)
}