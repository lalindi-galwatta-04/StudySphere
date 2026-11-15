
package com.example.studysphere.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysphere.data.local.StudyPreferences
import com.example.studysphere.data.repository.StudySessionRepository

class ProgressViewModelFactory(
    private val repository: StudySessionRepository,
    private val preferences: StudyPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(repository, preferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
