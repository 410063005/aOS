package com.example.aos.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.aos.navigation.Screen

sealed class Tab(val route: String, val icon: @Composable () -> Unit, val label: String) {
    object PopularRepos : Tab("popular_repos", { Icon(Icons.Default.Home, contentDescription = null) }, "Popular")
    object Profile : Tab("profile", { Icon(Icons.Default.Person, contentDescription = null) }, "Profile")
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf<Tab>(Tab.PopularRepos) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == Tab.PopularRepos,
                    onClick = { selectedTab = Tab.PopularRepos },
                    icon = Tab.PopularRepos.icon,
                    label = { Text(Tab.PopularRepos.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == Tab.Profile,
                    onClick = { selectedTab = Tab.Profile },
                    icon = Tab.Profile.icon,
                    label = { Text(Tab.Profile.label) }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            Tab.PopularRepos -> {
                PopularReposScreen(
                    navController = navController,
                    onRepoClick = { repo ->
                        /* Handle repo click */
                        navController?.navigate(Screen.RepoDetails.createRoute(repo.owner.login, repo.name))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            Tab.Profile -> {
                ProfileScreen(
                    onRepoClick = { repo ->
                        navController?.navigate(Screen.RepoDetails.createRoute(repo.owner.login, repo.name))
                    },
//                    modifier = Modifier.padding(paddingValues)
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
                )
            }
        }
    }
} 