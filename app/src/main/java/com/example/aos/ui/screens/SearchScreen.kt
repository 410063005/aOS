package com.example.aos.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aos.model.GithubRepo
import com.example.aos.viewmodel.SearchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
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
                selectedLanguages = selectedLanguages,
                onLanguageSelected = { languages ->
                    selectedLanguages = languages
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
                            Button(onClick = { viewModel.searchRepos(searchQuery, selectedLanguages) }) {
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
    selectedLanguages: Set<String>,
    onLanguageSelected: (Set<String>) -> Unit
) {
    val languages = listOf("Kotlin", "Java", "Python", "JavaScript", "TypeScript", "Go", "Rust")
    
    Column {
        Text(
            text = "Filter by language:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
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