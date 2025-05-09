package com.example.aos.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aos.viewmodel.RaiseIssueViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aos.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RaiseIssueScreen(
    owner: String,
    repo: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RaiseIssueViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val formState by viewModel.formState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val context = LocalContext.current

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var body by remember { mutableStateOf(TextFieldValue("")) }
    var selectedLabels by remember { mutableStateOf(setOf<String>()) }

    val scrollState = rememberScrollState()

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Issue created successfully", Toast.LENGTH_SHORT).show()
            // TODO how to fine control back
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Issue") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create Issue for $owner/$repo",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    viewModel.updateTitle(it.text)
                },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Menu, contentDescription = null)
                }
            )

            OutlinedTextField(
                value = body,
                onValueChange = { 
                    body = it
                    viewModel.updateBody(it.text)
                },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            )

            // Labels section
            Text(
                text = "Labels",
                style = MaterialTheme.typography.titleMedium
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val commonLabels = listOf("bug", "enhancement", "documentation", "question")
                commonLabels.forEach { label ->
                    FilterChip(
                        selected = selectedLabels.contains(label),
                        onClick = {
                            selectedLabels = if (selectedLabels.contains(label)) {
                                selectedLabels - label
                            } else {
                                selectedLabels + label
                            }
                            viewModel.updateLabels(selectedLabels.toList())
                        },
                        label = { Text(label) }
                    )
                }
            }

            if (error != null) {
                Text(
                    text = error ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = { viewModel.createIssue(owner, repo) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && title.text.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text("Create Issue")
                }
            }
        }
    }
} 