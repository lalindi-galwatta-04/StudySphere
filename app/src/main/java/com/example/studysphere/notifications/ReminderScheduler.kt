package com.example.studysphere.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

object ReminderScheduler {

    private const val ACTION_REMINDER =
        "com.example.studysphere.ACTION_REMINDER"

    fun scheduleReminder(
        context: Context,
        reminderId: String,
        title: String,
        description: String,
        reminderTime: Long
    ) {

        // Do not schedule reminders in the past
        if (reminderTime <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val pendingIntent = createPendingIntent(
            context = context,
            reminderId = reminderId,
            title = title,
            description = description
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                } else {
                    // Fallback when exact-alarm access is unavailable
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                }

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
                )

            } else {

                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
                )
            }

        } catch (exception: SecurityException) {
            // Fall back to an inexact alarm if exact access is denied
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        }
    }

    fun cancelReminder(
        context: Context,
        reminderId: String
    ) {
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(
            context,
            ReminderReceiver::class.java
        ).apply {
            action = ACTION_REMINDER
            data = Uri.parse("studysphere://reminder/$reminderId")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_NO_CREATE or
                    PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun createPendingIntent(
        context: Context,
        reminderId: String,
        title: String,
        description: String
    ): PendingIntent {

        val intent = Intent(
            context,
            ReminderReceiver::class.java
        ).apply {
            action = ACTION_REMINDER
            data = Uri.parse("studysphere://reminder/$reminderId")

            putExtra(
                ReminderReceiver.EXTRA_REMINDER_ID,
                reminderId
            )

            putExtra(
                ReminderReceiver.EXTRA_TITLE,
                title
            )

            putExtra(
                ReminderReceiver.EXTRA_DESCRIPTION,
                description
            )
        }

        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }
}
