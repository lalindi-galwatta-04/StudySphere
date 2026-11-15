package com.example.studysphere.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studysphere.data.local.entity.LectureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureDao {

    // Retrieve all lectures, ordered by day and start time
    @Query("SELECT * FROM lectures ORDER BY day, startTime")
    fun getAllLectures(): Flow<List<LectureEntity>>

    // Retrieve lectures for a specific day
    @Query("SELECT * FROM lectures WHERE day = :day ORDER BY startTime")
    fun getLecturesByDay(day: String): Flow<List<LectureEntity>>

    // Insert a new lecture
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: LectureEntity)

    // Update an existing lecture
    @Update
    suspend fun updateLecture(lecture: LectureEntity)

    // Delete a lecture
    @Delete
    suspend fun deleteLecture(lecture: LectureEntity)

    // Delete all lectures
    @Query("DELETE FROM lectures")
    suspend fun deleteAllLectures()
}
