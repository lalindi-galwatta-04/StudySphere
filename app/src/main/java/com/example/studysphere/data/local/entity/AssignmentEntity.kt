package com.example.studysphere.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "assignments")
data class AssignmentEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val title: String,

    val description: String = "",

    val subject: String,

    val dueDate: Long,

    val priority: String = "MEDIUM",

    val status: String = "PENDING",

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
