package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.CoverPageEntity
import com.example.data.model.StudentProfileEntity
import com.example.data.model.TeacherProfileEntity
import com.example.data.model.UniversityTemplateEntity
import com.example.data.repository.CoverPageRepository
import com.example.export.ImageExporter
import com.example.export.PdfExporter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CoverPageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CoverPageRepository

    val allCoverPages: StateFlow<List<CoverPageEntity>>
    val allStudentProfiles: StateFlow<List<StudentProfileEntity>>
    val allTeacherProfiles: StateFlow<List<TeacherProfileEntity>>
    val allUniversityTemplates: StateFlow<List<UniversityTemplateEntity>>

    private val _currentCoverPage = MutableStateFlow(getDefaultDaffodilLabReport())
    val currentCoverPage: StateFlow<CoverPageEntity> = _currentCoverPage.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CoverPageRepository(database.coverPageDao())

        allCoverPages = repository.allCoverPages
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allStudentProfiles = repository.allStudentProfiles
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allTeacherProfiles = repository.allTeacherProfiles
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allUniversityTemplates = repository.allUniversityTemplates
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Seed initial data if empty & clear all saved profiles and old seeded fields
        viewModelScope.launch {
            try {
                allStudentProfiles.first().forEach { profile ->
                    repository.deleteStudentProfile(profile)
                }
                allTeacherProfiles.first().forEach { profile ->
                    repository.deleteTeacherProfile(profile)
                }
            } catch (e: Exception) {
                // Ignore cleanup errors
            }

            allCoverPages.first().let { list ->
                if (list.isEmpty()) {
                    val initialId = repository.saveCoverPage(getDefaultDaffodilLabReport())
                    _currentCoverPage.value = getDefaultDaffodilLabReport().copy(id = initialId)
                } else {
                    val first = list.first()
                    if (first.submittedByName.isNotBlank() || first.submittedToName.isNotBlank() || first.submittedById.isNotBlank()) {
                        val cleaned = getDefaultDaffodilLabReport().copy(id = first.id)
                        repository.saveCoverPage(cleaned)
                        _currentCoverPage.value = cleaned
                    } else {
                        _currentCoverPage.value = first
                    }
                }
            }
        }
    }

    fun updateCurrentCoverPage(updated: CoverPageEntity) {
        _currentCoverPage.value = updated
    }

    fun selectCoverPage(coverPage: CoverPageEntity) {
        _currentCoverPage.value = coverPage
    }

    fun createNewCoverPage(presetType: String = "DAFFODIL_LAB_REPORT") {
        val newPage = getDefaultDaffodilLabReport().copy(id = 0, title = "")
        viewModelScope.launch {
            val newId = repository.saveCoverPage(newPage)
            _currentCoverPage.value = newPage.copy(id = newId)
        }
    }

    fun saveCurrentCoverPage(context: Context) {
        viewModelScope.launch {
            val id = repository.saveCoverPage(_currentCoverPage.value)
            _currentCoverPage.value = _currentCoverPage.value.copy(id = id)
            Toast.makeText(context, "Cover page saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteCoverPage(coverPage: CoverPageEntity, context: Context) {
        viewModelScope.launch {
            repository.deleteCoverPage(coverPage)
            if (_currentCoverPage.value.id == coverPage.id) {
                val remaining = allCoverPages.value.filter { it.id != coverPage.id }
                if (remaining.isNotEmpty()) {
                    _currentCoverPage.value = remaining.first()
                } else {
                    createNewCoverPage()
                }
            }
            Toast.makeText(context, "Deleted cover page", Toast.LENGTH_SHORT).show()
        }
    }

    fun applyStudentProfile(profile: StudentProfileEntity) {
        _currentCoverPage.value = _currentCoverPage.value.copy(
            submittedByName = profile.studentName.ifEmpty { _currentCoverPage.value.submittedByName },
            submittedById = profile.studentId.ifEmpty { _currentCoverPage.value.submittedById },
            submittedBySection = profile.section.ifEmpty { _currentCoverPage.value.submittedBySection },
            submittedBySemester = profile.semester.ifEmpty { _currentCoverPage.value.submittedBySemester },
            submittedByDepartment = profile.department.ifEmpty { _currentCoverPage.value.submittedByDepartment },
            submittedByInstitution = profile.universityName.ifEmpty { _currentCoverPage.value.submittedByInstitution }
        )
    }

    fun applyTeacherProfile(profile: TeacherProfileEntity) {
        _currentCoverPage.value = _currentCoverPage.value.copy(
            submittedToName = profile.teacherName.ifEmpty { _currentCoverPage.value.submittedToName },
            submittedToDesignation = profile.designation.ifEmpty { _currentCoverPage.value.submittedToDesignation },
            submittedToDepartment = profile.department.ifEmpty { _currentCoverPage.value.submittedToDepartment },
            submittedToInstitution = profile.institution.ifEmpty { _currentCoverPage.value.submittedToInstitution }
        )
    }

    fun deleteTeacherProfile(profile: TeacherProfileEntity, context: Context) {
        viewModelScope.launch {
            repository.deleteTeacherProfile(profile)
            Toast.makeText(context, "Deleted teacher profile '${profile.profileName}'", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveTeacherProfile(profile: TeacherProfileEntity, context: Context) {
        viewModelScope.launch {
            repository.saveTeacherProfile(profile)
            Toast.makeText(context, "Saved profile '${profile.profileName}'", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteStudentProfile(profile: StudentProfileEntity, context: Context) {
        viewModelScope.launch {
            repository.deleteStudentProfile(profile)
            Toast.makeText(context, "Deleted student profile '${profile.profileName}'", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveStudentProfile(profile: StudentProfileEntity, context: Context) {
        viewModelScope.launch {
            repository.saveStudentProfile(profile)
            Toast.makeText(context, "Saved profile '${profile.profileName}'", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteUniversityTemplate(template: UniversityTemplateEntity, context: Context) {
        viewModelScope.launch {
            repository.deleteUniversityTemplate(template)
            Toast.makeText(context, "Deleted preset '${template.universityName}'", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveUniversityTemplate(template: UniversityTemplateEntity, context: Context) {
        viewModelScope.launch {
            repository.saveUniversityTemplate(template)
            Toast.makeText(context, "Saved template '${template.universityName}'", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportPdf(context: Context) {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val file = PdfExporter.exportToPdf(context, _currentCoverPage.value)
                PdfExporter.sharePdf(context, file)
            } catch (e: Exception) {
                Toast.makeText(context, "PDF Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportImage(context: Context) {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val file = ImageExporter.exportToImage(context, _currentCoverPage.value)
                ImageExporter.shareImage(context, file)
            } catch (e: Exception) {
                Toast.makeText(context, "Image Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                _isExporting.value = false
            }
        }
    }

    companion object {
        fun getDefaultDaffodilLabReport(): CoverPageEntity {
            return CoverPageEntity(
                title = "",
                docType = "LAB REPORT",
                universityName = "Daffodil International University",
                logoPreset = "DAFFODIL",
                watermarkPreset = "DAFFODIL",
                showWatermark = true,
                watermarkOpacity = 0.12f,
                experimentNo = "",
                experimentName = "",
                assignmentTopic = "",
                courseCode = "",
                courseTitle = "",
                submittedToHeader = "Submitted To",
                submittedToHeaderStyle = "UNDERLINE",
                submittedToName = "",
                submittedToDesignation = "",
                submittedToDepartment = "",
                submittedToInstitution = "Daffodil International University",
                submittedByHeader = "Submitted By",
                submittedByHeaderStyle = "UNDERLINE",
                submittedByName = "",
                submittedById = "",
                submittedBySection = "",
                submittedBySemester = "",
                submittedByDepartment = "",
                submittedByInstitution = "Daffodil International University",
                submissionDate = "",
                dateStyle = "ROUNDED_PILL",
                footerWebsite = "",
                borderStyle = "SOLID",
                borderMarginDp = 16,
                fontFamily = "SANS_SERIF",
                accentColorHex = "#15439B"
            )
        }
    }
}
