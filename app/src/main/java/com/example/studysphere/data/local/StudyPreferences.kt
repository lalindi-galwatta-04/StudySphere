
package com.example.studysphere.data.local

import android.content.Context

class StudyPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "study_preferences",
        Context.MODE_PRIVATE
    )

    // WEEKLY STUDY GOAL

    fun getWeeklyGoal(): Int {
        return preferences.getInt("weekly_goal_minutes", 20 * 60)
    }

    fun saveWeeklyGoal(minutes: Int) {
        preferences.edit()
            .putInt("weekly_goal_minutes", minutes)
            .apply()
    }

    // POMODORO FOCUS DURATION

    fun getFocusDuration(): Int {
        return preferences.getInt("focus_duration", 25)
    }

    fun saveFocusDuration(minutes: Int) {
        preferences.edit()
            .putInt("focus_duration", minutes)
            .apply()
    }

    // SHORT BREAK DURATION

    fun getShortBreakDuration(): Int {
        return preferences.getInt("short_break_duration", 5)
    }

    fun saveShortBreakDuration(minutes: Int) {
        preferences.edit()
            .putInt("short_break_duration", minutes)
            .apply()
    }

    // LONG BREAK DURATION

    fun getLongBreakDuration(): Int {
        return preferences.getInt("long_break_duration", 15)
    }

    fun saveLongBreakDuration(minutes: Int) {
        preferences.edit()
            .putInt("long_break_duration", minutes)
            .apply()
    }

    // LONG BREAK INTERVAL

    fun getLongBreakInterval(): Int {
        return preferences.getInt("long_break_interval", 4)
    }

    fun saveLongBreakInterval(sessions: Int) {
        preferences.edit()
            .putInt("long_break_interval", sessions)
            .apply()
    }
}
