package com.example.coordinateproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {
    private var isSidebarVisible = false
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment: Fragment = MapsViewFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

        val sidebarButton = findViewById<ImageView>(R.id.sidebarButton)
        sidebarButton.setOnClickListener {
            if (isSidebarVisible) {
                hideSidebarFragment()
            } else {
                showSidebarFragment()
            }
        }

        val otherOption = findViewById<ImageView>(R.id.otherOption)
        otherOption.setOnClickListener {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showSidebarFragment() {
        val fragment = ShipFragment()
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_from_left, // Animation for entering
            R.anim.slide_out_to_left,   // Animation for exiting
            R.anim.slide_in_from_left,
            R.anim.slide_out_to_left
        )
        transaction.replace(R.id.fragmentSidebar, fragment)
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
        val sidebarContainer = findViewById<View>(R.id.fragmentSidebar)

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