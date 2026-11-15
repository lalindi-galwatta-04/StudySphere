
package com.example.studysphere.ui.screens.assignments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysphere.data.repository.AssignmentRepository
import com.example.studysphere.data.repository.ReminderRepository

class AssignmentViewModelFactory(
    private val repository: AssignmentRepository,
    private val reminderRepository: ReminderRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(AssignmentViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return AssignmentViewModel(
                repository = repository,
                reminderRepository = reminderRepository,
                context = context
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
