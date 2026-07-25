package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileName: String = "My Default Profile",
    val studentName: String = "",
    val studentId: String = "",
    val section: String = "",
    val semester: String = "",
    val department: String = "",
    val universityName: String = "",
    val isDefault: Boolean = false
)
