package com.example.coordinateproject.playbackFeature

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.coordinateproject.ErrorServer
import com.example.coordinateproject.R
import com.example.coordinateproject.playbackFeature.ViewModel.PlaybackViewModel
import com.example.coordinateproject.playbackFeature.interfacePlayback.PlaybackDataListener
import com.example.coordinateproject.response.POIData
import com.example.coordinateproject.response.PlayBackData
import com.example.coordinateproject.responseBypass.POI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaybackMapsFragment : Fragment(), PlaybackDataListener {

    private lateinit var mMap: GoogleMap
    private var mmsi: String?= ""
    private var fromDate: String?= ""
    private var toDate: String?= ""
    private var tokenAPI: String = "73ob73y64nt3n653k4l1"
    private lateinit var sharedPreferences: SharedPreferences
//    private val playbackDataQueue = mutableListOf<List<PlayBackData>>()
    private var isMapReady = false
//    private var playbackDataListener: PlaybackDataListener? = null
//    private lateinit var handler: Handler
//    private var isFetchingData = false
//    private val intervalMillis = 45000L // 1 detik
//    private var playbackDataList: List<PlayBackData> = listOf()

    // ViewModel instance
    private lateinit var playbackViewModel: PlaybackViewModel

    // untuk simulasi playback
    private var playbackHandler: Handler? = null
    private var playbackRunnable: Runnable? = null
    private var playbackInterval: Long = 1000L // Interval default 1 detik
    private var playbackSpeedMultiplier: Float = 1.0f // Faktor kecepatan default

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val latitude = -6.9135609 // Lattitude WMO
        val longitude = 112.5814275 // Longtitude WMO
        val mapAwal = LatLng(latitude,longitude) // WMO
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapAwal, 8.5f))
        isMapReady = true
        makePOICall()
        processMarkerQueue()
        processPlaybackQueue()
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
        // Observe playback data from ViewModel
        playbackViewModel = ViewModelProvider(requireActivity())[PlaybackViewModel::class.java]
        playbackViewModel.playbackData.observe(viewLifecycleOwner) { playbackData ->
            playbackData?.let {
                Log.d("PlaybackMapsFragment", "Playback data received: $it")
                displayPlaybackDataOnMap(it)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        mmsi = sharedPreferences.getString("MMSI", mmsi)
        fromDate = sharedPreferences.getString("FROM_DATE", fromDate)
        toDate = sharedPreferences.getString("FROM_DATE", toDate)
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun setCustomMarkerforPlayback(
        location: LatLng,
        name: String,
        type: Int,
        typeName: String,
        lat: Double,
        lon: Double,
        stamp: String,
        heading: Int,
        customMarkerType: Int
    ) {
        if (isMapReady) {
            val markerOptions = MarkerOptions()
                .position(location)
                .title(name)

            val iconResource =
                if (customMarkerType == 1) R.drawable.custom_marker_icon
                else R.drawable.poi_marker

            val iconBitmap = BitmapFactory.decodeResource(resources, iconResource)

            // Check if iconBitmap is null
            if (iconBitmap == null) {
                Log.e("PBVTSCallAPI", "API call gagal: iconBitmap must not be null")
                // Use pb_checkpoint_icon as default icon if the custom icon is not found
                val defaultIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.pb_checkpoint_icon)
                if (defaultIconBitmap != null) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(defaultIconBitmap))
                } else {
                    Log.e("PBVTSCallAPI", "Failed to load default icon: pb_checkpoint_icon")
                }
            } else {
                // Rotate the custom icon according to the heading
                val rotatedBitmap = rotateBitmap(iconBitmap, heading)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap))
            }

            val customInfoMarker =
                if (customMarkerType == 1) com.example.coordinateproject.customMarker.CustomInfoPlayback(requireContext(), lat, lon, stamp, heading)
                else com.example.coordinateproject.customMarker.CustomInfoPOI(requireContext(), name, type, typeName)

            mMap.setInfoWindowAdapter(customInfoMarker)
            val marker = mMap.addMarker(markerOptions)
            marker?.tag = customInfoMarker
            if (customMarkerType == 1) { // 1 is assumed to be the type for pb checkpoint
                polylinePoints.add(location)
                // Draw the polyline
                drawPolyline()
            }

        } else {
            markerDataQueue.add(MarkerData(location, name, type, typeName, lat, lon, stamp, heading, customMarkerType))
        }
    }

    private val polylinePoints = mutableListOf<LatLng>()

    private fun drawPolyline() {
        if (polylinePoints.size >= 2) { // At least two points are needed to draw a line
            val polylineOptions = PolylineOptions()
                .addAll(polylinePoints)
                .width(5f) // Set the width of the polyline (in pixels)
                .color(Color.GREEN) // Optional: set the color of the polyline
            mMap.addPolyline(polylineOptions)
        }
    }

    data class MarkerData(
        val location: LatLng,
        val name: String,
        val type: Int,
        val typeName: String,
        val lat: Double,
        val lon: Double,
        val stamp: String,
        val heading: Int,
        val customMarkerType: Int
    )

    private val markerDataQueue = mutableListOf<MarkerData>()

    private fun processMarkerQueue() {
        for (data in markerDataQueue) {
            setCustomMarkerforPlayback(
                data.location, data.name, data.type, data.typeName,
                data.lat, data.lon, data.stamp, data.heading, data.customMarkerType
            )
        }
        markerDataQueue.clear()
    }

    fun startPlayback(locations: List<LatLng>) {
        if (!isMapReady) {
            playbackQueue.add(locations)
            return
        }

        val marker = mMap.addMarker(MarkerOptions().position(locations[0]).title("Ship"))
        val handler = Handler(Looper.getMainLooper())
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                if (index < locations.size) {
                    marker?.position = locations[index]
                    marker?.position?.let { CameraUpdateFactory.newLatLng(it) }
                        ?.let { mMap.animateCamera(it) }
                    index++
                    handler.postDelayed(this, 1000) // Mengatur interval playback (1 detik)
                }
            }
        }
        handler.post(runnable)
    }

    private val playbackQueue = mutableListOf<List<LatLng>>()

    private fun processPlaybackQueue() {
        for (locations in playbackQueue) {
            startPlayback(locations)
        }
        playbackQueue.clear()
    }

    fun setPlaybackSpeed(multiplier: Float) {
        playbackSpeedMultiplier = multiplier
        playbackRunnable?.let { playbackHandler?.removeCallbacks(it) }
        playbackRunnable?.let { playbackHandler?.post(it) }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // Pengambilan data untuk Point of Interest
    private fun makePOICall() {
        val call = POI.POIRetrofit.apiService.getPOI(tokenAPI)
        val typeData = 2
        call.enqueue(object : Callback<POIData> {
            override fun onResponse(call: Call<POIData>, response: Response<POIData>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        // Handle the response data
                        val yourDataList = data.data

                        for (item in yourDataList) {
                            val name = item.name
                            val type = item.type
                            val lat = item.lat
                            val lon = item.lon
                            val typeName = item.type_name

                            // Create a LatLng object using the latitude and longitude
                            val location = LatLng(lat, lon)

                            // Marker ini khusus untuk mengetahui lokasi, nama kapal, dan arah kapal melaju menggunakan custom marker
//                            setCustomMarkerPOI(location, name, type, type_name)
                            setCustomMarkerforPlayback(location, name, type, typeName,  lat, lon, "", 0, typeData)
                        }
                    }
                } else {
                    // Handle unsuccessful response (e.g., non-2xx status codes)
                    val errorResponseCode = response.code() // HTTP status code
                    val errorMessage = response.errorBody()?.string()
                    Log.d("error", "$errorResponseCode : $errorMessage")
                }
            }
            override fun onFailure(call: Call<POIData>, t: Throwable) {
                errorNihServernya()
            }
        })
    }

    fun errorNihServernya(){
        val errorServer = ErrorServer()
        childFragmentManager.beginTransaction()
            .replace(R.id.map, errorServer)
            .addToBackStack(null) // Optional, adds the fragment to the back stack
            .commit()
    }

    private fun displayPlaybackDataOnMap(playbackData: List<PlayBackData>) {
        for (item in playbackData) {
            val location = LatLng(item.lat, item.lon)
            setCustomMarkerforPlayback(location, "", 0, "", item.lat, item.lon, item.stamp, item.hdg, 1)
        }
    }

}
