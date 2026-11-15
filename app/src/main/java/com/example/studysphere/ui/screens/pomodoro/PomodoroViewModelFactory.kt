
package com.example.studysphere.ui.screens.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysphere.data.local.StudyPreferences
import com.example.studysphere.data.repository.StudySessionRepository

class PomodoroViewModelFactory(
    private val repository: StudySessionRepository,
    private val studyPreferences: StudyPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PomodoroViewModel(
                repository,
                studyPreferences
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
