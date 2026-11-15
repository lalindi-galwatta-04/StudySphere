package com.example.studysphere.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "exams")
data class ExamEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val subject: String,

    val title: String,

    val description: String = "",

    val examDate: Long,

    val startTime: String = "",

    val endTime: String = "",

    val venue: String = "",

    val notes: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
