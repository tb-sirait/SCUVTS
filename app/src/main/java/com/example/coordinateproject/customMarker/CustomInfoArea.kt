package com.example.coordinateproject.customMarker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.coordinateproject.MapsViewFragment
import com.example.coordinateproject.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoArea(private val context: Context,
                     private val imo: String,
                     private val mmsi: String,
                     private val calcspeed: Double,
                     private val name: String?,
                     private val date: String) : GoogleMap.InfoWindowAdapter {

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun getInfoContents(marker: Marker): View? {
        val infoViewShipArea = LayoutInflater.from(context).inflate(R.layout.fragment_ship_area_detail, null)

        // Temukan view yang ada di layout kustom
        val imoTextView = infoViewShipArea.findViewById<TextView>(R.id.imo)
        val mmsiTextView = infoViewShipArea.findViewById<TextView>(R.id.mmsi)
        val calcspeedTextView = infoViewShipArea.findViewById<TextView>(R.id.cos)
        val nameTextView = infoViewShipArea.findViewById<TextView>(R.id.ship_name)
        val dateTextView = infoViewShipArea.findViewById<TextView>(R.id.ship_date)

        imoTextView.text = "IMO = $imo"
        mmsiTextView.text = "MMSI = $mmsi"
        calcspeedTextView.text = "CoS = $calcspeed KTS"
        nameTextView.text = "Name = $name"
        dateTextView.text = "Date = $date"

        return infoViewShipArea
    }

    override fun getInfoWindow(marker: Marker): View? {
        val customInfoMarker = marker.tag as? CustomInfoArea
        return customInfoMarker?.getInfoContents(marker)
    }
}