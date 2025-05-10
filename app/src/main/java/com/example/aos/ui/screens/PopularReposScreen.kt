package com.example.aos.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aos.model.GithubRepo
import com.example.aos.model.Owner
import com.example.aos.ui.components.HighlightedText
import com.example.aos.viewmodel.PopularReposViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aos.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState

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
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var isPulling by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isPulling, //isLoading && repos.isEmpty(),
        onRefresh = {
            isPulling = true
            viewModel.fetchPopularRepos(strToDate(selectedDate)) {
                isPulling = false
            }
        }
    )

    // Handle scroll to bottom
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastIndex ->
                if (lastIndex != null && lastIndex >= repos.size - 5 && hasMoreItems && !isLoading) {
                    viewModel.fetchPopularRepos()
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
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    val fmtDate = strToDate(date)
                    viewModel.reset(fmtDate)
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
            refreshing = isPulling, //isLoading && repos.isEmpty(),
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

private fun strToDate(date: String?): String? {
    val fmtDate = when (date) {
        "2 Weeks" -> Date().minusDays(14).yymmdd()
        "1 Month" -> Date().minusMonths(1).yymmdd()
        "2 Months" -> Date().minusMonths(2).yymmdd()
        "6 Months" -> Date().minusMonths(6).yymmdd()
        "1 Year" -> Date().minusYears(1).yymmdd()
        else -> null
    }
    return fmtDate
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

@Composable
private fun DateFilter(
    selectedDate: String?,
    onDateSelected: (String?) -> Unit
) {
    val dates = listOf("2 Weeks", "1 Month", "2 Months", "6 Months", "1 Year")

    Column {
        Text(
            text = "Filter by date:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedDate == null,
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

fun Date.minusDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_YEAR, -days)
    return calendar.time
}

fun Date.minusMonths(months: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, -months)
    return calendar.time
}   

fun Date.minusYears(years: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.YEAR, -years)
    return calendar.time
}
    
fun Date.yymmdd(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return formatter.format(this)
}