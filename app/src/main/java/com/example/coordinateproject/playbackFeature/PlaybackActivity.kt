package com.example.coordinateproject.playbackFeature

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.coordinateproject.R


class PlaybackActivity : AppCompatActivity() {

    private var isSidebarVisible = false
    @SuppressLint("WrongViewCast")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

//        val mmsi = intent.getStringExtra("mmsi")
//
//        if (!mmsi.isNullOrEmpty()) {
//            // Lakukan sesuatu dengan mmsi
//            println("MMSI: $mmsi")
//        } else {
//            // Tangani kasus mmsi null atau kosong
//            println("MMSI is null or empty")
//        }

        val fragmentMapsPlayback: Fragment = PlaybackMapsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mapsPlayback, fragmentMapsPlayback)
            .commit()

        val sidebarPlaybackButton = findViewById<ImageView>(R.id.sidebarPlaybackButton)
        sidebarPlaybackButton.setOnClickListener {
            if (isSidebarVisible) {
                hideSidebarFragment()
            } else {
                showSidebarFragment()
            }
        }
    }

    private fun showSidebarFragment() {
        val fragment = SidebarPlaybackFragment()
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_from_left, // Animation for entering
            R.anim.slide_out_to_left,   // Animation for exiting
            R.anim.slide_in_from_left,
            R.anim.slide_out_to_left
        )
        transaction.replace(R.id.sidebarPlayback, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

        // Slide in the sidebar
        animateSidebar(true)
        isSidebarVisible = true
    }

    private fun hideSidebarFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.popBackStack()
        isSidebarVisible = false
    }

    private fun animateSidebar(show: Boolean) {
        val sidebarContainer = findViewById<View>(R.id.sidebarPlayback)

        // translation sidebar anim
        val translationXStart = if (show) -200f else 0f
        val translationXEnd = if (show) 0f else -200f

        // Initialize Translate Anim
        val anim = TranslateAnimation(
            TranslateAnimation.ABSOLUTE, translationXStart,
            TranslateAnimation.ABSOLUTE, translationXEnd,
            TranslateAnimation.ABSOLUTE, 0f,
            TranslateAnimation.ABSOLUTE, 0f
        )
        anim.duration = 100 // Set the animation duration in milliseconds
        anim.fillAfter = true // Keep the final position after animation

        // Start the animation
        sidebarContainer.startAnimation(anim)
    }
}