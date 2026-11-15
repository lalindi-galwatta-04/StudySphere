package com.example.studysphere.data.repository

import com.example.studysphere.data.local.dao.NoteDao
import com.example.studysphere.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao
) {

    // Observe all notes
    val allNotes: Flow<List<NoteEntity>> =
        noteDao.getAllNotes()

    // Get notes by subject
    fun getNotesBySubject(subject: String): Flow<List<NoteEntity>> {
        return noteDao.getNotesBySubject(subject)
    }

    // Search notes
    fun searchNotes(query: String): Flow<List<NoteEntity>> {
        return noteDao.searchNotes(query)
    }

    // Insert a new note
    suspend fun insertNote(note: NoteEntity) {
        noteDao.insertNote(note)
    }

    // Update an existing note
    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    // Delete a note
    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    // Delete all notes
    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
}
