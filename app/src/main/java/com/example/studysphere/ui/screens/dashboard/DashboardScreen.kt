package com.example.studysphere.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studysphere.data.local.entity.*
import com.example.studysphere.ui.screens.profile.StudentProfile
import com.example.studysphere.ui.theme.StudySphereTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    profile: StudentProfile,
    lectures: List<LectureEntity> = emptyList(),
    assignments: List<AssignmentEntity> = emptyList(),
    studySessions: List<StudySessionEntity> = emptyList(),
    weeklyGoalMinutes: Int = 1200,
    reminders: List<ReminderEntity> = emptyList(),
    onMenuClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val today = Calendar.getInstance()

    val todayName = SimpleDateFormat(
        "EEE",
        Locale.US
    ).format(today.time)

    val todayLectures = lectures
        .filter {
            it.day.take(3).equals(todayName, ignoreCase = true)
        }
        .sortedBy {
            parseDashboardTime(it.startTime)
        }

    val pendingAssignments = assignments.filter {
        !it.status.equals("COMPLETED", ignoreCase = true)
    }

    val upcomingAssignments = pendingAssignments
        .sortedBy { it.dueDate }
        .take(3)

    val weeklyMinutes = calculateDashboardWeeklyMinutes(
        studySessions
    )

    val safeGoal = weeklyGoalMinutes.coerceAtLeast(1)

    val progress = (
            weeklyMinutes.toFloat() / safeGoal
            ).coerceIn(0f, 1f)

    val upcomingReminders = reminders
        .filter {
            it.isEnabled &&
                    it.reminderTime > System.currentTimeMillis()
        }
        .sortedBy { it.reminderTime }
        .take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(44.dp)
            ) {
                Text(
                    text = "☰",
                    fontSize = 28.sp,
                    color = colors.onBackground
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "YOUR ACADEMIC SPACE",
                    color = colors.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hello, ${profile.name.substringBefore(" ")}! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )

                Text(
                    text = "Ready for a productive day?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onBackground.copy(alpha = 0.65f)
                )
            }

            Surface(
                shape = CircleShape,
                color = colors.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = profile.name.firstOrNull()
                            ?.uppercase() ?: "S",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // UNIVERSITY INFORMATION
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = colors.primaryContainer
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🎓", fontSize = 25.sp)
                        }
                    }

                    Column {
                        Text(
                            "YOUR UNIVERSITY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer.copy(alpha = 0.7f)
                        )

                        Text(
                            profile.university,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer
                        )
                    }
                }

                HorizontalDivider(
                    color = colors.onPrimaryContainer.copy(alpha = 0.15f)
                )

                Text(
                    profile.degree,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = colors.onPrimaryContainer
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            profile.academicYear,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            profile.semester,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // TODAY'S OVERVIEW
        SectionHeading("Today's Overview")

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                modifier = Modifier.weight(1f),
                emoji = "📚",
                number = todayLectures.size.toString(),
                label = "Classes Today"
            )

            OverviewCard(
                modifier = Modifier.weight(1f),
                emoji = "📝",
                number = pendingAssignments.size.toString(),
                label = "Pending Tasks"
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // TODAY'S SCHEDULE
        SectionHeading("Today's Schedule")

        Spacer(modifier = Modifier.height(12.dp))

        if (todayLectures.isEmpty()) {
            EmptyDashboardMessage(
                "No lectures scheduled for today. Enjoy your free time! 💜"
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                todayLectures.forEach { lecture ->
                    ScheduleCard(
                        time = "${lecture.startTime} - ${lecture.endTime}",
                        subject = lecture.subject,
                        location = lecture.location
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // UPCOMING ASSIGNMENTS
        SectionHeading("Upcoming Assignments")

        Spacer(modifier = Modifier.height(12.dp))

        if (upcomingAssignments.isEmpty()) {
            EmptyDashboardMessage(
                "No pending assignments. Great job! 🎉"
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                upcomingAssignments.forEach { assignment ->
                    AssignmentCard(
                        title = assignment.title,
                        subject = assignment.subject,
                        deadline = formatDashboardDate(assignment.dueDate),
                        status = assignment.status
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // WEEKLY STUDY PROGRESS
        SectionHeading("Weekly Study Progress")

        Spacer(modifier = Modifier.height(12.dp))

        StudyProgressCard(
            weeklyMinutes = weeklyMinutes,
            goalMinutes = safeGoal,
            progress = progress
        )

        Spacer(modifier = Modifier.height(28.dp))

        // UPCOMING REMINDERS
        SectionHeading("Upcoming Reminders")

        Spacer(modifier = Modifier.height(12.dp))

        if (upcomingReminders.isEmpty()) {
            EmptyDashboardMessage(
                "No upcoming enabled reminders."
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                upcomingReminders.forEach { reminder ->
                    ReminderDashboardCard(reminder)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // FOOTER
        Text(
            text = "Made for your academic journey 💜",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = colors.onBackground.copy(alpha = 0.55f)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// SECTION HEADING
@Composable
fun SectionHeading(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

// OVERVIEW CARD
@Composable
fun OverviewCard(
    modifier: Modifier = Modifier,
    emoji: String,
    number: String,
    label: String
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = number,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}

// SCHEDULE CARD
@Composable
fun ScheduleCard(
    time: String,
    subject: String,
    location: String
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colors.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = time,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = subject,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ASSIGNMENT CARD
@Composable
fun AssignmentCard(
    title: String,
    subject: String,
    deadline: String,
    status: String
) {
    val colors = MaterialTheme.colorScheme

    val normalizedStatus = status
        .uppercase()
        .replace(" ", "_")

    val isOverdue = normalizedStatus != "COMPLETED" &&
            try {
                val dueDate = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).parse(deadline)

                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                dueDate != null && dueDate.before(today)
            } catch (e: Exception) {
                false
            }

    val badgeText = when {
        normalizedStatus == "COMPLETED" -> "Completed"
        isOverdue -> "Overdue"
        normalizedStatus == "IN_PROGRESS" -> "In Progress"
        else -> "Pending"
    }

    val badgeBackground = when (badgeText) {
        "Completed" -> colors.tertiaryContainer
        "Overdue" -> colors.errorContainer
        "In Progress" -> colors.primaryContainer
        else -> colors.secondaryContainer
    }

    val badgeTextColor = when (badgeText) {
        "Completed" -> colors.onTertiaryContainer
        "Overdue" -> colors.onErrorContainer
        "In Progress" -> colors.onPrimaryContainer
        else -> colors.onSecondaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onSurfaceVariant
            )

            Text(
                text = subject,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due: $deadline",
                    color = colors.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = badgeBackground
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeTextColor
                    )
                }
            }
        }
    }
}

// STUDY PROGRESS CARD

@Composable
fun StudyProgressCard(
    weeklyMinutes: Int,
    goalMinutes: Int,
    progress: Float
) {
    val colors = MaterialTheme.colorScheme

    val studiedHours = weeklyMinutes / 60f
    val goalHours = goalMinutes / 60f

    val remainingMinutes = (goalMinutes - weeklyMinutes)
        .coerceAtLeast(0)

    val remainingHours = remainingMinutes / 60f

    // Display progress up to 100%, while preserving actual hours.
    val displayedProgress = progress.coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // CIRCULAR PROGRESS INDICATOR
            Box(
                modifier = Modifier.size(112.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { displayedProgress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 9.dp,
                    color = colors.primary,
                    trackColor = colors.outline.copy(alpha = 0.2f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(displayedProgress * 100).toInt()}%",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant
                    )

                    Text(
                        text = "COMPLETED",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = colors.onSurfaceVariant.copy(alpha = 0.65f)
                    )
                }
            }

            // STUDY INFORMATION
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "YOUR WEEKLY GOAL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )

                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%.1f hrs",
                        studiedHours
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurfaceVariant
                )

                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "of %.1f hours",
                        goalHours
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant.copy(alpha = 0.75f)
                )

                Text(
                    text = if (remainingMinutes > 0) {
                        String.format(
                            Locale.getDefault(),
                            "%.1f hours remaining",
                            remainingHours
                        )
                    } else {
                        "Weekly goal achieved! 🎉"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )
            }
        }
    }
}


// REMINDER CARD
@Composable
fun ReminderDashboardCard(reminder: ReminderEntity) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.primaryContainer,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🔔",
                            fontSize = 24.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant
                    )

                    if (reminder.description.isNotBlank()) {
                        Text(
                            text = reminder.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = colors.onSurfaceVariant.copy(alpha = 0.12f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📅",
                    fontSize = 18.sp
                )

                Text(
                    text = SimpleDateFormat(
                        "dd MMM yyyy • hh:mm a",
                        Locale.getDefault()
                    ).format(Date(reminder.reminderTime)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )
            }
        }
    }
}

// EMPTY STATE
@Composable
fun EmptyDashboardMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// HELPER FUNCTIONS

private fun parseDashboardTime(value: String): String {
    return try {
        val formatter = SimpleDateFormat(
            "hh:mm a",
            Locale.US
        )

        val date = formatter.parse(value)

        SimpleDateFormat(
            "HH:mm",
            Locale.US
        ).format(date!!)
    } catch (e: Exception) {
        value
    }
}

private fun formatDashboardDate(timestamp: Long): String {
    return SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(Date(timestamp))
}

private fun calculateDashboardWeeklyMinutes(
    sessions: List<StudySessionEntity>
): Int {
    val calendar = Calendar.getInstance()

    calendar.set(
        Calendar.DAY_OF_WEEK,
        calendar.firstDayOfWeek
    )

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val weekStart = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_WEEK, 7)

    val weekEnd = calendar.timeInMillis

    return sessions
        .filter {
            it.sessionDate >= weekStart &&
                    it.sessionDate < weekEnd
        }
        .sumOf {
            it.durationMinutes
        }
}

// LIGHT PREVIEW
@Preview(
    name = "Dashboard - Light",
    showBackground = true
)
@Composable
fun DashboardLightPreview() {
    StudySphereTheme(darkTheme = false) {
        DashboardScreen(
            profile = StudentProfile(
                name = "Alex Morgan",
                university = "University of Colombo",
                degree = "BSc in Computer Science",
                academicYear = "Year 2",
                semester = "Semester 1"
            )
        )
    }
}

// DARK PREVIEW
@Preview(
    name = "Dashboard - Dark",
    showBackground = true
)
@Composable
fun DashboardDarkPreview() {
    StudySphereTheme(darkTheme = true) {
        DashboardScreen(
            profile = StudentProfile(
                name = "Alex Morgan",
                university = "University of Colombo",
                degree = "BSc in Computer Science",
                academicYear = "Year 2",
                semester = "Semester 1"
            )
        )
    }
}
