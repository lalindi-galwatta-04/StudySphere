package com.example.studysphere.data.repository

import com.example.studysphere.data.local.dao.ExamDao
import com.example.studysphere.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

class ExamRepository(
    private val examDao: ExamDao
) {

    val allExams: Flow<List<ExamEntity>> =
        examDao.getAllExams()

    suspend fun getExamById(id: String): ExamEntity? {
        return examDao.getExamById(id)
    }

    fun getExamsBySubject(
        subject: String
    ): Flow<List<ExamEntity>> {
        return examDao.getExamsBySubject(subject)
    }

    fun getExamsByDate(
        date: Long
    ): Flow<List<ExamEntity>> {
        return examDao.getExamsByDate(date)
    }

    suspend fun insertExam(exam: ExamEntity) {
        examDao.insertExam(exam)
    }

    suspend fun updateExam(exam: ExamEntity) {
        examDao.updateExam(exam)
    }

    suspend fun deleteExam(exam: ExamEntity) {
        examDao.deleteExam(exam)
    }

    suspend fun deleteAllExams() {
        examDao.deleteAllExams()
    }
}
