
package com.example.studysphere.ui.screens.exams

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysphere.data.repository.ExamRepository
import com.example.studysphere.data.repository.ReminderRepository

class ExamViewModelFactory(
    private val examRepository: ExamRepository,
    private val reminderRepository: ReminderRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ExamViewModel::class.java)) {
            return ExamViewModel(
                repository = examRepository,
                reminderRepository = reminderRepository,
                context = context.applicationContext
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
