package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CoverPageDao
import com.example.data.model.CoverPageEntity
import com.example.data.model.StudentProfileEntity
import com.example.data.model.TeacherProfileEntity
import com.example.data.model.UniversityTemplateEntity

@Database(
    entities = [
        CoverPageEntity::class,
        StudentProfileEntity::class,
        TeacherProfileEntity::class,
        UniversityTemplateEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coverPageDao(): CoverPageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cover_page_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
