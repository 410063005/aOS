package com.example.aos

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testMainActivityInitialState() {
        // Verify that the app launches successfully
        composeTestRule.onRoot().assertExists()
        
        // Verify that the navigation is initialized
        composeTestRule.onNodeWithText("aOS").assertExists()
    }

    @Test
    fun testNavigationComponents() {
        // Verify that the bottom navigation bar exists
        composeTestRule.onNodeWithText("Popular").assertExists()
        composeTestRule.onNodeWithText("Profile").assertExists()
        
        // Verify that the search button exists in the top bar
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
    }
} 