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
import com.example.coordinateproject.response.ApiResponse
import com.example.coordinateproject.responseBypass.WMOShip
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            // mengganti tampilan map menjadi mode tampilan satelit
        }
        makeWMOCall()
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
        makeWMOCall() // run the process of taking all marker (based on position from data) and apply marker to map
        // Implement other LocationListener methods as needed
        refreshHandler = Handler()
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL)

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
        val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.other_vessel)

        // Rotate the custom icon according to the heading
        val rotatedBitmap = rotateBitmap(iconBitmap, heading)

        // Set the custom rotated icon for the marker
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap))

        val customInfoMarker = CustomInfoArea(
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

    private fun setPOICustomMarker(lat: Double, lon: Double, name: String, type: Int, type_name: String) {
        val location = LatLng(lat, lon)
        val markerOptions = MarkerOptions()
            .position(location)
            .title(name)

        // Load custom marker icon from drawable
        val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.custom_marker_icon)

        // Set the custom icon for the marker
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))

        val customInfoPOI = CustomInfoPOI(
            requireContext(),
            name,
            type,
            type_name
        )

        mMap.setInfoWindowAdapter(customInfoPOI)
        val marker = mMap.addMarker(markerOptions)
        marker?.tag = customInfoPOI
    }

    // Main Process of Managing data from API to Map Visualization
    private fun makeWMOCall() {
        val call = WMOShip.RetrofitClient.apiService.getData("73ob73y64nt3n653k4l1")
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
                    Log.d("error", "$errorResponseCode : $errorMessage")
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
            makeWMOCall()
            refreshHandler.postDelayed(this, REFRESH_INTERVAL)
        }
    }

    // to pause activity on application and callback data from API
    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    // Make custom info marker (POI) to showing vessel data
    inner class CustomInfoPOI(private val context: Context,
                                 private val name: String?,
                                 private val type: Int,
                                 private val type_name: String) : GoogleMap.InfoWindowAdapter {
        @SuppressLint("InflateParams", "SetTextI18n", "MissingInflatedId")
        override fun getInfoContents(marker: Marker): View? {
            val infoView = LayoutInflater.from(context).inflate(R.layout.fragment_poi_detail, null)

            // Temukan view yang ada di layout kustom
            val nameTextView = infoView.findViewById<TextView>(R.id.poi_name)
            val typeTextView = infoView.findViewById<TextView>(R.id.type)
            val typeNameTextView = infoView.findViewById<TextView>(R.id.type_name)

            nameTextView.text = "$name"
            typeTextView.text = "Type = $type"
            typeNameTextView.text = "Type Name = $type_name"

            return infoView
        }

        override fun getInfoWindow(marker: Marker): View? {
            val customInfoPOI = marker.tag as? CustomInfoPOI
            return customInfoPOI?.getInfoContents(marker)
        }

        // Make custom info marker to showing vessel data
        inner class CustomInfoMarker(private val context: Context,
                                     private val imo: String,
                                     private val mmsi: String,
                                     private val calcspeed: Double,
                                     private val name: String?,
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

    // Make custom info marker to showing All vessel data
    inner class CustomInfoArea(private val context: Context,
                               private val imo: String,
                               private val mmsi: String,
                               private val calcspeed: Int,
                               private val name: String?,
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
            val customInfoMarker = marker.tag as? MapsViewFragment.CustomInfoArea
            return customInfoMarker?.getInfoContents(marker)
        }

        // Make custom info marker to showing All vessel data
        inner class CustomInfoArea(
            private val context: Context,
            private val imo: String,
            private val mmsi: String,
            private val calcspeed: Double,
            private val name: String?,
            private val date: String
        ) : GoogleMap.InfoWindowAdapter {

            @SuppressLint("InflateParams", "SetTextI18n")
            override fun getInfoContents(marker: Marker): View? {
                val infoView =
                    LayoutInflater.from(context).inflate(R.layout.fragment_ship_detail, null)

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
                val customInfoMarker = marker.tag as? MapsViewFragment.CustomInfoArea
                return customInfoMarker?.getInfoContents(marker)
            }


        }


    }







}