package com.example.coordinateproject.customMarker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.coordinateproject.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoPlayback(private val context: Context,
                         private val lat: Double,
                         private val lon: Double,
                         private val stamp: String,
                         private val hdg: Int
) : GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams", "SetTextI18n")
        override fun getInfoContents(marker: Marker): View? {
            val infoViewWMO = LayoutInflater.from(context).inflate(R.layout.fragment_playback_detail, null)

            // Temukan view yang ada di layout kustom
            val latlonTextView = infoViewWMO.findViewById<TextView>(R.id.latlon)
            val stampTextView = infoViewWMO.findViewById<TextView>(R.id.stamp)
            val hdgTextView = infoViewWMO.findViewById<TextView>(R.id.heading)

            latlonTextView.text = "$lat | $lon"
            stampTextView.text = "Time Stamp = $stamp"
            hdgTextView.text = "Heading = $hdg"

            return infoViewWMO
        }

    override fun getInfoWindow(marker: Marker): View? {
        return when (val customInfoMarker = marker.tag) {
            is CustomInfoPlayback -> customInfoMarker.getInfoContents(marker)
            is CustomInfoPOI -> customInfoMarker.getInfoContents(marker)
            else -> null // Handle other types of CustomInfoMarker or return null if necessary
        }
    }
}