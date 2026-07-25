package com.example.data.dao

import androidx.room.*
import com.example.data.model.CoverPageEntity
import com.example.data.model.StudentProfileEntity
import com.example.data.model.TeacherProfileEntity
import com.example.data.model.UniversityTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoverPageDao {
    @Query("SELECT * FROM cover_pages ORDER BY updatedAt DESC")
    fun getAllCoverPages(): Flow<List<CoverPageEntity>>

    @Query("SELECT * FROM cover_pages WHERE id = :id")
    suspend fun getCoverPageById(id: Long): CoverPageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoverPage(coverPage: CoverPageEntity): Long

    @Update
    suspend fun updateCoverPage(coverPage: CoverPageEntity)

    @Delete
    suspend fun deleteCoverPage(coverPage: CoverPageEntity)

    @Query("DELETE FROM cover_pages WHERE id = :id")
    suspend fun deleteCoverPageById(id: Long)

    // Student Profiles
    @Query("SELECT * FROM student_profiles ORDER BY id DESC")
    fun getAllStudentProfiles(): Flow<List<StudentProfileEntity>>

    @Query("SELECT * FROM student_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultStudentProfile(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(profile: StudentProfileEntity): Long

    @Delete
    suspend fun deleteStudentProfile(profile: StudentProfileEntity)

    // Teacher Profiles
    @Query("SELECT * FROM teacher_profiles ORDER BY id DESC")
    fun getAllTeacherProfiles(): Flow<List<TeacherProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacherProfile(profile: TeacherProfileEntity): Long

    @Delete
    suspend fun deleteTeacherProfile(profile: TeacherProfileEntity)

    // University Templates
    @Query("SELECT * FROM university_templates ORDER BY id DESC")
    fun getAllUniversityTemplates(): Flow<List<UniversityTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUniversityTemplate(template: UniversityTemplateEntity): Long

    @Delete
    suspend fun deleteUniversityTemplate(template: UniversityTemplateEntity)
}
