package com.example.aos.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aos.model.GithubRepo
import com.example.aos.model.UserProfile
import com.example.aos.ui.components.ProfileAvatar
import com.example.aos.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.aos.viewmodel.LoginViewModel
import com.example.aos.viewmodel.ViewModelFactory
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    onRepoClick: (GithubRepo) -> Unit,
    navController: NavController?,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    loginViewModel: LoginViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val profile by viewModel.profile.collectAsState()
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    val username by loginViewModel.username.collectAsState()
    val hasMoreItems by viewModel.hasMoreItems.collectAsState()
    var isPulling by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isPulling,
        onRefresh = {
            isPulling = true
            viewModel.loadProfile(username)
            isPulling = false
        }
    )

    // Handle scroll to bottom
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastIndex ->
                if (lastIndex != null && lastIndex >= repos.size - 5 && hasMoreItems && !isLoading) {
                    viewModel.loadMoreRepos()
                }
            }
    }
    
    // Load profile when the screen is first displayed
    LaunchedEffect(Unit) {
        if (isLoggedIn && profile == null) {
            viewModel.loadProfile(username)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = { loginViewModel.logout() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Logout")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            !isLoggedIn -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Please log in to view your profile and repos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { navController?.navigate("login") }
                        ) {
                            Text("Login")
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .pullRefresh(pullRefreshState)
                ) {
                    when {
                        error != null && repos.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = error ?: "An error occurred",
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.loadProfile(username) }) {
                                    Text("Retry")
                                }
                            }
                        }
                        profile != null -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    ProfileHeader(profile = profile!!, viewModel, loginViewModel, navController)
                                }
                                
                                item {
                                    ProfileStats(profile = profile!!)
                                }
                                
                                item {
                                    ProfileInfo(profile = profile!!)
                                }
                                
                                item {
                                    Text(
                                        text = "Repositories",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                
                                items(repos) { repo ->
                                    RepoItem(
                                        repo = repo,
                                        onClick = { onRepoClick(repo) }
                                    )
                                }

                                if (isLoading) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = isPulling,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    viewModel: ProfileViewModel,
    loginViewModel: LoginViewModel,
    navController: NavController?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAvatar(
            avatarUrl = profile.avatarUrl,
            name = profile.name ?: profile.login,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = profile.name ?: profile.login,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "@${profile.login}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = {
            viewModel.reset()
            loginViewModel.logout()
            navController?.navigate("login")
        }) {
            Text("Logout")
        }
    }
}

@Composable
private fun ProfileStats(profile: UserProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            icon = Icons.Default.Info,
            value = profile.publicRepos.toString(),
            label = "Repositories"
        )
        StatItem(
            icon = Icons.Default.Person,
            value = profile.followers.toString(),
            label = "Followers"
        )
        StatItem(
            icon = Icons.Default.Person,
            value = profile.following.toString(),
            label = "Following"
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileInfo(profile: UserProfile) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!profile.bio.isNullOrBlank()) {
            Text(
                text = profile.bio,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        if (!profile.location.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = profile.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (!profile.company.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = profile.company,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (!profile.blog.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO: Open blog URL */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = profile.blog,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 