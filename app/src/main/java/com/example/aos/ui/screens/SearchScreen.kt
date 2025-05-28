package com.example.aos.ui.screens

import android.app.Application
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aos.model.GithubRepo
import com.example.aos.viewmodel.SearchViewModel
import com.example.aos.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onRepoClick: (GithubRepo) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguages by remember { mutableStateOf<Set<String>>(emptySet()) }

    val expandLanguageFilter by viewModel.expandLanguageFilter.collectAsState()

    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Search Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.searchRepos(it, selectedLanguages)
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search repositories") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        viewModel.searchRepos("", selectedLanguages)
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true
                        )
                        TextButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text("Cancel")
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Language Filter
            LanguageFilter(
                expanded = expandLanguageFilter,
                selectedLanguages = selectedLanguages,
                onExpand = { viewModel.toggleLanguageFilter() },
                onLanguageSelected = { languages ->
                    selectedLanguages = languages
                    viewModel.toggleLanguageFilter()
                    viewModel.searchRepos(searchQuery, languages)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Results
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error ?: "An error occurred",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                viewModel.searchRepos(
                                    searchQuery,
                                    selectedLanguages
                                )
                            }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(repos) { repo ->
                            RepoItem(
                                repo = repo,
                                keywords = searchQuery,
                                onClick = { onRepoClick(repo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageFilter(
    expanded: Boolean,
    selectedLanguages: Set<String>,
    onExpand: () -> Unit,
    onLanguageSelected: (Set<String>) -> Unit
) {
    val languages = listOf("Kotlin", "Java", "Python", "JavaScript", "TypeScript", "Go", "Rust")

    Column {
        FilterChip(
            selected = false,
            onClick = { onExpand() },
            label = {
                Row {
                    Text(
                        text = if (selectedLanguages.isEmpty()) {
                            "Filter by language"
                        } else if (selectedLanguages.size == 1) {
                            selectedLanguages.first()
                        } else {
                            selectedLanguages.first() + "+" + (selectedLanguages.size - 1)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (expanded) {
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
        if (expanded) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedLanguages.isEmpty(),
                    onClick = { onLanguageSelected(emptySet()) },
                    label = { Text("All") }
                )
                languages.forEach { language ->
                    FilterChip(
                        selected = language in selectedLanguages,
                        onClick = {
                            val newSelection = if (language in selectedLanguages) {
                                selectedLanguages - language
                            } else {
                                selectedLanguages + language
                            }
                            onLanguageSelected(newSelection)
                        },
                        label = { Text(language) }
                    )
                }
            }
        }
    }
} 