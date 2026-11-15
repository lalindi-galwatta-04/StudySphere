
package com.example.studysphere.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studysphere.data.local.StudySphereDatabase
import com.example.studysphere.data.preferences.NotificationPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val preferences = NotificationPreferences(appContext)

                if (!preferences.areNotificationsEnabled()) {
                    return@launch
                }

                val database = StudySphereDatabase.getDatabase(appContext)

                val reminders = database.reminderDao()
                    .getEnabledReminders()
                    .first()

                reminders.forEach { reminder ->

                    if (reminder.reminderTime <= System.currentTimeMillis()) {
                        return@forEach
                    }

                    val categoryEnabled = when (reminder.type.uppercase()) {

                        "ASSIGNMENT" ->
                            preferences.isAssignmentReminderEnabled()

                        "LECTURE" ->
                            preferences.isLectureReminderEnabled()

                        "CUSTOM" ->
                            preferences.isStudyReminderEnabled()

                        "EXAM" ->
                            preferences.isExamReminderEnabled()

                        else -> false
                    }

                    if (categoryEnabled) {

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

            } finally {
                pendingResult.finish()
            }
        }
    }
}
