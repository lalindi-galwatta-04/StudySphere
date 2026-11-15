package com.example.studysphere.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.studysphere.data.local.entity.AssignmentEntity
import com.example.studysphere.data.local.entity.ExamEntity
import com.example.studysphere.data.local.entity.LectureEntity
import com.example.studysphere.ui.screens.components.StudySphereHeader
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val CalendarLavender = Color(0xFF8B72C7)
private val CalendarLightLavender = Color(0xFFF0EBFA)
private val LectureDot = Color(0xFF5C9DCE)
private val AssignmentDot = Color(0xFFE5A34B)
private val ExamDot = Color(0xFFD95C65)

// Recognizes both abbreviated and full weekday names.
private fun isSameWeekday(
    storedDay: String,
    calendarDay: String
): Boolean {
    return storedDay.trim()
        .take(3)
        .equals(calendarDay.trim().take(3), ignoreCase = true)
}

private fun timestampToCalendarDate(timestamp: Long): LocalDate {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

@Composable
fun CalendarScreen(
    lectures: List<LectureEntity> = emptyList(),
    assignments: List<AssignmentEntity> = emptyList(),
    exams: List<ExamEntity> = emptyList(),
    onMenuClick: () -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val today = LocalDate.now()
    val firstDay = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val leadingDays = firstDay.dayOfWeek.value - 1

    val calendarDays: List<LocalDate?> =
        List(leadingDays) { null } +
                (1..daysInMonth).map { currentMonth.atDay(it) }

    val selectedDayName = selectedDate.dayOfWeek.getDisplayName(
        TextStyle.FULL,
        Locale.ENGLISH
    )

    val selectedDayLectures = lectures
        .filter { isSameWeekday(it.day, selectedDayName) }
        .sortedBy { it.startTime }

    val selectedDayAssignments = assignments
        .filter { timestampToCalendarDate(it.dueDate) == selectedDate }
        .sortedBy { it.dueDate }

    val selectedDayExams = exams
        .filter { timestampToCalendarDate(it.examDate) == selectedDate }
        .sortedWith(compareBy<ExamEntity> { it.startTime }.thenBy { it.title })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        StudySphereHeader(
            eyebrow = "YOUR STUDY OVERVIEW",
            title = "Calendar 🗓️",
            subtitle = "Keep track of your study schedule.",
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            currentMonth = currentMonth.minusMonths(1)
                            selectedDate = currentMonth.atDay(1)
                        }
                    ) {
                        Text("‹", fontSize = 28.sp, color = CalendarLavender)
                    }

                    Text(
                        text = currentMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        ) + " ${currentMonth.year}",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TextButton(
                        onClick = {
                            currentMonth = currentMonth.plusMonths(1)
                            selectedDate = currentMonth.atDay(1)
                        }
                    ) {
                        Text("›", fontSize = 28.sp, color = CalendarLavender)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        .forEach { day ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(270.dp),
                    userScrollEnabled = false
                ) {
                    items(calendarDays) { date ->
                        val dayName = date?.dayOfWeek?.getDisplayName(
                            TextStyle.FULL,
                            Locale.ENGLISH
                        )

                        val hasLecture = date != null &&
                                dayName != null &&
                                lectures.any { isSameWeekday(it.day, dayName) }

                        val hasAssignment = date != null &&
                                assignments.any {
                                    timestampToCalendarDate(it.dueDate) == date
                                }

                        val hasExam = date != null &&
                                exams.any {
                                    timestampToCalendarDate(it.examDate) == date
                                }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    color = when {
                                        date == selectedDate -> CalendarLavender
                                        date == today -> CalendarLightLavender
                                        else -> Color.Transparent
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (date != null) {
                                TextButton(
                                    onClick = { selectedDate = date },
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            color = when {
                                                date == selectedDate -> Color.White
                                                date == today -> CalendarLavender
                                                else -> MaterialTheme.colorScheme.onSurface
                                            },
                                            fontWeight = if (
                                                date == selectedDate || date == today
                                            ) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )

                                        if (hasLecture || hasAssignment || hasExam) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (hasLecture) {
                                                    CalendarDot(
                                                        color = if (date == selectedDate) Color.White else LectureDot
                                                    )
                                                }
                                                if (hasAssignment) {
                                                    CalendarDot(
                                                        color = if (date == selectedDate) Color.White else AssignmentDot
                                                    )
                                                }
                                                if (hasExam) {
                                                    CalendarDot(
                                                        color = if (date == selectedDate) Color.White else ExamDot
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Event Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = LectureDot, label = "Lectures")
                    Spacer(modifier = Modifier.width(12.dp))
                    LegendItem(color = AssignmentDot, label = "Assignments")
                    Spacer(modifier = Modifier.width(12.dp))
                    LegendItem(color = ExamDot, label = "Exams")
                }

                TextButton(
                    onClick = {
                        currentMonth = YearMonth.now()
                        selectedDate = LocalDate.now()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Today",
                        color = CalendarLavender,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = selectedDate.format(
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
            ),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "Daily Agenda",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Lectures",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LectureDot
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedDayLectures.isEmpty()) {
                    Text(
                        text = "No lectures scheduled for this day.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    selectedDayLectures.forEach { lecture ->
                        AgendaEvent(
                            title = lecture.subject,
                            subtitle = "${lecture.startTime} - ${lecture.endTime}",
                            extra = lecture.location,
                            dotColor = LectureDot
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Assignment Deadlines",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AssignmentDot
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedDayAssignments.isEmpty()) {
                    Text(
                        text = "No assignment deadlines for this day.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    selectedDayAssignments.forEach { assignment ->
                        AgendaEvent(
                            title = assignment.title,
                            subtitle = "Subject: ${assignment.subject}",
                            extra = "Priority: ${assignment.priority}",
                            dotColor = AssignmentDot
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Exams",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ExamDot
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedDayExams.isEmpty()) {
                    Text(
                        text = "No exams scheduled for this day.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    selectedDayExams.forEach { exam ->
                        val timeText = when {
                            exam.startTime.isNotBlank() && exam.endTime.isNotBlank() ->
                                "${exam.startTime} - ${exam.endTime}"
                            exam.startTime.isNotBlank() -> exam.startTime
                            else -> "Time not specified"
                        }

                        AgendaEvent(
                            title = exam.title,
                            subtitle = "Subject: ${exam.subject} • $timeText",
                            extra = exam.venue,
                            dotColor = ExamDot
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun CalendarDot(color: Color) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(4.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AgendaEvent(
    title: String,
    subtitle: String,
    extra: String,
    dotColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .background(dotColor, CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (extra.isNotBlank()) {
                Text(
                    text = extra,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    MaterialTheme {
        CalendarScreen()
    }
}
