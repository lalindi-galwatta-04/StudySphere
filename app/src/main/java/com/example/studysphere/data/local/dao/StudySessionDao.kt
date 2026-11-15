package com.example.studysphere.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studysphere.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    // Retrieve all study sessions
    @Query("SELECT * FROM study_sessions ORDER BY sessionDate DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    // Retrieve sessions for a particular subject
    @Query("""
        SELECT * FROM study_sessions
        WHERE subject = :subject
        ORDER BY sessionDate DESC
    """)
    fun getSessionsBySubject(
        subject: String
    ): Flow<List<StudySessionEntity>>

    // Retrieve sessions within a date range
    @Query("""
        SELECT * FROM study_sessions
        WHERE sessionDate BETWEEN :startDate AND :endDate
        ORDER BY sessionDate DESC
    """)
    fun getSessionsBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<List<StudySessionEntity>>

    // Insert a study session
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(
        session: StudySessionEntity
    )

    // Update a study session
    @Update
    suspend fun updateSession(
        session: StudySessionEntity
    )

    // Delete a study session
    @Delete
    suspend fun deleteSession(
        session: StudySessionEntity
    )

    // Delete all study sessions
    @Query("DELETE FROM study_sessions")
    suspend fun deleteAllSessions()
}
