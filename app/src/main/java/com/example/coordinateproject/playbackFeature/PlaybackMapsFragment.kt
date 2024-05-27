package com.example.coordinateproject.playbackFeature

import android.graphics.BitmapFactory
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coordinateproject.R
import com.example.coordinateproject.response.ApiResponse
import com.example.coordinateproject.response.PBVTS
import com.example.coordinateproject.response.PlayBackData
import com.example.coordinateproject.responseBypass.PlaybackAPI
import com.example.coordinateproject.responseBypass.WMOShip

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaybackMapsFragment : Fragment() {

    private var tokenAPI: String = "73ob73y64nt3n653k4l1"
    private lateinit var mMap:GoogleMap

    private val callback = OnMapReadyCallback { mMap ->
        val latitude = -6.9135609 // Lattitude WMO
        val longitude = 112.5814275 // Longtitude WMO
        val mapAwal = LatLng(latitude,longitude) // WMO
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapAwal, 8.5f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playback_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    // Pengambilan data untuk kapal OSES
    private fun makePBCall(mmsi: String, from: String, to: String) {
        val call = PlaybackAPI.PBRetrofit.apiService.getPBVTSData(mmsi, from, to)
        call.enqueue(object : Callback<PBVTS> {
            override fun onResponse(call: Call<PBVTS>, response: Response<PBVTS>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        // Handle the response data
                        val yourDataList = data.data

                        for (item in yourDataList) {
                            val speed = item.speed
                            val stamp = item.stamp
                            val lat = item.lat
                            val lon = item.lon
                            val heading = item.hdg

                            val location = LatLng(lat,lon)

//                            // Marker ini khusus untuk mengetahui lokasi, nama kapal, dan arah kapal melaju menggunakan custom marker
//                            setCustomMarker(location, name, heading, calcspeed.toDouble(), date, imo, mmsi, 0, "", typeData)
                        }
                    }
                } else {
                    // Handle unsuccessful response (e.g., non-2xx status codes)
                    val errorResponseCode = response.code() // HTTP status code
                    val errorMessage = response.errorBody()?.string()
                    Log.d("error", "$errorResponseCode : $errorMessage")
                }
            }

            override fun onFailure(call: Call<PBVTS>, t: Throwable) {

            }

        })
    }

    private fun setCustomMarker(
        location: LatLng,
        name: String,
        heading: Float,
        calcspeed: Double,
        date: String,
        imo: String,
        mmsi: String,
        type: Int,
        type_name: String,
        customMarkerType: Int
    ) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(name)

        // Load custom marker icon from drawable based on customMarkerType
        val iconResource =
            if (customMarkerType == 1) R.drawable.custom_marker_icon
            else R.drawable.poi_marker

        val iconBitmap = BitmapFactory.decodeResource(resources, iconResource)

        // Rotate the custom icon according to the heading
        val rotatedBitmap = rotateBitmap(iconBitmap, heading)

        // Set the custom rotated icon for the marker
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap))

        val customInfoMarker =
            if (customMarkerType == 1) com.example.coordinateproject.customMarker.CustomInfoWMO(requireContext(), imo, mmsi, calcspeed.toInt(), name, date)
            else com.example.coordinateproject.customMarker.CustomInfoPOI(requireContext(), name, type, type_name)

        mMap.setInfoWindowAdapter(customInfoMarker)
        val marker = mMap.addMarker(markerOptions)
        marker?.tag = customInfoMarker
    }

    
}