package com.example.aos.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.aos.model.GithubRepo
import com.example.aos.model.Owner
import com.example.aos.ui.components.HighlightedText
import com.example.aos.util.DateUtils
import com.example.aos.viewmodel.PopularReposViewModel
import com.example.aos.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Preview(showBackground = true)
@Composable
fun PopularReposScreenPreview() {
    PopularReposScreen({})
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PopularReposScreen(
    onRepoClick: (GithubRepo) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PopularReposViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasMoreItems by viewModel.hasMoreItems.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val expandDateFilter by viewModel.expandDateFilter.collectAsState()
    var isPulling by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isPulling,
        onRefresh = {
            isPulling = true
            viewModel.fetchPopularRepos(DateUtils.strToDate(selectedDate)) {
                isPulling = false
            }
        }
    )

    // Handle scroll to bottom
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastIndex ->
                if (lastIndex != null && lastIndex >= repos.size - 5 && hasMoreItems && !isLoading) {
                    viewModel.fetchPopularRepos(DateUtils.strToDate(selectedDate))
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Date Filter
            DateFilter(
                expand = expandDateFilter,
                selectedDate = selectedDate,
                onExpand = { viewModel.toggleDateFilter() },
                onDateSelected = { date ->
                    viewModel.setSelectedDate(date)
                    val fmtDate = DateUtils.strToDate(date)
                    viewModel.reset(fmtDate)
                    viewModel.toggleDateFilter()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        Button(onClick = { viewModel.reset() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
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

        PullRefreshIndicator(
            refreshing = isPulling,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RepoItemPreview() {
    val repo = GithubRepo(
        id = 1,
        name = "freeCodeCamp",
        description = "freeCodeCamp.org offers several free developer certifications. Each of these certifications involves building 5 required web app projects, along with hundreds of optional coding challenges to help you prepare for those projects. We estimate that each certification will take a beginner programmer around 300 hours to earn.\n",
        owner = Owner(
            login = "freeCodeCamp",
            avatarUrl = "https://avatars.githubusercontent.com/u/127165244?v=4"
        ),
        stars = 417943,
        language = "TypeScript",
        fullName = "freeCodeCamp/freeCodeCamp"
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
            }

            if (!repo.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HighlightedText(
                    text = repo.description,
                    keywords = keywords,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HighlightedText(
                    text = repo.language ?: "",
                    keywords = keywords,
                    modifier = Modifier
                )
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

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateFilterPreview() {
    Column {
        DateFilter(expand = true, "All",
            onExpand = { },
            onDateSelected = { date ->
                // pass
            })
        DateFilter(expand = false, "All",
            onExpand = { },
            onDateSelected = { date ->
                // pass
            })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DateFilter(
    expand: Boolean,
    selectedDate: String?,
    onExpand: () -> Unit,
    onDateSelected: (String?) -> Unit,
) {
    val dates = listOf("2 Weeks", "1 Month", "2 Months", "6 Months", "1 Year")

    Column {
        FilterChip(
            selected = false,
            onClick = { onExpand() },
            label = {
                Row {
                    Text(
                        text = selectedDate ?: "Filter by date",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (expand) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "ArrowUp",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "ArrowDown",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
        if (expand) {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedDate == null || selectedDate == "All",
                    onClick = { onDateSelected(null) },
                    label = { Text("All") }
                )
                dates.forEach { date ->
                    FilterChip(
                        selected = selectedDate == date,
                        onClick = { onDateSelected(date) },
                        label = { Text(date) }
                    )
                }
            }
        }
    }
}