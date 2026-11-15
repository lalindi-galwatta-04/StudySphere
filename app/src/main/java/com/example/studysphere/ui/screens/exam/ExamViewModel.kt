
package com.example.studysphere.ui.screens.exams

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysphere.data.local.entity.ExamEntity
import com.example.studysphere.data.local.entity.ReminderEntity
import com.example.studysphere.data.preferences.NotificationPreferences
import com.example.studysphere.data.repository.ExamRepository
import com.example.studysphere.data.repository.ReminderRepository
import com.example.studysphere.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ExamViewModel(
    private val repository: ExamRepository,
    private val reminderRepository: ReminderRepository,
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext

    private val notificationPreferences =
        NotificationPreferences(appContext)

    companion object {
        private const val REMINDER_MINUTES_BEFORE = 1440
    }

    val exams: StateFlow<List<ExamEntity>> =
        repository.allExams.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            val existingExams = repository.allExams.first()

            existingExams.forEach { exam ->
                synchronizeExamReminder(exam)
            }
        }
    }

    // --------------------------------------------------
    // ADD EXAM
    // --------------------------------------------------

    fun addExam(
        subject: String,
        title: String,
        description: String,
        examDate: Long,
        startTime: String,
        endTime: String,
        venue: String,
        notes: String
    ) {
        if (
            subject.isBlank() ||
            title.isBlank() ||
            examDate <= 0L
        ) return

        viewModelScope.launch {

            val exam = ExamEntity(
                subject = subject.trim(),
                title = title.trim(),
                description = description.trim(),
                examDate = examDate,
                startTime = startTime.trim(),
                endTime = endTime.trim(),
                venue = venue.trim(),
                notes = notes.trim()
            )

            repository.insertExam(exam)

            synchronizeExamReminder(exam)
        }
    }

    // --------------------------------------------------
    // UPDATE EXAM
    // --------------------------------------------------

    fun updateExam(exam: ExamEntity) {

        if (
            exam.subject.isBlank() ||
            exam.title.isBlank() ||
            exam.examDate <= 0L
        ) return

        viewModelScope.launch {

            val updatedExam = exam.copy(
                subject = exam.subject.trim(),
                title = exam.title.trim(),
                description = exam.description.trim(),
                startTime = exam.startTime.trim(),
                endTime = exam.endTime.trim(),
                venue = exam.venue.trim(),
                notes = exam.notes.trim(),
                updatedAt = System.currentTimeMillis()
            )

            repository.updateExam(updatedExam)

            synchronizeExamReminder(updatedExam)
        }
    }

    // --------------------------------------------------
    // DELETE EXAM
    // --------------------------------------------------

    fun deleteExam(exam: ExamEntity) {

        viewModelScope.launch {

            val linkedReminder = findExamReminder(exam.id)

            if (linkedReminder != null) {

                ReminderScheduler.cancelReminder(
                    appContext,
                    linkedReminder.id
                )

                reminderRepository.deleteReminder(linkedReminder)
            }

            repository.deleteExam(exam)
        }
    }

    // --------------------------------------------------
    // FIND LINKED EXAM REMINDER
    // --------------------------------------------------

    private suspend fun findExamReminder(
        examId: String
    ): ReminderEntity? {

        return reminderRepository.allReminders
            .first()
            .firstOrNull {
                it.type.equals("EXAM", ignoreCase = true) &&
                        it.relatedItemId == examId
            }
    }

    // --------------------------------------------------
    // AUTOMATIC EXAM REMINDER
    // --------------------------------------------------

    private suspend fun synchronizeExamReminder(
        exam: ExamEntity
    ) {

        val examStartTime = calculateExamStartTime(exam)

        val existingReminder = findExamReminder(exam.id)

        // A valid examination start time is required.

        if (examStartTime == null) {

            if (existingReminder != null) {

                ReminderScheduler.cancelReminder(
                    appContext,
                    existingReminder.id
                )

                reminderRepository.deleteReminder(existingReminder)
            }

            return
        }

        // Calculate reminder time: 24 hours before exam.

        val reminderTime = examStartTime -
                (REMINDER_MINUTES_BEFORE * 60_000L)

        // Do not retain reminders whose scheduled time has passed.

        if (reminderTime <= System.currentTimeMillis()) {

            if (existingReminder != null) {

                ReminderScheduler.cancelReminder(
                    appContext,
                    existingReminder.id
                )

                reminderRepository.deleteReminder(existingReminder)
            }

            return
        }

        val description =
            "Your ${exam.subject} examination is tomorrow."

        // Create or update the reminder.

        val reminder = if (existingReminder != null) {

            existingReminder.copy(
                title = "Upcoming Exam: ${exam.title}",
                description = description,
                type = "EXAM",
                reminderTime = reminderTime,
                relatedItemId = exam.id
            )

        } else {

            ReminderEntity(
                title = "Upcoming Exam: ${exam.title}",
                description = description,
                type = "EXAM",
                reminderTime = reminderTime,
                relatedItemId = exam.id,
                isEnabled = true
            )
        }

        if (existingReminder == null) {

            reminderRepository.addReminder(reminder)

        } else {

            ReminderScheduler.cancelReminder(
                appContext,
                existingReminder.id
            )

            reminderRepository.updateReminder(reminder)
        }

        // Schedule only when both notification settings are enabled.

        if (
            notificationPreferences.areNotificationsEnabled() &&
            notificationPreferences.isExamReminderEnabled() &&
            reminder.isEnabled
        ) {

            ReminderScheduler.scheduleReminder(
                context = appContext,
                reminderId = reminder.id,
                title = reminder.title,
                description = reminder.description,
                reminderTime = reminder.reminderTime
            )
        }
    }

    // --------------------------------------------------
    // CALCULATE EXAM START TIME
    // --------------------------------------------------

    private fun calculateExamStartTime(
        exam: ExamEntity
    ): Long? {

        if (exam.startTime.isBlank()) return null

        // Material 3 DatePicker stores the selected date
        // as UTC midnight. Extract its date components in UTC.

        val utcCalendar = Calendar.getInstance(
            TimeZone.getTimeZone("UTC")
        ).apply {
            timeInMillis = exam.examDate
        }

        val year = utcCalendar.get(Calendar.YEAR)
        val month = utcCalendar.get(Calendar.MONTH)
        val day = utcCalendar.get(Calendar.DAY_OF_MONTH)

        // Construct the examination date in local time.

        val examCalendar = Calendar.getInstance().apply {
            clear()

            set(
                year,
                month,
                day,
                0,
                0,
                0
            )

            set(Calendar.MILLISECOND, 0)
        }

        // Supported time formats.

        val timePatterns = listOf(
            "h:mm a",
            "hh:mm a",
            "HH:mm"
        )

        var parsedTime: java.util.Date? = null

        for (pattern in timePatterns) {

            try {

                val formatter = SimpleDateFormat(
                    pattern,
                    Locale.getDefault()
                ).apply {
                    isLenient = false
                }

                parsedTime = formatter.parse(
                    exam.startTime.trim()
                )

                if (parsedTime != null) break

            } catch (_: Exception) {
                // Try the next supported time format.
            }
        }

        if (parsedTime == null) return null

        // Extract the parsed hour and minute.

        val parsedCalendar = Calendar.getInstance().apply {
            time = parsedTime!!
        }

        examCalendar.set(
            Calendar.HOUR_OF_DAY,
            parsedCalendar.get(Calendar.HOUR_OF_DAY)
        )

        examCalendar.set(
            Calendar.MINUTE,
            parsedCalendar.get(Calendar.MINUTE)
        )

        examCalendar.set(Calendar.SECOND, 0)
        examCalendar.set(Calendar.MILLISECOND, 0)

        return examCalendar.timeInMillis
    }

    // --------------------------------------------------
    // GET EXAMS
    // --------------------------------------------------

    fun getExamsBySubject(subject: String) =
        repository.getExamsBySubject(subject)

    fun getExamsByDate(date: Long) =
        repository.getExamsByDate(date)
}
