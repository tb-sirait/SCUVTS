package com.example.coordinateproject.response

data class ApiResponse(
    val status: String,
    val message: String,
    val `data`: List<Data>
)