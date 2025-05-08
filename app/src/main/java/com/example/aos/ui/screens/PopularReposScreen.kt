package com.example.aos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aos.model.GithubRepo
import com.example.aos.model.Owner
import com.example.aos.ui.components.HighlightedText
import com.example.aos.viewmodel.PopularReposViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun PopularReposScreenPreview() {
    PopularReposScreen({})
}

@Composable
fun PopularReposScreen(
    onRepoClick: (GithubRepo) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PopularReposViewModel = viewModel()
) {
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasMoreItems by viewModel.hasMoreItems.collectAsState()
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Handle scroll to bottom
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastIndex ->
                if (lastIndex != null && lastIndex >= repos.size - 5 && hasMoreItems && !isLoading) {
                    viewModel.fetchPopularRepos()
                }
            }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            error != null && repos.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.reset() }) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun RepoItemPreview() {
    val repo = GithubRepo(
        id = 1,
        name = "test",
        description = null, //"test",
        owner = Owner(
            login = "test",
            avatarUrl = "test"
        ),
        stars = 1000,
        language = null, //"test",
        fullName = "cm"
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RepoItem(repo = repo, onClick = {})
        RepoItem(repo = repo, onClick = {})
        RepoItem(repo = repo, onClick = {})
        RepoItem(repo = repo, onClick = {})
        RepoItem(repo = repo, onClick = {})
    }
}

@Composable
fun RepoItem(
    repo: GithubRepo,
    keywords: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = repo.owner.avatarUrl,
                    contentDescription = "Owner avatar",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    HighlightedText(
                        text = repo.fullName ?: "",
                        keywords = keywords,
                        modifier = Modifier
                    )
                    HighlightedText(
                        text = repo.owner.login,
                        keywords = keywords,
                        modifier = Modifier
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = repo.stars.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            if (!repo.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HighlightedText(
                    text = repo.description,
                    keywords = keywords,
                    modifier = Modifier
                )
            }
            
            if (!repo.language.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HighlightedText(
                    text = repo.language,
                    keywords = keywords,
                    modifier = Modifier
                )
            }
        }
    }
} 