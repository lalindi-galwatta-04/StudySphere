package com.example.studysphere.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID)
            ?: return

        val title = intent.getStringExtra(EXTRA_TITLE)
            ?: "Study Reminder"

        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
            ?: "It's time for your scheduled study activity!"

        // Ensure the notification channel exists
        ReminderNotificationManager.createNotificationChannel(context)

        // Display the reminder notification
        ReminderNotificationManager.showNotification(
            context = context,
            reminderId = reminderId,
            title = title,
            description = description
        )
    }

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_TITLE = "reminder_title"
        const val EXTRA_DESCRIPTION = "reminder_description"
    }
}
