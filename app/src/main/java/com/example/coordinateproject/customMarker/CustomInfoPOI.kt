package com.example.coordinateproject.customMarker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.coordinateproject.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoPOI (private val context: Context,
                     private val name: String?,
                     private val type: Int,
                     private val type_name: String) : GoogleMap.InfoWindowAdapter {
    @SuppressLint("InflateParams", "SetTextI18n", "MissingInflatedId")
    override fun getInfoContents(marker: Marker): View? {
        val infoViewPOI = LayoutInflater.from(context).inflate(R.layout.fragment_poi_detail, null)

        // Temukan view yang ada di layout kustom
        val nameTextView = infoViewPOI.findViewById<TextView>(R.id.poi_name)
        val typeTextView = infoViewPOI.findViewById<TextView>(R.id.type)
        val typeNameTextView = infoViewPOI.findViewById<TextView>(R.id.type_name)

        nameTextView.text = "$name"
        typeTextView.text = "Type = $type"
        typeNameTextView.text = "Type Name = $type_name"

        return infoViewPOI
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}