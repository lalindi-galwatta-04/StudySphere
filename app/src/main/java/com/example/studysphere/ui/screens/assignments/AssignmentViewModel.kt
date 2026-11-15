
package com.example.studysphere.ui.screens.assignments

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysphere.data.local.entity.AssignmentEntity
import com.example.studysphere.data.local.entity.ReminderEntity
import com.example.studysphere.data.preferences.NotificationPreferences
import com.example.studysphere.data.repository.AssignmentRepository
import com.example.studysphere.data.repository.ReminderRepository
import com.example.studysphere.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class AssignmentViewModel(
    private val repository: AssignmentRepository,
    private val reminderRepository: ReminderRepository,
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext
    private val notificationPreferences =
        NotificationPreferences(appContext)

    companion object {
        private const val REMINDER_HOURS_BEFORE = 24
        private const val TAG = "AssignmentReminderDebug"
    }

    val assignments: StateFlow<List<AssignmentEntity>> =
        repository.allAssignments.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Checking existing assignments")

                val existingAssignments =
                    repository.allAssignments.first()

                Log.d(
                    TAG,
                    "Existing assignments found: ${existingAssignments.size}"
                )

                existingAssignments.forEach { assignment ->
                    synchronizeAssignmentReminder(assignment)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error synchronizing existing assignments", e)
            }
        }
    }

    fun addAssignment(
        title: String,
        description: String,
        subject: String,
        dueDate: Long,
        priority: String
    ) {
        Log.d(TAG, "addAssignment() called")
        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Subject: $subject")
        Log.d(TAG, "Due date timestamp: $dueDate")

        if (title.isBlank() || subject.isBlank() || dueDate <= 0L) {
            Log.e(TAG, "Assignment validation failed")
            return
        }

        viewModelScope.launch {
            try {
                val assignment = AssignmentEntity(
                    title = title.trim(),
                    description = description.trim(),
                    subject = subject.trim(),
                    dueDate = dueDate,
                    priority = priority
                )

                repository.insertAssignment(assignment)

                Log.d(
                    TAG,
                    "Assignment saved successfully: ${assignment.id}"
                )

                synchronizeAssignmentReminder(assignment)

            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error while saving assignment or creating reminder",
                    e
                )
            }
        }
    }

    fun updateAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch {
            try {
                val updatedAssignment = assignment.copy(
                    title = assignment.title.trim(),
                    description = assignment.description.trim(),
                    subject = assignment.subject.trim(),
                    updatedAt = System.currentTimeMillis()
                )

                repository.updateAssignment(updatedAssignment)

                Log.d(
                    TAG,
                    "Assignment updated: ${updatedAssignment.id}"
                )

                synchronizeAssignmentReminder(updatedAssignment)

            } catch (e: Exception) {
                Log.e(TAG, "Error updating assignment", e)
            }
        }
    }

    fun deleteAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch {
            try {
                val linkedReminder =
                    findAssignmentReminder(assignment.id)

                if (linkedReminder != null) {
                    ReminderScheduler.cancelReminder(
                        appContext,
                        linkedReminder.id
                    )

                    reminderRepository.deleteReminder(linkedReminder)

                    Log.d(TAG, "Linked assignment reminder deleted")
                }

                repository.deleteAssignment(assignment)

                Log.d(TAG, "Assignment deleted: ${assignment.id}")

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting assignment", e)
            }
        }
    }

    private suspend fun findAssignmentReminder(
        assignmentId: String
    ): ReminderEntity? {
        return reminderRepository.allReminders.first().firstOrNull {
            it.type.equals("ASSIGNMENT", ignoreCase = true) &&
                    it.relatedItemId == assignmentId
        }
    }

    private suspend fun synchronizeAssignmentReminder(
        assignment: AssignmentEntity
    ) {
        try {
            Log.d(TAG, "--------------------------------")
            Log.d(
                TAG,
                "Synchronizing reminder for: ${assignment.title}"
            )
            Log.d(
                TAG,
                "Status: ${assignment.status}"
            )
            Log.d(
                TAG,
                "Due date timestamp: ${assignment.dueDate}"
            )

            val existingReminder =
                findAssignmentReminder(assignment.id)

            Log.d(
                TAG,
                "Existing reminder found: ${existingReminder != null}"
            )

            if (assignment.status == "COMPLETED") {
                if (existingReminder != null) {
                    ReminderScheduler.cancelReminder(
                        appContext,
                        existingReminder.id
                    )

                    reminderRepository.deleteReminder(existingReminder)

                    Log.d(
                        TAG,
                        "Reminder deleted because assignment is completed"
                    )
                }
                return
            }

            // Convert DatePicker UTC date into local midnight
            val utcCalendar =
                Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = assignment.dueDate
                }

            val year = utcCalendar.get(Calendar.YEAR)
            val month = utcCalendar.get(Calendar.MONTH)
            val day = utcCalendar.get(Calendar.DAY_OF_MONTH)

            val dueDateCalendar = Calendar.getInstance().apply {
                clear()
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val dueDateTime = dueDateCalendar.timeInMillis

            val reminderTime =
                dueDateTime - (REMINDER_HOURS_BEFORE * 60L * 60_000L)

            val currentTime = System.currentTimeMillis()

            Log.d(TAG, "Converted due date: $dueDateTime")
            Log.d(TAG, "Calculated reminder time: $reminderTime")
            Log.d(TAG, "Current time: $currentTime")

            if (reminderTime <= currentTime) {
                Log.d(
                    TAG,
                    "Reminder time has already passed. No reminder will be created."
                )

                if (existingReminder != null) {
                    ReminderScheduler.cancelReminder(
                        appContext,
                        existingReminder.id
                    )

                    reminderRepository.deleteReminder(existingReminder)
                }

                return
            }

            val description =
                "Your ${assignment.subject} assignment is due tomorrow."

            val reminder = if (existingReminder != null) {
                existingReminder.copy(
                    title = "Upcoming Assignment: ${assignment.title}",
                    description = description,
                    type = "ASSIGNMENT",
                    reminderTime = reminderTime,
                    relatedItemId = assignment.id
                )
            } else {
                ReminderEntity(
                    title = "Upcoming Assignment: ${assignment.title}",
                    description = description,
                    type = "ASSIGNMENT",
                    reminderTime = reminderTime,
                    relatedItemId = assignment.id,
                    isEnabled = true
                )
            }

            if (existingReminder == null) {
                Log.d(TAG, "Inserting new assignment reminder")

                reminderRepository.addReminder(reminder)

                Log.d(
                    TAG,
                    "Assignment reminder inserted successfully: ${reminder.id}"
                )
            } else {
                Log.d(TAG, "Updating existing assignment reminder")

                ReminderScheduler.cancelReminder(
                    appContext,
                    existingReminder.id
                )

                reminderRepository.updateReminder(reminder)

                Log.d(
                    TAG,
                    "Assignment reminder updated successfully: ${reminder.id}"
                )
            }

            val notificationsEnabled =
                notificationPreferences.areNotificationsEnabled()

            val assignmentRemindersEnabled =
                notificationPreferences.isAssignmentReminderEnabled()

            Log.d(
                TAG,
                "Notifications enabled: $notificationsEnabled"
            )

            Log.d(
                TAG,
                "Assignment reminders enabled: $assignmentRemindersEnabled"
            )

            Log.d(
                TAG,
                "Reminder enabled: ${reminder.isEnabled}"
            )

            if (
                notificationsEnabled &&
                assignmentRemindersEnabled &&
                reminder.isEnabled
            ) {
                Log.d(TAG, "Scheduling assignment notification")

                ReminderScheduler.scheduleReminder(
                    context = appContext,
                    reminderId = reminder.id,
                    title = reminder.title,
                    description = reminder.description,
                    reminderTime = reminder.reminderTime
                )

                Log.d(TAG, "Assignment notification scheduling requested")
            } else {
                Log.d(
                    TAG,
                    "Notification scheduling skipped because a setting is disabled"
                )
            }

        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error inside synchronizeAssignmentReminder()",
                e
            )
        }
    }

    fun getAssignmentsByStatus(status: String) =
        repository.getAssignmentsByStatus(status)

    fun getAssignmentsBySubject(subject: String) =
        repository.getAssignmentsBySubject(subject)
}
