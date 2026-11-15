package com.example.studysphere.data.repository

import com.example.studysphere.data.local.dao.StudySessionDao
import com.example.studysphere.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

class StudySessionRepository(
    private val studySessionDao: StudySessionDao
) {

    // Observe all study sessions
    val allSessions: Flow<List<StudySessionEntity>> =
        studySessionDao.getAllSessions()

    // Observe sessions for a particular subject
    fun getSessionsBySubject(
        subject: String
    ): Flow<List<StudySessionEntity>> {
        return studySessionDao.getSessionsBySubject(subject)
    }

    // Observe sessions within a date range
    fun getSessionsBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<List<StudySessionEntity>> {
        return studySessionDao.getSessionsBetweenDates(
            startDate,
            endDate
        )
    }

    // Add a study session
    suspend fun insertSession(
        session: StudySessionEntity
    ) {
        studySessionDao.insertSession(session)
    }

    // Update a study session
    suspend fun updateSession(
        session: StudySessionEntity
    ) {
        studySessionDao.updateSession(session)
    }

    // Delete a study session
    suspend fun deleteSession(
        session: StudySessionEntity
    ) {
        studySessionDao.deleteSession(session)
    }

    // Delete all study sessions
    suspend fun deleteAllSessions() {
        studySessionDao.deleteAllSessions()
    }
}
