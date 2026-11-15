package com.example.studysphere.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reminders")
data class ReminderEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val title: String,

    val description: String = "",

    // CUSTOM, ASSIGNMENT, or LECTURE
    val type: String = "CUSTOM",

    // Reminder trigger time in milliseconds
    val reminderTime: Long,

    // Optional related assignment or lecture ID
    val relatedItemId: String? = null,

    val isEnabled: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)
