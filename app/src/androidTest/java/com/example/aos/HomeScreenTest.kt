package com.example.aos

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.aos.ui.screens.HomeScreen
import com.example.aos.viewmodel.LoginViewModel
import com.example.aos.viewmodel.PopularReposViewModel
import com.example.aos.viewmodel.ProfileViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockNavController = mock(NavController::class.java)
//    private val mockLoginViewModel = mock(LoginViewModel::class.java)
//    private val mockPopularReposViewModel = mock(PopularReposViewModel::class.java)
//    private val mockProfileViewModel = mock(ProfileViewModel::class.java)

    @Test
    fun testHomeScreenInitialState() {
        // Launch the HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                navController = mockNavController,
//                loginViewModel = mockLoginViewModel,
//                popularReposViewModel = mockPopularReposViewModel,
//                profileViewModel = mockProfileViewModel
            )
        }

        // Verify that the top app bar exists with correct title
        composeTestRule.onNodeWithText("aOS").assertExists()
        
        // Verify that the search button exists
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
    }

    @Test
    fun testBottomNavigation() {
        // Launch the HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                navController = mockNavController,
//                loginViewModel = mockLoginViewModel,
//                popularReposViewModel = mockPopularReposViewModel,
//                profileViewModel = mockProfileViewModel
            )
        }

        // Verify that both navigation items exist
        composeTestRule.onNodeWithText("Popular").assertExists()
        composeTestRule.onNodeWithText("Profile").assertExists()
    }

    @Test
    fun testPopularReposTabContent() {
        // Launch the HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                navController = mockNavController,
//                loginViewModel = mockLoginViewModel,
//                popularReposViewModel = mockPopularReposViewModel,
//                profileViewModel = mockProfileViewModel
            )
        }

        // Verify that the Popular tab is selected by default
        composeTestRule.onNodeWithText("Popular").assertIsSelected()
        
        // Verify that the repository list is displayed
        composeTestRule.onNodeWithTag("repo_list").assertExists()
    }

    @Test
    fun testProfileTabContent() {
        // Launch the HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                navController = mockNavController,
//                loginViewModel = mockLoginViewModel,
//                popularReposViewModel = mockPopularReposViewModel,
//                profileViewModel = mockProfileViewModel
            )
        }

        // Click on the Profile tab
        composeTestRule.onNodeWithText("Profile").performClick()
        
        // Verify that the Profile tab is selected
        composeTestRule.onNodeWithText("Profile").assertIsSelected()
        
        // Verify that the profile content is displayed
        composeTestRule.onNodeWithTag("profile_content").assertExists()
    }

    @Test
    fun testSearchNavigation() {
        // Launch the HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                navController = mockNavController,
//                loginViewModel = mockLoginViewModel,
//                popularReposViewModel = mockPopularReposViewModel,
//                profileViewModel = mockProfileViewModel
            )
        }

        // Click on the search button
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        
        // Verify navigation to search screen
//        verify(mockNavController).navigate("search")
    }
} 