package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teacher_profiles")
data class TeacherProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileName: String = "My Teacher Profile",
    val teacherName: String = "",
    val designation: String = "",
    val department: String = "",
    val institution: String = ""
)
