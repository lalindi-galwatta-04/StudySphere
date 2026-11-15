
package com.example.studysphere.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.studysphere.data.local.dao.AssignmentDao
import com.example.studysphere.data.local.dao.ExamDao
import com.example.studysphere.data.local.dao.LectureDao
import com.example.studysphere.data.local.dao.NoteDao
import com.example.studysphere.data.local.dao.StudySessionDao
import com.example.studysphere.data.local.dao.ReminderDao

import com.example.studysphere.data.local.entity.AssignmentEntity
import com.example.studysphere.data.local.entity.ExamEntity
import com.example.studysphere.data.local.entity.LectureEntity
import com.example.studysphere.data.local.entity.NoteEntity
import com.example.studysphere.data.local.entity.StudySessionEntity
import com.example.studysphere.data.local.entity.ReminderEntity

@Database(
    entities = [
        LectureEntity::class,
        NoteEntity::class,
        StudySessionEntity::class,
        AssignmentEntity::class,
        ReminderEntity::class,
        ExamEntity::class
    ],
    version = 6,
    exportSchema = false
)

abstract class StudySphereDatabase : RoomDatabase() {

    abstract fun lectureDao(): LectureDao
    abstract fun noteDao(): NoteDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun reminderDao(): ReminderDao
    abstract fun examDao(): ExamDao

    companion object {

        @Volatile
        private var INSTANCE: StudySphereDatabase? = null

        // Migration from version 1 to version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS notes (
                        id TEXT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        subject TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        // Migration from version 2 to version 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS study_sessions (
                        id TEXT NOT NULL,
                        subject TEXT NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        sessionDate INTEGER NOT NULL,
                        notes TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        // Migration from version 3 to version 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS assignments (
                        id TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        subject TEXT NOT NULL,
                        dueDate INTEGER NOT NULL,
                        priority TEXT NOT NULL,
                        status TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        // Migration from version 4 to version 5
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS reminders (
                        id TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        type TEXT NOT NULL,
                        reminderTime INTEGER NOT NULL,
                        relatedItemId TEXT,
                        isEnabled INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        // Migration from version 5 to version 6
        // Adds the exams table without deleting existing data.
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS exams (
                        id TEXT NOT NULL,
                        subject TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        examDate INTEGER NOT NULL,
                        startTime TEXT NOT NULL,
                        endTime TEXT NOT NULL,
                        venue TEXT NOT NULL,
                        notes TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): StudySphereDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudySphereDatabase::class.java,
                    "studysphere_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
