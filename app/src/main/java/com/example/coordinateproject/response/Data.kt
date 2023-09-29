package com.example.coordinateproject.response

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val AE1: String,
    val AE2: String,
    val AE3: String,
    val IMO: String,
    val MMSI: String,
    val RPM_GB1: String,
    val RPM_GB2: String,
    val RPM_GB3: String,
    val RPM_ME1: String,
    val RPM_ME2: String,
    val RPM_ME3: String,
    val boxsensor: String,
    val calcspeed: Int,
    val date: String,
    val distance: Int,
    val fuel_cons: String,
    val fuel_cons2: String,
    val heading: Int,
    val ior: Int,
    val ips: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val port: String,
    val speed: Int,
    val speed_class_t: String,
    val txid: String,
    val vessel_id: String
)
