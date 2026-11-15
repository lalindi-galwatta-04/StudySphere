package com.example.studysphere.data.preferences

import android.content.Context
import com.example.studysphere.ui.screens.profile.StudentProfile

class ProfilePreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "studysphere_profile_preferences",
        Context.MODE_PRIVATE
    )

    fun saveProfile(profile: StudentProfile) {
        preferences.edit()
            .putString(KEY_NAME, profile.name)
            .putString(KEY_UNIVERSITY, profile.university)
            .putString(KEY_DEGREE, profile.degree)
            .putString(KEY_ACADEMIC_YEAR, profile.academicYear)
            .putString(KEY_SEMESTER, profile.semester)
            .putBoolean(KEY_PROFILE_EXISTS, true)
            .apply()
    }

    fun getProfile(): StudentProfile? {

        if (!preferences.getBoolean(KEY_PROFILE_EXISTS, false)) {
            return null
        }

        return StudentProfile(
            name = preferences.getString(KEY_NAME, "") ?: "",
            university = preferences.getString(KEY_UNIVERSITY, "") ?: "",
            degree = preferences.getString(KEY_DEGREE, "") ?: "",
            academicYear = preferences.getString(KEY_ACADEMIC_YEAR, "") ?: "",
            semester = preferences.getString(KEY_SEMESTER, "") ?: ""
        )
    }

    fun clearProfile() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_NAME = "student_name"
        private const val KEY_UNIVERSITY = "student_university"
        private const val KEY_DEGREE = "student_degree"
        private const val KEY_ACADEMIC_YEAR = "academic_year"
        private const val KEY_SEMESTER = "semester"
        private const val KEY_PROFILE_EXISTS = "profile_exists"
    }
}
