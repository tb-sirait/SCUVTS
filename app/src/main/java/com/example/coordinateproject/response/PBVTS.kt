package com.example.coordinateproject.response

data class PBVTS(
    val status: String,
    val message: String,
    val `data`: List<PlayBackData>
)