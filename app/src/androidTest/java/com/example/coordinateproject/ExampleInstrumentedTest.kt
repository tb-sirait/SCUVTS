package com.example.coordinateproject

import android.R
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
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
        val sidebarButton = activity.findViewById<ImageView>(com.example.coordinateproject.R.id.sidebarButton)
        sidebarButton.performClick()

        // Assert that the sidebar fragment is visible
        val fragmentManager = activity.supportFragmentManager
        val fragment = fragmentManager.findFragmentById(com.example.coordinateproject.R.id.fragmentSidebar)
        assertNotNull(fragment)
        assertTrue(fragment is ShipFragment)
    }

    @Test
    fun testAboutUsIntent() {
        // Click the other option button
        val otherOption = activity.findViewById<ImageView>(com.example.coordinateproject.R.id.sidebarButton)
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