package com.example.studysphere.data.repository

import com.example.studysphere.data.local.dao.ReminderDao
import com.example.studysphere.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val reminderDao: ReminderDao
) {

    val allReminders: Flow<List<ReminderEntity>> =
        reminderDao.getAllReminders()

    val enabledReminders: Flow<List<ReminderEntity>> =
        reminderDao.getEnabledReminders()

    fun getRemindersByType(type: String): Flow<List<ReminderEntity>> {
        return reminderDao.getRemindersByType(type)
    }

    suspend fun getReminderById(id: String): ReminderEntity? {
        return reminderDao.getReminderById(id)
    }

    suspend fun addReminder(reminder: ReminderEntity) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: ReminderEntity) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: ReminderEntity) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun deleteReminderById(id: String) {
        reminderDao.deleteReminderById(id)
    }

    suspend fun deleteAllReminders() {
        reminderDao.deleteAllReminders()
    }
}
