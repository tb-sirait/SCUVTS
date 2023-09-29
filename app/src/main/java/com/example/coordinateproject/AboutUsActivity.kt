package com.example.coordinateproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }

    fun onImageViewClick(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}