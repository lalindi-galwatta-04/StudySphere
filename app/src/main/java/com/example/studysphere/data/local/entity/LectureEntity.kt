package com.example.studysphere.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "lectures")
data class LectureEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val day: String,

    val subject: String,

    val startTime: String,

    val endTime: String,

    val location: String
)
