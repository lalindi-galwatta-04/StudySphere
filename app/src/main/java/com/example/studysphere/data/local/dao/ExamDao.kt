package com.example.studysphere.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studysphere.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Query("SELECT * FROM exams ORDER BY examDate ASC, startTime ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getExamById(id: String): ExamEntity?

    @Query("SELECT * FROM exams WHERE subject = :subject ORDER BY examDate ASC")
    fun getExamsBySubject(subject: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE examDate = :date ORDER BY startTime ASC")
    fun getExamsByDate(date: Long): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    @Query("DELETE FROM exams")
    suspend fun deleteAllExams()
}
