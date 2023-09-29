package com.example.coordinateproject

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.coordinateproject.response.APIService
import com.example.coordinateproject.response.ApiResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapsViewFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val REFRESH_INTERVAL: Long = 30 * 1000 // 30 seconds in milliseconds
    private lateinit var refreshHandler: Handler

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
        mapFragment.getMapAsync(this) // inisiasi sinkronisasi dari tampilan fragment map
        mapFragment.getMapAsync { googleMap ->
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            // mengganti tampilan map menjadi mode tampilan satelit
        }
        makeApiCall()
        // Implement other LocationListener methods as needed
        refreshHandler = Handler()
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL)
    }

    //fungsi dari tampilan map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap // initialize Map Library to using marker and markerOption
        val latitude = -4.200000 // latitude Indonesia
        val longitude = 106.816666 // Longtitude Indonesia
        val mapAwal = LatLng(latitude,longitude) // direct map view to Indonesia
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapAwal, 2f))
        makeApiCall() // run the process of taking all marker (based on position from data) and apply marker to map
        // Implement other LocationListener methods as needed
        refreshHandler = Handler()
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL)

    }

    // Take all data with passing from API using Bearer Token by Retrofit Library
    object RetrofitClient {
        private const val BASE_URL = "https://api.scu.co.id/vtms/wmo/"
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

    // Apply Heading marker on Vessel heading direction
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // Apply Marker and Marker Option on Map
    private fun setCustomMarkerIcon(location: LatLng, name: String, heading: Float, calcspeed: Int, date:String, mmsi: String, imo: String) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(name)

        // Load custom marker icon from drawable
        val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.custom_marker_icon)

        // Rotate the custom icon according to the heading
        val rotatedBitmap = rotateBitmap(iconBitmap, heading)

        // Set the custom rotated icon for the marker
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap))

        val customInfoMarker = CustomInfoMarker(
            requireContext(),
            imo,
            mmsi,
            calcspeed,
            name,
            date
        )
        mMap.setInfoWindowAdapter(customInfoMarker)
        val marker = mMap.addMarker(markerOptions)
        marker?.tag = customInfoMarker

    }

    // Main Process of Managing data from API to Map Visualization
    private fun makeApiCall() {
        val call = RetrofitClient.apiService.getData("73ob73y64nt3n653k4l1")
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

                            Log.d("API Response", "Data: $data")
                            // Create a LatLng object using the latitude and longitude
                            val location = LatLng(lat, lon)

                            // Marker ini khusus untuk mengetahui lokasi, nama kapal, dan arah kapal melaju menggunakan custom marker
                            setCustomMarkerIcon(location, name, heading, calcspeed, date, imo, mmsi)
                        }
                    }
                } else {
                    // Handle unsuccessful response (e.g., non-2xx status codes)
                    val errorResponseCode = response.code() // HTTP status code
                    val errorMessage = response.errorBody()?.string()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Handle failure
                // This method is called when the API call fails, for example, due to a network issue.
                val errorServer = ErrorServer()
                childFragmentManager.beginTransaction()
                    .replace(R.id.map, errorServer)
                    .addToBackStack(null) // Optional, adds the fragment to the back stack
                    .commit()
            }
        })
    }

    // Apply Refresh Runnable to call data from API automatically according to the specified time
    private val refreshRunnable = object : Runnable {
        override fun run() {
            mMap.clear()
            makeApiCall()
            refreshHandler.postDelayed(this, REFRESH_INTERVAL)
        }
    }

    // to pause activity on application and callback data from API
    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    // Make custom info marker to showing vessel data
    inner class CustomInfoMarker(private val context: Context,
                                 private val imo: String,
                                 private val mmsi: String,
                                 private val calcspeed: Int,
                                 private val name: String,
                                 private val date: String) : GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams", "SetTextI18n")
        override fun getInfoContents(marker: Marker): View? {
            val infoView = LayoutInflater.from(context).inflate(R.layout.fragment_ship_detail, null)

            // Temukan view yang ada di layout kustom
            val imoTextView = infoView.findViewById<TextView>(R.id.imo)
            val mmsiTextView = infoView.findViewById<TextView>(R.id.mmsi)
            val calcspeedTextView = infoView.findViewById<TextView>(R.id.cos)
            val nameTextView = infoView.findViewById<TextView>(R.id.ship_name)
            val dateTextView = infoView.findViewById<TextView>(R.id.ship_date)

            imoTextView.text = "IMO = $imo"
            mmsiTextView.text = "MMSI = $mmsi"
            calcspeedTextView.text = "CoS = $calcspeed KTS"
            nameTextView.text = "Name = $name"
            dateTextView.text = "Date = $date"

            return infoView
        }

        override fun getInfoWindow(marker: Marker): View? {
            val customInfoMarker = marker.tag as? CustomInfoMarker
            return customInfoMarker?.getInfoContents(marker)
        }


    }

}