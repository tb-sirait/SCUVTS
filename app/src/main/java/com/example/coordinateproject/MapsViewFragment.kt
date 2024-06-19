package com.example.coordinateproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.coordinateproject.customMarker.CustomInfoArea
import com.example.coordinateproject.customMarker.CustomInfoPOI
import com.example.coordinateproject.customMarker.CustomInfoWMO
import com.example.coordinateproject.response.ApiResponse
import com.example.coordinateproject.response.POIData
import com.example.coordinateproject.response.wmoarea
import com.example.coordinateproject.responseBypass.POI
import com.example.coordinateproject.responseBypass.WMOArea
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

@Suppress("DEPRECATION")
class MapsViewFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val REFRESH_INTERVAL: Long = 30 * 1000 // 30 seconds in milliseconds
    private lateinit var refreshHandler: Handler
    private var tokenAPI: String = "73ob73y64nt3n653k4l1"
    var mmsiPlayback: String? = null // WMO to Playback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.getMapAsync { googleMap ->
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }

    //fungsi dari tampilan map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap // initialize Map Library to using marker and markerOption
        val latitude = -6.9135609 // Lattitude WMO
        val longitude = 112.5814275 // Longtitude WMO
        val mapAwal = LatLng(latitude,longitude) // WMO
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapAwal, 8.5f))
        makeWMOCall()
        makePOICall()
        makeAreaCall()
        // Implement other LocationListener methods as needed
        refreshHandler = Handler()
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL)


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
        mmsiPlayback = mmsi

        val markerOptions = MarkerOptions()
            .position(location)

        // Load custom marker icon from drawable based on customMarkerType
        val iconResource =
            if (customMarkerType == 2) R.drawable.other_vessel
            else if (customMarkerType == 1) R.drawable.custom_marker_icon
            else R.drawable.poi_marker

        val iconBitmap = BitmapFactory.decodeResource(resources, iconResource)

        // Rotate the custom icon according to the heading
        val rotatedBitmap = rotateBitmap(iconBitmap, heading)

        // Set the custom rotated icon for the marker
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap))

        val customInfoMarker =
            if (customMarkerType == 1) CustomInfoWMO(requireContext(), imo, mmsi, calcspeed.toInt(), name, date)
            else if (customMarkerType == 2) CustomInfoArea(requireContext(), imo, mmsi, calcspeed, name, date)
            else CustomInfoPOI(requireContext(), name, type, type_name)

        mMap.setInfoWindowAdapter(customInfoMarker)
        mMap.setOnInfoWindowClickListener(null)
        val marker = mMap.addMarker(markerOptions)
        marker?.tag = customInfoMarker
    }

    // Pengambilan data kapal untuk semua kapal WMO
    private fun makeAreaCall() {
        val call = WMOArea.WMOAREARetrofit.apiService.getAllDataKapal(tokenAPI)
        val typeData = 2
        call.enqueue(object : Callback<wmoarea> {
            override fun onResponse(call: Call<wmoarea>, response: Response<wmoarea>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        // Handle the response data
                        val yourDataList = data.data

                        for (item in yourDataList) {
                            val name = item.name
                            val date = item.date
                            val lat = item.lat
                            val lon = item.lon
                            val heading = item.heading.toFloat()
                            val calcspeed = item.calcspeed
                            val imo = item.IMO
                            val mmsi = item.MMSI

                            // Create a LatLng object using the latitude and longitude
                            val location = LatLng(lat, lon)

//                            Log.d("data", "nama: $name")

                            // Marker ini khusus untuk mengetahui lokasi, nama kapal, dan arah kapal melaju menggunakan custom marker
//                            setCustomMarkerArea(location, name, heading, calcspeed, date, imo, mmsi)
                            setCustomMarker(location, name, heading, calcspeed, date, imo, mmsi, 0, "", typeData)
                        }
                    }
                } else {
                    // Handle unsuccessful response (e.g., non-2xx status codes)
                    val errorResponseCode = response.code() // HTTP status code
                    val errorMessage = response.errorBody()?.string()
                    Log.d("error", "$errorResponseCode : $errorMessage")
                }
            }

            override fun onFailure(call: Call<wmoarea>, t: Throwable) {
                errorNihServernya()
            }
        })
    }

    // Pengambilan data untuk kapal OSES
    private fun makeWMOCall() {
        val call = WMOShip.RetrofitClient.apiService.getData(tokenAPI)
        val typeData = 1
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        // Handle the response data
                        val yourDataList = data.data

                        for (item in yourDataList) {
                            val name = item.name
                            val date = item.date
                            val lat = item.lat
                            val lon = item.lon
                            val heading = item.heading.toFloat()
                            val calcspeed = item.calcspeed
                            val imo = item.IMO
                            val mmsi = item.MMSI

                            val location = LatLng(lat,lon)

                            // Marker ini khusus untuk mengetahui lokasi, nama kapal, dan arah kapal melaju menggunakan custom marker
                            setCustomMarker(location, name, heading, calcspeed.toDouble(), date, imo, mmsi, 0, "", typeData)
                        }
                    }
                } else {
                    // Handle unsuccessful response (e.g., non-2xx status codes)
                    val errorResponseCode = response.code() // HTTP status code
                    val errorMessage = response.errorBody()?.string()
                    Log.d("error", "$errorResponseCode : $errorMessage")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                errorNihServernya()
            }
        })
    }

    // Pengambilan data untuk Point of Interest
    private fun makePOICall() {
        val call = POI.POIRetrofit.apiService.getPOI(tokenAPI)
        val typeData = 3
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
                            setCustomMarker(location, name, 0.0f, 0.0,  "", "", "", type, typeName, typeData)
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

    // Apply Refresh Runnable to call data from API automatically according to the specified time
    private val refreshRunnable = object : Runnable {
        override fun run() {
            mMap.clear()
            makeWMOCall()
            makePOICall()
            makeAreaCall()
            refreshHandler.postDelayed(this, REFRESH_INTERVAL)
        }
    }

    // to pause activity on application and callback data from API
    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    // Rotasi Bitmap menyesuaikan arah haluan kapal
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun errorNihServernya(){
        val errorServer = ErrorServer()
        childFragmentManager.beginTransaction()
            .replace(R.id.map, errorServer)
            .addToBackStack(null) // Optional, adds the fragment to the back stack
            .commit()
    }
}