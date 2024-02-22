package com.example.coordinateproject

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
import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activity = activityRule.activity
    }

    @Test
    fun testSidebarButtonClick() {
        // Click the sidebar button
        val sidebarButton = activity.findViewById<ImageView>(R.id.sidebarButton)
        sidebarButton.performClick()

        // Assert that the sidebar fragment is visible
        val fragmentManager = activity.supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.fragmentSidebar)
        assertNotNull(fragment)
        assertTrue(fragment is ShipFragment)
    }

    @Test
    fun testAboutUsIntent() {
        // Click the other option button
        val otherOption = activity.findViewById<ImageView>(R.id.otherOption)
        otherOption.performClick()

        // Assert that the AboutUsActivity is started
        val aboutUsActivity = InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(activity.monitor, 5000)
        assertNotNull(aboutUsActivity)
    }

    @Test
    fun testSidebarAnimation() {
        // Click the sidebar button
        val sidebarButton = activity.findViewById<ImageView>(R.id.sidebarButton)
        sidebarButton.performClick()

        // Assert that the sidebar animation is correct
        val sidebarContainer = activity.findViewById<View>(R.id.fragmentSidebar)
        val anim = sidebarContainer.getAnimation() as TranslateAnimation
        assertEquals(anim.fromXDelta, -200f)
        assertEquals(anim.toXDelta, 0f)
    }
}