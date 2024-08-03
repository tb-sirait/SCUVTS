package com.example.coordinateproject.playbackFeature.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coordinateproject.response.PlayBackData
import com.google.android.gms.maps.model.LatLng

class PlaybackViewModel: ViewModel() {
    private val _playbackData = MutableLiveData<List<PlayBackData>>()
    val playbackData: LiveData<List<PlayBackData>> get() = _playbackData

    //menyimpan data untuk simulasi
    private val _latLngList = MutableLiveData<List<LatLng>>()
    val latLngList: LiveData<List<LatLng>> get() = _latLngList

    fun setPlaybackData(data: List<PlayBackData>) {
        _playbackData.value = data
        _latLngList.value = data.map { LatLng(it.lat, it.lon) }
    }
}