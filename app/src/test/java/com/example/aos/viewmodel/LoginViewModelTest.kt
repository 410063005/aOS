package com.example.aos.viewmodel

import android.app.Application
import com.example.aos.data.UserPreferences
import com.example.aos.service.GithubApiFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @Mock
    private lateinit var mockContext: Application
    @Mock
    private lateinit var userPreferences: UserPreferences

    @Test
    fun login() = runTest {
        val api = GithubApiFactory.githubApiForTest("token")

        val loginViewModel = LoginViewModel(api, userPreferences)

        loginViewModel.login("token")
        assertTrue(loginViewModel.uiState.value::class.java == LoginUiState.Error::class.java)
    }

    @Test
    fun logout() {
    }
}