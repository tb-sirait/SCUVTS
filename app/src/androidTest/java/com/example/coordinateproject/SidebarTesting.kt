package com.example.coordinateproject

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test

class SidebarTesting {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testShowAndHideSidebar() {
        // Click the sidebar button to show sidebar
        onView(withId(R.id.sidebarButton)).perform(click())

        // Check if sidebar is displayed
        onView(withId(R.id.fragmentSidebar)).check(matches(isDisplayed()))

        // Click the sidebar button again to hide sidebar
        onView(withId(R.id.sidebarButton)).perform(click())

        // Check if sidebar is hidden
        onView(withId(R.id.fragmentSidebar)).check(matches(isNotDisplayed() as Matcher<in View>?))
    }

    private fun isNotDisplayed(): Any {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(view: View): Boolean {
                return !view.isShown
            }

            override fun describeTo(description: Description) {
                description.appendText("is not displayed on the screen")
            }
        }
    }
}