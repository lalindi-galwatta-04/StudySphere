
package com.example.studysphere.data.preferences

import android.content.Context

class NotificationPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "studysphere_notification_preferences",
        Context.MODE_PRIVATE
    )

    fun areNotificationsEnabled(): Boolean {
        return preferences.getBoolean(KEY_MASTER, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_MASTER, enabled)
            .apply()
    }

    fun isAssignmentReminderEnabled(): Boolean {
        return preferences.getBoolean(KEY_ASSIGNMENTS, true)
    }

    fun setAssignmentReminderEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_ASSIGNMENTS, enabled)
            .apply()
    }

    fun isLectureReminderEnabled(): Boolean {
        return preferences.getBoolean(KEY_LECTURES, true)
    }

    fun setLectureReminderEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_LECTURES, enabled)
            .apply()
    }

    fun isStudyReminderEnabled(): Boolean {
        return preferences.getBoolean(KEY_STUDY, true)
    }

    fun setStudyReminderEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_STUDY, enabled)
            .apply()
    }

    // Exam reminder preferences
    fun isExamReminderEnabled(): Boolean {
        return preferences.getBoolean(KEY_EXAMS, true)
    }

    fun setExamReminderEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_EXAMS, enabled)
            .apply()
    }

    companion object {
        private const val KEY_MASTER = "notifications_enabled"
        private const val KEY_ASSIGNMENTS = "assignment_reminders_enabled"
        private const val KEY_LECTURES = "lecture_reminders_enabled"
        private const val KEY_STUDY = "study_reminders_enabled"
        private const val KEY_EXAMS = "exam_reminders_enabled"
    }
}
