package com.example.studysphere.ui.theme

import android.content.Context

class ThemePreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "studysphere_theme_preferences",
        Context.MODE_PRIVATE
    )

    fun saveThemeMode(themeMode: AppThemeMode) {
        preferences.edit()
            .putString(KEY_THEME_MODE, themeMode.name)
            .apply()
    }

    fun getThemeMode(): AppThemeMode {
        val savedMode = preferences.getString(
            KEY_THEME_MODE,
            AppThemeMode.SYSTEM.name
        )

        return try {
            AppThemeMode.valueOf(
                savedMode ?: AppThemeMode.SYSTEM.name
            )
        } catch (exception: IllegalArgumentException) {
            AppThemeMode.SYSTEM
        }
    }

    companion object {
        private const val KEY_THEME_MODE = "selected_theme_mode"
    }
}
