package com.example.studysphere.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "study_sessions")
data class StudySessionEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val subject: String,

    val durationMinutes: Int,

    val sessionDate: Long = System.currentTimeMillis(),

    val notes: String = ""
)
