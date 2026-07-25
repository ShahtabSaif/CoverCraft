package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cover_pages")
data class CoverPageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "Untitled Cover Page",
    val docType: String = "LAB REPORT", // LAB REPORT, ASSIGNMENT, PROJECT REPORT, THESIS, TERM PAPER, CUSTOM
    val universityName: String = "Daffodil International University",
    val logoPreset: String = "DAFFODIL", // DAFFODIL, BUET, DU, SCIENCE_TECH, GENERIC_SHIELD, CUSTOM
    val customLogoUri: String? = null,
    val watermarkPreset: String = "DAFFODIL",
    val customWatermarkUri: String? = null,
    val showWatermark: Boolean = true,
    val watermarkOpacity: Float = 0.12f,
    
    // Experiment / Course Details
    val experimentNo: String = "",
    val experimentName: String = "",
    val courseCode: String = "",
    val courseTitle: String = "",
    val assignmentTopic: String = "",
    val extraNote: String = "",

    // Submitted To
    val submittedToHeader: String = "Submitted To",
    val submittedToHeaderStyle: String = "UNDERLINE", // UNDERLINE, PILL, BOLD_PLAIN
    val submittedToName: String = "",
    val submittedToDesignation: String = "",
    val submittedToDepartment: String = "",
    val submittedToInstitution: String = "Daffodil International University",

    // Submitted By
    val submittedByHeader: String = "Submitted By",
    val submittedByHeaderStyle: String = "UNDERLINE", // UNDERLINE, PILL, BOLD_PLAIN
    val submittedByName: String = "",
    val submittedById: String = "",
    val submittedBySection: String = "",
    val submittedBySemester: String = "",
    val submittedByDepartment: String = "",
    val submittedByInstitution: String = "Daffodil International University",

    // Submission Date
    val submissionDate: String = "",
    val dateStyle: String = "ROUNDED_PILL", // ROUNDED_PILL, UNDERLINE, PLAIN
    val footerWebsite: String = "",

    // Styling options
    val borderStyle: String = "SOLID", // SOLID, DOUBLE, DECORATIVE, THICK, NONE
    val borderMarginDp: Int = 16,
    val fontFamily: String = "SANS_SERIF", // SANS_SERIF, SERIF, MONOSPACE
    val accentColorHex: String = "#1E3A8A",
    val textColorHex: String = "#0F172A",

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
