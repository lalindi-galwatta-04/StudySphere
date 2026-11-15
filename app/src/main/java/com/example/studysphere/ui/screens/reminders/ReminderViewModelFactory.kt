
package com.example.studysphere.ui.screens.reminders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysphere.data.repository.ReminderRepository

class ReminderViewModelFactory(
    private val context: Context,
    private val repository: ReminderRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(
                repository = repository,
                context = context.applicationContext
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
