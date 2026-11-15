
package com.example.studysphere.ui.screens.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysphere.data.local.StudyPreferences
import com.example.studysphere.data.local.entity.StudySessionEntity
import com.example.studysphere.data.repository.StudySessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PomodoroMode {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK
}

class PomodoroViewModel(
    private val repository: StudySessionRepository,
    private val studyPreferences: StudyPreferences
) : ViewModel() {

    // TIMER STATE

    private val _mode = MutableStateFlow(PomodoroMode.FOCUS)
    val mode: StateFlow<PomodoroMode> = _mode.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(
        getDurationSeconds(PomodoroMode.FOCUS)
    )
    val remainingSeconds: StateFlow<Int> =
        _remainingSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _completedFocusSessions = MutableStateFlow(0)
    val completedFocusSessions: StateFlow<Int> =
        _completedFocusSessions.asStateFlow()

    private val _selectedSubject = MutableStateFlow("")
    val selectedSubject: StateFlow<String> =
        _selectedSubject.asStateFlow()

    private var timerJob: Job? = null

    // DURATION OF THE CURRENT SESSION

    private var currentSessionDurationSeconds =
        getDurationSeconds(PomodoroMode.FOCUS)

    // SELECT STUDY SUBJECT

    fun selectSubject(subject: String) {
        if (!_isRunning.value) {
            _selectedSubject.value = subject
        }
    }

    // GET DURATION FOR EACH MODE

    fun getDurationSeconds(mode: PomodoroMode): Int {
        val minutes = when (mode) {
            PomodoroMode.FOCUS ->
                studyPreferences.getFocusDuration()

            PomodoroMode.SHORT_BREAK ->
                studyPreferences.getShortBreakDuration()

            PomodoroMode.LONG_BREAK ->
                studyPreferences.getLongBreakDuration()
        }

        return minutes.coerceAtLeast(1) * 60
    }

    // REFRESH CURRENT DURATION FROM SETTINGS

    fun refreshDuration() {
        if (!_isRunning.value) {
            currentSessionDurationSeconds =
                getDurationSeconds(_mode.value)

            _remainingSeconds.value = currentSessionDurationSeconds
        }
    }

    // START TIMER

    fun startTimer() {

        if (_isRunning.value) return

        if (
            _mode.value == PomodoroMode.FOCUS &&
            _selectedSubject.value.isBlank()
        ) return

        // Keep the duration fixed for this session.
        if (_remainingSeconds.value <= 0) {
            _remainingSeconds.value = getDurationSeconds(_mode.value)
        }

        currentSessionDurationSeconds =
            getDurationSeconds(_mode.value)

        _isRunning.value = true

        timerJob = viewModelScope.launch {

            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }

            _isRunning.value = false
            timerJob = null

            onTimerCompleted()
        }
    }

    // PAUSE TIMER

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _isRunning.value = false
    }

    // RESET TIMER

    fun resetTimer() {
        pauseTimer()

        currentSessionDurationSeconds =
            getDurationSeconds(_mode.value)

        _remainingSeconds.value = currentSessionDurationSeconds
    }

    // SELECT TIMER MODE

    fun selectMode(newMode: PomodoroMode) {
        pauseTimer()

        _mode.value = newMode

        currentSessionDurationSeconds =
            getDurationSeconds(newMode)

        _remainingSeconds.value = currentSessionDurationSeconds
    }

    // HANDLE TIMER COMPLETION

    private fun onTimerCompleted() {

        when (_mode.value) {

            PomodoroMode.FOCUS -> {

                val subject = _selectedSubject.value.trim()

                if (subject.isNotBlank()) {

                    _completedFocusSessions.value += 1

                    val completedDuration =
                        currentSessionDurationSeconds / 60

                    viewModelScope.launch {
                        repository.insertSession(
                            StudySessionEntity(
                                subject = subject,
                                durationMinutes = completedDuration,
                                notes = "Completed Pomodoro focus session"
                            )
                        )
                    }
                }

                // Determine whether it is time for a long break.

                val longBreakInterval =
                    studyPreferences.getLongBreakInterval()
                        .coerceAtLeast(1)

                val nextMode =
                    if (
                        _completedFocusSessions.value > 0 &&
                        _completedFocusSessions.value % longBreakInterval == 0
                    ) {
                        PomodoroMode.LONG_BREAK
                    } else {
                        PomodoroMode.SHORT_BREAK
                    }

                _mode.value = nextMode

                currentSessionDurationSeconds =
                    getDurationSeconds(nextMode)

                _remainingSeconds.value = currentSessionDurationSeconds

                // Automatically start the next break.

                startTimer()
            }

            PomodoroMode.SHORT_BREAK,
            PomodoroMode.LONG_BREAK -> {

                _mode.value = PomodoroMode.FOCUS

                currentSessionDurationSeconds =
                    getDurationSeconds(PomodoroMode.FOCUS)

                _remainingSeconds.value = currentSessionDurationSeconds

                // Automatically start the next focus session.

                startTimer()
            }
        }
    }

    // CLEAN UP TIMER

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
