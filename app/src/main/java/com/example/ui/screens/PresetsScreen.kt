package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoverPageEntity
import com.example.data.model.UniversityTemplateEntity
import com.example.ui.components.CoverPageCanvas
import com.example.ui.viewmodel.CoverPageViewModel

data class PresetOption(
    val id: String,
    val title: String,
    val description: String,
    val coverPage: CoverPageEntity
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsScreen(
    viewModel: CoverPageViewModel,
    onNavigateToEditor: () -> Unit
) {
    val context = LocalContext.current
    val customTemplates by viewModel.allUniversityTemplates.collectAsState()
    var showAddPresetDialog by remember { mutableStateOf(false) }

    // Dialog state for creating a new custom preset
    var newTemplateName by remember { mutableStateOf("") }
    var newUniversityName by remember { mutableStateOf("") }
    var newLogoPreset by remember { mutableStateOf("DAFFODIL") }
    var newDocType by remember { mutableStateOf("ASSIGNMENT") }

    val defaultPresets = listOf(
        PresetOption(
            id = "DAFFODIL_LAB_REPORT",
            title = "Daffodil Lab Report (Official)",
            description = "Official Daffodil International University reference layout with shield crest, experiment details & pill headers.",
            coverPage = CoverPageViewModel.getDefaultDaffodilLabReport()
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("University Cover Templates", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showAddPresetDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Custom Preset")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddPresetDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Custom Preset") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select a preset or add your own custom university template:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Official Default Daffodil Preset
                items(defaultPresets) { preset ->
                    Card(
                        onClick = {
                            viewModel.createNewCoverPage(preset.id)
                            onNavigateToEditor()
                        },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                                    CoverPageCanvas(coverPage = preset.coverPage)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = preset.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    viewModel.createNewCoverPage(preset.id)
                                    onNavigateToEditor()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Use & Customize")
                            }
                        }
                    }
                }

                // Custom User Saved Presets
                items(customTemplates) { template ->
                    val customCoverPage = CoverPageViewModel.getDefaultDaffodilLabReport().copy(
                        universityName = template.universityName,
                        docType = template.docType,
                        logoPreset = template.logoPreset,
                        customLogoUri = template.customLogoUri,
                        watermarkPreset = template.watermarkPreset,
                        customWatermarkUri = template.customWatermarkUri,
                        accentColorHex = template.accentColorHex
                    )

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = template.templateName.ifEmpty { template.universityName },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                IconButton(onClick = { viewModel.deleteUniversityTemplate(template, context) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Preset",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Text(
                                text = "University: ${template.universityName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
                                    CoverPageCanvas(coverPage = customCoverPage)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.updateCurrentCoverPage(customCoverPage)
                                    onNavigateToEditor()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apply Preset")
                            }
                        }
                    }
                }
            }
        }

        // Add Custom Preset Dialog
        if (showAddPresetDialog) {
            AlertDialog(
                onDismissRequest = { showAddPresetDialog = false },
                title = { Text("Add Custom Preset") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newTemplateName,
                            onValueChange = { newTemplateName = it },
                            label = { Text("Preset Nickname (e.g. My Custom DIU)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newUniversityName,
                            onValueChange = { newUniversityName = it },
                            label = { Text("University Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDocType,
                            onValueChange = { newDocType = it },
                            label = { Text("Document Type (e.g., ASSIGNMENT, THESIS)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Logo Style Preset:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("DAFFODIL").forEach { presetKey ->
                                FilterChip(
                                    selected = newLogoPreset == presetKey,
                                    onClick = { newLogoPreset = presetKey },
                                    label = { Text(presetKey, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newUniversityName.isNotBlank()) {
                                viewModel.saveUniversityTemplate(
                                    UniversityTemplateEntity(
                                        templateName = newTemplateName.ifEmpty { newUniversityName },
                                        universityName = newUniversityName,
                                        docType = newDocType,
                                        logoPreset = newLogoPreset,
                                        watermarkPreset = newLogoPreset
                                    ),
                                    context
                                )
                                showAddPresetDialog = false
                                newTemplateName = ""
                                newUniversityName = ""
                            } else {
                                Toast.makeText(context, "Please enter a University Name", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save Preset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddPresetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
