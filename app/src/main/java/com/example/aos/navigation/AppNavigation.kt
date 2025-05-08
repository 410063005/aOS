package com.example.aos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aos.ui.screens.HomeScreen
import com.example.aos.ui.screens.LoginScreen
import com.example.aos.ui.screens.RepoDetailScreen
import com.example.aos.ui.screens.SearchScreen
import com.example.aos.model.GithubRepo
import com.example.aos.model.Owner
import com.example.aos.ui.screens.RaiseIssueScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search")
    object RepoDetails : Screen("repo_details/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "repo_details/$owner/${repo}"
    }
    object RaiseIssue : Screen("raise_issue/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "raise_issue/$owner/${repo}"
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onRepoClick = { repo ->
                    navController.navigate(Screen.RepoDetails.createRoute(repo.owner.login, repo.name))
                },
                navController = navController
            )
        }
        composable(
            route = Screen.RepoDetails.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: return@composable
            val repo = backStackEntry.arguments?.getString("repo") ?: return@composable
            RepoDetailScreen(
                owner = owner,
                repo = repo,
                navController = navController
            )
        }
        composable(
            route = Screen.RaiseIssue.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: return@composable
            val repo = backStackEntry.arguments?.getString("repo") ?: return@composable
            RaiseIssueScreen(
                owner = owner,
                repo = repo,
                navController = navController
            )
        }
    }
} 