package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoverPageEntity
import com.example.ui.components.CoverPageCanvas
import com.example.ui.viewmodel.CoverPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPagesScreen(
    viewModel: CoverPageViewModel,
    onNavigateToEditor: () -> Unit
) {
    val context = LocalContext.current
    val savedPages by viewModel.allCoverPages.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cover Pages Library", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        viewModel.createNewCoverPage()
                        onNavigateToEditor()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Create New Cover Page")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (savedPages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved cover pages yet!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create a new cover page or pick a university preset.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        viewModel.createNewCoverPage()
                        onNavigateToEditor()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Cover Page")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedPages) { page ->
                    SavedPageCard(
                        page = page,
                        onEdit = {
                            viewModel.selectCoverPage(page)
                            onNavigateToEditor()
                        },
                        onExportPdf = {
                            viewModel.selectCoverPage(page)
                            viewModel.exportPdf(context)
                        },
                        onExportImage = {
                            viewModel.selectCoverPage(page)
                            viewModel.exportImage(context)
                        },
                        onDelete = {
                            viewModel.deleteCoverPage(page, context)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedPageCard(
    page: CoverPageEntity,
    onEdit: () -> Unit,
    onExportPdf: () -> Unit,
    onExportImage: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini Canvas Thumbnail
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp)
                    .clickable { onEdit() }
            ) {
                CoverPageCanvas(coverPage = page)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details Column & Action Buttons
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = page.docType,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = page.universityName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                if (page.courseCode.isNotEmpty()) {
                    Text(
                        text = "Course: ${page.courseCode}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (page.submissionDate.isNotEmpty()) {
                    Text(
                        text = "Date: ${page.submissionDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action Row: Edit, Export PDF, Export Image, Delete
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                    }

                    FilledTonalIconButton(
                        onClick = onExportPdf,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", modifier = Modifier.size(18.dp))
                    }

                    FilledTonalIconButton(
                        onClick = onExportImage,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Outlined.Image, contentDescription = "Export PNG", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
