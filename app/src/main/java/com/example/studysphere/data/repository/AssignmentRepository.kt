package com.example.studysphere.data.repository

import com.example.studysphere.data.local.dao.AssignmentDao
import com.example.studysphere.data.local.entity.AssignmentEntity
import kotlinx.coroutines.flow.Flow

class AssignmentRepository(
    private val assignmentDao: AssignmentDao
) {

    val allAssignments: Flow<List<AssignmentEntity>> =
        assignmentDao.getAllAssignments()

    fun getAssignmentsByStatus(
        status: String
    ): Flow<List<AssignmentEntity>> {
        return assignmentDao.getAssignmentsByStatus(status)
    }

    fun getAssignmentsBySubject(
        subject: String
    ): Flow<List<AssignmentEntity>> {
        return assignmentDao.getAssignmentsBySubject(subject)
    }

    suspend fun getAssignmentById(
        id: String
    ): AssignmentEntity? {
        return assignmentDao.getAssignmentById(id)
    }

    suspend fun insertAssignment(
        assignment: AssignmentEntity
    ) {
        assignmentDao.insertAssignment(assignment)
    }

    suspend fun updateAssignment(
        assignment: AssignmentEntity
    ) {
        assignmentDao.updateAssignment(assignment)
    }

    suspend fun deleteAssignment(
        assignment: AssignmentEntity
    ) {
        assignmentDao.deleteAssignment(assignment)
    }

    suspend fun deleteAllAssignments() {
        assignmentDao.deleteAllAssignments()
    }
}
