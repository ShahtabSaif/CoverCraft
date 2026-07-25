package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoverPageEntity
import com.example.ui.components.CoverPageCanvas
import com.example.ui.viewmodel.CoverPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: CoverPageViewModel,
    onNavigateToLibrary: () -> Unit
) {
    val context = LocalContext.current
    val currentCoverPage by viewModel.currentCoverPage.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val studentProfiles by viewModel.allStudentProfiles.collectAsState()
    val teacherProfiles by viewModel.allTeacherProfiles.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Document, 1: Course/Exp, 2: Submitted To, 3: Submitted By, 4: Style/Borders
    var isPreviewFullScreen by remember { mutableStateOf(false) }
    var isPreviewCompact by remember { mutableStateOf(false) }

    // Custom Logo Image Picker
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateCurrentCoverPage(currentCoverPage.copy(customLogoUri = it.toString()))
        }
    }

    // Custom Watermark Image Picker
    val watermarkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateCurrentCoverPage(currentCoverPage.copy(customWatermarkUri = it.toString()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Cover Page Editor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentCoverPage.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isPreviewFullScreen = !isPreviewFullScreen }) {
                        Icon(
                            imageVector = if (isPreviewFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Toggle Fullscreen Preview"
                        )
                    }
                    IconButton(onClick = { viewModel.saveCurrentCoverPage(context) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Cover Page")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Quick Compact Export Bar
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.exportImage(context) },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        enabled = !isExporting,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Outlined.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export Image", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.exportPdf(context) },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        enabled = !isExporting,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isPreviewFullScreen) {
            // Fullscreen Live Preview Mode
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier
                        .fillMaxHeight(0.92f)
                        .padding(16.dp)
                ) {
                    CoverPageCanvas(coverPage = currentCoverPage)
                }

                FloatingActionButton(
                    onClick = { isPreviewFullScreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Fullscreen")
                }
            }
        } else {
            // Split Editor View: Top Live Preview Card + Scrollable Customization Controls
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top Live Canvas Preview Container (Compact & Usable)
                AnimatedVisibility(
                    visible = !isPreviewCompact,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable { isPreviewFullScreen = true }
                        ) {
                            CoverPageCanvas(coverPage = currentCoverPage)
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { isPreviewCompact = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Compress, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Minimize", fontSize = 10.sp, color = Color.White)
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { isPreviewFullScreen = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ZoomIn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Zoom", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (isPreviewCompact) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPreviewCompact = false }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Live Preview Minimized", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            TextButton(onClick = { isPreviewCompact = false }) {
                                Icon(Icons.Default.Expand, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Show Preview", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Quick Student Profile Auto-Fill Bar
                if (studentProfiles.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Text(
                                "Fill Profile:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(studentProfiles) { profile ->
                            FilterChip(
                                selected = false,
                                onClick = { viewModel.applyStudentProfile(profile) },
                                label = { Text(profile.profileName, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            )
                        }
                    }
                }

                // Editor Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = activeTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("1. Header") },
                        icon = { Icon(Icons.Default.School, contentDescription = null) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("2. Course/Lab") },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = null) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("3. Submitted To") },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        text = { Text("4. Submitted By") },
                        icon = { Icon(Icons.Default.Group, contentDescription = null) }
                    )
                    Tab(
                        selected = activeTab == 4,
                        onClick = { activeTab = 4 },
                        text = { Text("5. Style & Dates") },
                        icon = { Icon(Icons.Default.Palette, contentDescription = null) }
                    )
                }

                // Tab Controls Content Panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (activeTab) {
                        0 -> {
                            // Header & University Info
                            OutlinedTextField(
                                value = currentCoverPage.title,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(title = it)) },
                                label = { Text("Cover Page Name / Saved Reference Title") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentCoverPage.universityName,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(universityName = it)) },
                                label = { Text("University / Institution Name") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                                singleLine = true
                            )

                            Text(
                                "Document Type Header:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = currentCoverPage.docType,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(docType = it)) },
                                label = { Text("Document Header (e.g. LAB REPORT / ASSIGNMENT)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            val docTypes = listOf("LAB REPORT", "ASSIGNMENT", "PROJECT REPORT", "THESIS REPORT", "TERM PAPER", "PRESENTATION", "SEMINAR REPORT")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(docTypes) { type ->
                                    FilterChip(
                                        selected = currentCoverPage.docType.equals(type, ignoreCase = true),
                                        onClick = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(docType = type)) },
                                        label = { Text(type) }
                                    )
                                }
                            }

                            Divider()

                            Text(
                                "Logo Preset & Watermark:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            val logoPresets = listOf(
                                "DAFFODIL" to "Daffodil Int. Univ."
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(logoPresets) { (presetKey, presetName) ->
                                    FilterChip(
                                        selected = currentCoverPage.logoPreset == presetKey && currentCoverPage.customLogoUri == null,
                                        onClick = {
                                            viewModel.updateCurrentCoverPage(
                                                currentCoverPage.copy(
                                                    logoPreset = presetKey,
                                                    watermarkPreset = presetKey,
                                                    customLogoUri = null
                                                )
                                            )
                                        },
                                        label = { Text(presetName) }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { logoPickerLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Upload Custom Logo")
                                }

                                if (currentCoverPage.customLogoUri != null) {
                                    IconButton(onClick = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(customLogoUri = null)) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove custom logo", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Course & Experiment Details
                            Text("Experiment / Topic Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = currentCoverPage.experimentNo,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(experimentNo = it)) },
                                label = { Text("Experiment No (e.g., 03)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentCoverPage.experimentName,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(experimentName = it)) },
                                label = { Text("Experiment Name") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            OutlinedTextField(
                                value = currentCoverPage.assignmentTopic,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(assignmentTopic = it)) },
                                label = { Text("Topic / Project Name (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            Divider()

                            Text("Course Information", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = currentCoverPage.courseCode,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(courseCode = it)) },
                                label = { Text("Course Code (e.g., CSE216)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentCoverPage.courseTitle,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(courseTitle = it)) },
                                label = { Text("Course Title (e.g., Electronic Devices and Circuit)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        2 -> {
                            // Submitted To Information
                            Text("Submitted To Block", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            if (teacherProfiles.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Quick Auto-fill from Teacher Profiles:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(teacherProfiles) { profile ->
                                            SuggestionChip(
                                                onClick = { viewModel.applyTeacherProfile(profile) },
                                                label = { Text(profile.profileName) },
                                                icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = currentCoverPage.submittedToHeader,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedToHeader = it)) },
                                label = { Text("Section Header (e.g., Submitted To / Course Teacher / Supervisor)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentCoverPage.submittedToName,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedToName = it)) },
                                label = { Text("Teacher / Lecturer Name") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentCoverPage.submittedToDesignation,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedToDesignation = it)) },
                                label = { Text("Designation (e.g., Lecturer / Professor)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentCoverPage.submittedToDepartment,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedToDepartment = it)) },
                                label = { Text("Department (e.g., Department of CSE)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentCoverPage.submittedToInstitution,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedToInstitution = it)) },
                                label = { Text("Institution Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        3 -> {
                            // Submitted By Information
                            Text("Submitted By Block", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            if (studentProfiles.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Quick Auto-fill from Student Profiles:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(studentProfiles) { profile ->
                                            SuggestionChip(
                                                onClick = { viewModel.applyStudentProfile(profile) },
                                                label = { Text(profile.profileName) },
                                                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = currentCoverPage.submittedByName,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedByName = it)) },
                                label = { Text("Student Name") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = currentCoverPage.submittedById,
                                    onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedById = it)) },
                                    label = { Text("Student ID (e.g. 251-15-181)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = currentCoverPage.submittedBySection,
                                    onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedBySection = it)) },
                                    label = { Text("Section (e.g. 68_A2)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = currentCoverPage.submittedBySemester,
                                    onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedBySemester = it)) },
                                    label = { Text("Semester (e.g. Summer 26)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = currentCoverPage.submittedByDepartment,
                                    onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedByDepartment = it)) },
                                    label = { Text("Department") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = currentCoverPage.submittedByInstitution,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submittedByInstitution = it)) },
                                label = { Text("Institution Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        4 -> {
                            // Style, Fonts, Borders & Submission Date
                            Text("Submission Date", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = currentCoverPage.submissionDate,
                                onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(submissionDate = it)) },
                                label = { Text("Date of Submission (e.g., 21/06/2026)") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                                singleLine = true
                            )

                            Divider()

                            Text("Border Style:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            val borderStyles = listOf("SOLID", "DOUBLE", "DECORATIVE", "THICK", "NONE")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(borderStyles) { style ->
                                    FilterChip(
                                        selected = currentCoverPage.borderStyle == style,
                                        onClick = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(borderStyle = style)) },
                                        label = { Text(style) }
                                    )
                                }
                            }

                            Divider()

                            Text("Typography Font Family:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FilterChip(
                                    selected = currentCoverPage.fontFamily == "SANS_SERIF",
                                    onClick = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(fontFamily = "SANS_SERIF")) },
                                    label = { Text("Sans-Serif (Modern)") }
                                )
                                FilterChip(
                                    selected = currentCoverPage.fontFamily == "SERIF",
                                    onClick = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(fontFamily = "SERIF")) },
                                    label = { Text("Serif (Academic)") }
                                )
                                FilterChip(
                                    selected = currentCoverPage.fontFamily == "MONOSPACE",
                                    onClick = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(fontFamily = "MONOSPACE")) },
                                    label = { Text("Monospace") }
                                )
                            }

                            Divider()

                            Text("Watermark Controls:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Switch(
                                    checked = currentCoverPage.showWatermark,
                                    onCheckedChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(showWatermark = it)) }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Show Central University Watermark")
                            }

                            if (currentCoverPage.showWatermark) {
                                Column {
                                    Text("Watermark Opacity: ${(currentCoverPage.watermarkOpacity * 100).toInt()}%")
                                    Slider(
                                        value = currentCoverPage.watermarkOpacity,
                                        onValueChange = { viewModel.updateCurrentCoverPage(currentCoverPage.copy(watermarkOpacity = it)) },
                                        valueRange = 0.02f..0.95f
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
