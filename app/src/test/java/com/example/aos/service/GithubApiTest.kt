package com.example.aos.service

import com.example.aos.githubApiForTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GithubApiTest {

    private lateinit var api: GithubApi

    @Before
    fun setUp() {
        api = githubApiForTest()
    }

    @Test
    fun getRepo() = runTest {
        val repo = api.getRepo("410063005", "aOS")
        assertTrue(repo.owner.login == "410063005")
        assertTrue(repo.openIssues > 0)
    }

    @Test
    fun getUserProfile() = runTest {
        val userProfile = api.getUserProfile("410063005")
        assertTrue(userProfile.publicRepos > 0)
    }
}