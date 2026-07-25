package com.example.data.repository

import com.example.data.dao.CoverPageDao
import com.example.data.model.CoverPageEntity
import com.example.data.model.StudentProfileEntity
import com.example.data.model.TeacherProfileEntity
import com.example.data.model.UniversityTemplateEntity
import kotlinx.coroutines.flow.Flow

class CoverPageRepository(private val dao: CoverPageDao) {
    val allCoverPages: Flow<List<CoverPageEntity>> = dao.getAllCoverPages()
    val allStudentProfiles: Flow<List<StudentProfileEntity>> = dao.getAllStudentProfiles()
    val allTeacherProfiles: Flow<List<TeacherProfileEntity>> = dao.getAllTeacherProfiles()
    val allUniversityTemplates: Flow<List<UniversityTemplateEntity>> = dao.getAllUniversityTemplates()

    suspend fun getCoverPageById(id: Long): CoverPageEntity? = dao.getCoverPageById(id)

    suspend fun saveCoverPage(coverPage: CoverPageEntity): Long {
        return if (coverPage.id == 0L) {
            dao.insertCoverPage(coverPage)
        } else {
            dao.updateCoverPage(coverPage.copy(updatedAt = System.currentTimeMillis()))
            coverPage.id
        }
    }

    suspend fun deleteCoverPage(coverPage: CoverPageEntity) {
        dao.deleteCoverPage(coverPage)
    }

    suspend fun deleteCoverPageById(id: Long) {
        dao.deleteCoverPageById(id)
    }

    suspend fun getDefaultStudentProfile(): StudentProfileEntity? = dao.getDefaultStudentProfile()

    suspend fun saveStudentProfile(profile: StudentProfileEntity): Long {
        return dao.insertStudentProfile(profile)
    }

    suspend fun deleteStudentProfile(profile: StudentProfileEntity) {
        dao.deleteStudentProfile(profile)
    }

    suspend fun saveTeacherProfile(profile: TeacherProfileEntity): Long {
        return dao.insertTeacherProfile(profile)
    }

    suspend fun deleteTeacherProfile(profile: TeacherProfileEntity) {
        dao.deleteTeacherProfile(profile)
    }

    suspend fun saveUniversityTemplate(template: UniversityTemplateEntity): Long {
        return dao.insertUniversityTemplate(template)
    }

    suspend fun deleteUniversityTemplate(template: UniversityTemplateEntity) {
        dao.deleteUniversityTemplate(template)
    }
}
