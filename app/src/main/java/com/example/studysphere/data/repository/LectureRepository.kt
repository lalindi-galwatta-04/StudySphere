package com.example.studysphere.data.repository

import com.example.studysphere.data.local.dao.LectureDao
import com.example.studysphere.data.local.entity.LectureEntity
import kotlinx.coroutines.flow.Flow

class LectureRepository(
    private val lectureDao: LectureDao
) {

    // Observe all lectures
    val allLectures: Flow<List<LectureEntity>> =
        lectureDao.getAllLectures()

    // Observe lectures for a particular day
    fun getLecturesByDay(day: String): Flow<List<LectureEntity>> {
        return lectureDao.getLecturesByDay(day)
    }

    // Add a lecture
    suspend fun insertLecture(lecture: LectureEntity) {
        lectureDao.insertLecture(lecture)
    }

    // Update a lecture
    suspend fun updateLecture(lecture: LectureEntity) {
        lectureDao.updateLecture(lecture)
    }

    // Delete a lecture
    suspend fun deleteLecture(lecture: LectureEntity) {
        lectureDao.deleteLecture(lecture)
    }

    // Delete all lectures
    suspend fun deleteAllLectures() {
        lectureDao.deleteAllLectures()
    }
}
