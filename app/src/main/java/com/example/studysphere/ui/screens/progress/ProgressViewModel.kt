
package com.example.studysphere.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysphere.data.local.StudyPreferences
import com.example.studysphere.data.local.entity.StudySessionEntity
import com.example.studysphere.data.repository.StudySessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class ProgressViewModel(
    private val repository: StudySessionRepository,
    private val preferences: StudyPreferences
) : ViewModel() {

    val sessions: StateFlow<List<StudySessionEntity>> =
        repository.allSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _weeklyGoalMinutes =
        MutableStateFlow(preferences.getWeeklyGoal())

    val weeklyGoalMinutes: StateFlow<Int> = _weeklyGoalMinutes

    fun updateWeeklyGoal(minutes: Int) {
        if (minutes <= 0) return

        preferences.saveWeeklyGoal(minutes)
        _weeklyGoalMinutes.value = minutes
    }

    fun calculateTotalMinutes(sessions: List<StudySessionEntity>): Int =
        sessions.sumOf { it.durationMinutes }

    fun calculateTotalHours(sessions: List<StudySessionEntity>): Double =
        calculateTotalMinutes(sessions) / 60.0

    fun calculateWeeklyMinutes(sessions: List<StudySessionEntity>): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val weekStart = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, 7)
        val weekEnd = calendar.timeInMillis

        return sessions
            .filter {
                it.sessionDate >= weekStart && it.sessionDate < weekEnd
            }
            .sumOf { it.durationMinutes }
    }

    fun calculateSubjectMinutes(
        sessions: List<StudySessionEntity>
    ): Map<String, Int> =
        sessions.groupBy { it.subject }
            .mapValues { (_, subjectSessions) ->
                subjectSessions.sumOf { it.durationMinutes }
            }

    fun addSession(
        subject: String,
        durationMinutes: Int,
        notes: String = ""
    ) {
        viewModelScope.launch {
            if (subject.isBlank() || durationMinutes <= 0) return@launch

            val session = StudySessionEntity(
                subject = subject.trim(),
                durationMinutes = durationMinutes,
                notes = notes.trim()
            )

            repository.insertSession(session)
        }
    }

    fun updateSession(session: StudySessionEntity) {
        viewModelScope.launch {
            repository.updateSession(session)
        }
    }

    fun deleteSession(session: StudySessionEntity) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}
