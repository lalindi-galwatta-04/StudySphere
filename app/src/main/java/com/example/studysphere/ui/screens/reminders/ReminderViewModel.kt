
package com.example.studysphere.ui.screens.reminders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysphere.data.local.entity.ReminderEntity
import com.example.studysphere.data.preferences.NotificationPreferences
import com.example.studysphere.data.repository.ReminderRepository
import com.example.studysphere.notifications.ReminderNotificationManager
import com.example.studysphere.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository,
    private val context: Context
) : ViewModel() {

    private val appContext = context.applicationContext

    private val notificationPreferences =
        NotificationPreferences(appContext)

    val reminders: StateFlow<List<ReminderEntity>> =
        repository.allReminders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val enabledReminders: StateFlow<List<ReminderEntity>> =
        repository.enabledReminders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        ReminderNotificationManager.createNotificationChannel(appContext)
    }

    // --------------------------------------------------
    // ADD REMINDER
    // --------------------------------------------------

    fun addReminder(
        title: String,
        description: String,
        type: String,
        reminderTime: Long,
        relatedItemId: String? = null
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title,
                description = description,
                type = type,
                reminderTime = reminderTime,
                relatedItemId = relatedItemId
            )

            repository.addReminder(reminder)
            scheduleIfEnabled(reminder)
        }
    }

    // --------------------------------------------------
    // UPDATE REMINDER
    // --------------------------------------------------

    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(
                appContext,
                reminder.id
            )

            repository.updateReminder(reminder)
            scheduleIfEnabled(reminder)
        }
    }

    // --------------------------------------------------
    // DELETE REMINDER
    // --------------------------------------------------

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(
                appContext,
                reminder.id
            )

            repository.deleteReminder(reminder)
        }
    }

    fun deleteReminderById(id: String) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(
                appContext,
                id
            )

            repository.deleteReminderById(id)
        }
    }

    // --------------------------------------------------
    // TOGGLE INDIVIDUAL REMINDER
    // --------------------------------------------------

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updatedReminder = reminder.copy(
                isEnabled = !reminder.isEnabled
            )

            repository.updateReminder(updatedReminder)

            if (updatedReminder.isEnabled) {
                scheduleIfEnabled(updatedReminder)
            } else {
                ReminderScheduler.cancelReminder(
                    appContext,
                    reminder.id
                )
            }
        }
    }

    // --------------------------------------------------
    // NOTIFICATION PREFERENCE ACCESS
    // --------------------------------------------------

    fun areNotificationsEnabled(): Boolean {
        return notificationPreferences.areNotificationsEnabled()
    }

    fun isAssignmentReminderEnabled(): Boolean {
        return notificationPreferences.isAssignmentReminderEnabled()
    }

    fun isLectureReminderEnabled(): Boolean {
        return notificationPreferences.isLectureReminderEnabled()
    }

    fun isStudyReminderEnabled(): Boolean {
        return notificationPreferences.isStudyReminderEnabled()
    }

    fun isExamReminderEnabled(): Boolean {
        return notificationPreferences.isExamReminderEnabled()
    }

    // --------------------------------------------------
    // MASTER NOTIFICATION SWITCH
    // --------------------------------------------------

    fun setNotificationsEnabled(enabled: Boolean) {
        notificationPreferences.setNotificationsEnabled(enabled)

        viewModelScope.launch {
            val allReminders = repository.allReminders.first()

            allReminders.forEach { reminder ->
                if (enabled) {
                    scheduleIfEnabled(reminder)
                } else {
                    ReminderScheduler.cancelReminder(
                        appContext,
                        reminder.id
                    )
                }
            }
        }
    }

    // --------------------------------------------------
    // ASSIGNMENT REMINDER SWITCH
    // --------------------------------------------------

    fun setAssignmentRemindersEnabled(enabled: Boolean) {
        notificationPreferences.setAssignmentReminderEnabled(enabled)

        updateCategoryReminders(
            type = "ASSIGNMENT",
            enabled = enabled
        )
    }

    // --------------------------------------------------
    // LECTURE REMINDER SWITCH
    // --------------------------------------------------

    fun setLectureRemindersEnabled(enabled: Boolean) {
        notificationPreferences.setLectureReminderEnabled(enabled)

        updateCategoryReminders(
            type = "LECTURE",
            enabled = enabled
        )
    }

    // --------------------------------------------------
    // STUDY REMINDER SWITCH
    // --------------------------------------------------

    fun setStudyRemindersEnabled(enabled: Boolean) {
        notificationPreferences.setStudyReminderEnabled(enabled)

        updateCategoryReminders(
            type = "CUSTOM",
            enabled = enabled
        )
    }

    // --------------------------------------------------
    // EXAM REMINDER SWITCH
    // --------------------------------------------------

    fun setExamRemindersEnabled(enabled: Boolean) {
        notificationPreferences.setExamReminderEnabled(enabled)

        updateCategoryReminders(
            type = "EXAM",
            enabled = enabled
        )
    }

    // --------------------------------------------------
    // UPDATE CATEGORY REMINDERS
    // --------------------------------------------------

    private fun updateCategoryReminders(
        type: String,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            val categoryReminders = repository.allReminders
                .first()
                .filter { it.type.equals(type, ignoreCase = true) }

            categoryReminders.forEach { reminder ->
                if (enabled) {
                    scheduleIfEnabled(reminder)
                } else {
                    ReminderScheduler.cancelReminder(
                        appContext,
                        reminder.id
                    )
                }
            }
        }
    }

    // --------------------------------------------------
    // CHECK WHETHER A CATEGORY IS ALLOWED
    // --------------------------------------------------

    private fun isCategoryEnabled(type: String): Boolean {

        if (!notificationPreferences.areNotificationsEnabled()) {
            return false
        }

        return when (type.uppercase()) {

            "ASSIGNMENT" ->
                notificationPreferences.isAssignmentReminderEnabled()

            "LECTURE" ->
                notificationPreferences.isLectureReminderEnabled()

            "CUSTOM" ->
                notificationPreferences.isStudyReminderEnabled()

            "EXAM" ->
                notificationPreferences.isExamReminderEnabled()

            else -> false
        }
    }

    // --------------------------------------------------
    // SCHEDULE REMINDER
    // --------------------------------------------------

    private fun scheduleIfEnabled(reminder: ReminderEntity) {

        if (!reminder.isEnabled) return

        if (!isCategoryEnabled(reminder.type)) return

        if (reminder.reminderTime <= System.currentTimeMillis()) {
            return
        }

        ReminderScheduler.scheduleReminder(
            context = appContext,
            reminderId = reminder.id,
            title = reminder.title,
            description = reminder.description.ifBlank {
                "It's time for your scheduled study activity!"
            },
            reminderTime = reminder.reminderTime
        )
    }
}
