package com.example.coordinateproject.customMarker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.coordinateproject.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWMO(private val context: Context,
    private val imo: String,
    private val mmsi: String,
    private val calcspeed: Int,
    private val name: String?,
    private val date: String) : GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams", "SetTextI18n")
        override fun getInfoContents(marker: Marker): View? {
            val infoViewWMO = LayoutInflater.from(context).inflate(R.layout.fragment_ship_detail_wmo_modified, null)

            // Temukan view yang ada di layout kustom
            val imoTextView = infoViewWMO.findViewById<TextView>(R.id.imo)
            val mmsiTextView = infoViewWMO.findViewById<TextView>(R.id.mmsi)
            val calcspeedTextView = infoViewWMO.findViewById<TextView>(R.id.cos)
            val nameTextView = infoViewWMO.findViewById<TextView>(R.id.ship_name)
            val dateTextView = infoViewWMO.findViewById<TextView>(R.id.ship_date)

            imoTextView.text = "IMO = $imo"
            mmsiTextView.text = "MMSI = $mmsi"
            calcspeedTextView.text = "CoS = $calcspeed KTS"
            nameTextView.text = "Name = $name"
            dateTextView.text = "Date = $date"

            return infoViewWMO
        }

    override fun getInfoWindow(marker: Marker): View? {
        return when (val customInfoMarker = marker.tag) {
            is CustomInfoArea -> customInfoMarker.getInfoContents(marker)
            is CustomInfoWMO -> customInfoMarker.getInfoContents(marker)
            is CustomInfoPOI -> customInfoMarker.getInfoContents(marker)
            else -> null // Handle other types of CustomInfoMarker or return null if necessary
        }
    }
}