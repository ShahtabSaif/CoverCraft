package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.StudentProfileEntity
import com.example.data.model.TeacherProfileEntity
import com.example.ui.viewmodel.CoverPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    viewModel: CoverPageViewModel
) {
    val context = LocalContext.current
    val studentProfiles by viewModel.allStudentProfiles.collectAsState()
    val teacherProfiles by viewModel.allTeacherProfiles.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Student, 1: Teacher
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddTeacherDialog by remember { mutableStateOf(false) }

    // Dialog state for new student profile
    var sProfileName by remember { mutableStateOf("") }
    var sStudentName by remember { mutableStateOf("") }
    var sStudentId by remember { mutableStateOf("") }
    var sSection by remember { mutableStateOf("") }
    var sSemester by remember { mutableStateOf("") }
    var sDepartment by remember { mutableStateOf("") }
    var sUniversityName by remember { mutableStateOf("") }

    // Dialog state for new teacher profile
    var tProfileName by remember { mutableStateOf("") }
    var tTeacherName by remember { mutableStateOf("") }
    var tDesignation by remember { mutableStateOf("") }
    var tDepartment by remember { mutableStateOf("") }
    var tInstitution by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Profiles", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        if (selectedTab == 0) showAddStudentDialog = true else showAddTeacherDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddStudentDialog = true else showAddTeacherDialog = true
                },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text(if (selectedTab == 0) "Add Student" else "Add Teacher") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Student Profiles (${studentProfiles.size})") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Teacher Profiles (${teacherProfiles.size})") },
                    icon = { Icon(Icons.Default.School, contentDescription = null) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (selectedTab == 0) {
                    // Student Profiles Tab
                    Text(
                        text = "Save student details to auto-fill 'Submitted By' with 1 tap in the editor!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (studentProfiles.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No student profiles saved yet.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(studentProfiles) { profile ->
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
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
                                            Text(
                                                text = profile.profileName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Button(
                                                    onClick = {
                                                        viewModel.applyStudentProfile(profile)
                                                        Toast.makeText(context, "Applied ${profile.profileName} to active cover page!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Apply", fontSize = 12.sp)
                                                }

                                                IconButton(onClick = { viewModel.deleteStudentProfile(profile, context) }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Profile",
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                                        Text("Name: ${profile.studentName}", style = MaterialTheme.typography.bodyMedium)
                                        Text("ID: ${profile.studentId} | Section: ${profile.section}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Semester: ${profile.semester} | Dept: ${profile.department}", style = MaterialTheme.typography.bodyMedium)
                                        Text("University: ${profile.universityName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Teacher Profiles Tab
                    Text(
                        text = "Save teacher details to auto-fill 'Submitted To' with 1 tap in the editor!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (teacherProfiles.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No teacher profiles saved yet.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(teacherProfiles) { profile ->
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
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
                                            Text(
                                                text = profile.profileName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Button(
                                                    onClick = {
                                                        viewModel.applyTeacherProfile(profile)
                                                        Toast.makeText(context, "Applied ${profile.profileName} to active cover page!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Apply", fontSize = 12.sp)
                                                }

                                                IconButton(onClick = { viewModel.deleteTeacherProfile(profile, context) }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Profile",
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                                        Text("Teacher Name: ${profile.teacherName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text("Designation: ${profile.designation}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Department: ${profile.department}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Institution: ${profile.institution}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Student Profile Dialog
        if (showAddStudentDialog) {
            AlertDialog(
                onDismissRequest = { showAddStudentDialog = false },
                title = { Text("New Student Profile") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = sProfileName,
                            onValueChange = { sProfileName = it },
                            label = { Text("Profile Nickname (e.g., My Daffodil Profile)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sStudentName,
                            onValueChange = { sStudentName = it },
                            label = { Text("Student Name") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sStudentId,
                            onValueChange = { sStudentId = it },
                            label = { Text("Student ID") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sSection,
                            onValueChange = { sSection = it },
                            label = { Text("Section") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sSemester,
                            onValueChange = { sSemester = it },
                            label = { Text("Semester") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sDepartment,
                            onValueChange = { sDepartment = it },
                            label = { Text("Department") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sUniversityName,
                            onValueChange = { sUniversityName = it },
                            label = { Text("University / Institution") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (sProfileName.isNotBlank() && sStudentName.isNotBlank()) {
                                viewModel.saveStudentProfile(
                                    StudentProfileEntity(
                                        profileName = sProfileName,
                                        studentName = sStudentName,
                                        studentId = sStudentId,
                                        section = sSection,
                                        semester = sSemester,
                                        department = sDepartment,
                                        universityName = sUniversityName
                                    ),
                                    context
                                )
                                showAddStudentDialog = false
                                sProfileName = ""
                                sStudentName = ""
                                sStudentId = ""
                                sSection = ""
                                sSemester = ""
                                sDepartment = ""
                                sUniversityName = ""
                            } else {
                                Toast.makeText(context, "Please enter profile name and student name", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save Student Profile")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStudentDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Add Teacher Profile Dialog
        if (showAddTeacherDialog) {
            AlertDialog(
                onDismissRequest = { showAddTeacherDialog = false },
                title = { Text("New Teacher Profile") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = tProfileName,
                            onValueChange = { tProfileName = it },
                            label = { Text("Profile Nickname (e.g., Prof. Tanin - DIU)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tTeacherName,
                            onValueChange = { tTeacherName = it },
                            label = { Text("Teacher Name") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tDesignation,
                            onValueChange = { tDesignation = it },
                            label = { Text("Designation (e.g. Lecturer / Professor)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tDepartment,
                            onValueChange = { tDepartment = it },
                            label = { Text("Department") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tInstitution,
                            onValueChange = { tInstitution = it },
                            label = { Text("Institution Name") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tProfileName.isNotBlank() && tTeacherName.isNotBlank()) {
                                viewModel.saveTeacherProfile(
                                    TeacherProfileEntity(
                                        profileName = tProfileName,
                                        teacherName = tTeacherName,
                                        designation = tDesignation,
                                        department = tDepartment,
                                        institution = tInstitution
                                    ),
                                    context
                                )
                                showAddTeacherDialog = false
                                tProfileName = ""
                                tTeacherName = ""
                                tDesignation = ""
                                tDepartment = ""
                                tInstitution = ""
                            } else {
                                Toast.makeText(context, "Please enter profile name and teacher name", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save Teacher Profile")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTeacherDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
