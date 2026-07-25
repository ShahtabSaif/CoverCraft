package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "university_templates")
data class UniversityTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateName: String = "",
    val universityName: String,
    val docType: String = "ASSIGNMENT",
    val logoPreset: String = "GENERIC_SHIELD",
    val customLogoUri: String? = null,
    val watermarkPreset: String = "GENERIC_SHIELD",
    val customWatermarkUri: String? = null,
    val defaultDepartment: String = "",
    val accentColorHex: String = "#1E3A8A",
    val footerWebsite: String = ""
)
