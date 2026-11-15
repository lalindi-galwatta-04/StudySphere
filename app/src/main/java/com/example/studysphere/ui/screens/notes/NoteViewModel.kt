package com.example.studysphere.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysphere.data.local.entity.NoteEntity
import com.example.studysphere.data.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    // Observe all notes
    val notes: StateFlow<List<NoteEntity>> =
        repository.allNotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Add a new note
    fun addNote(
        title: String,
        content: String,
        subject: String
    ) {
        viewModelScope.launch {

            val note = NoteEntity(
                title = title.trim(),
                content = content.trim(),
                subject = subject.trim()
            )

            repository.insertNote(note)
        }
    }

    // Update an existing note
    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {

            val updatedNote = note.copy(
                updatedAt = System.currentTimeMillis()
            )

            repository.updateNote(updatedNote)
        }
    }

    // Delete a note
    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Search notes
    fun searchNotes(query: String) =
        repository.searchNotes(query)

    // Get notes by subject
    fun getNotesBySubject(subject: String) =
        repository.getNotesBySubject(subject)
}
