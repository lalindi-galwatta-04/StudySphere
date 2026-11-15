package com.example.studysphere.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysphere.data.local.entity.StudySessionEntity
import com.example.studysphere.ui.screens.components.StudySphereHeader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ProgressPurple = Color(0xFF8B6BCB)
private val ProgressLightPurple = Color(0xFFEAE1FA)

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel? = null,
    onMenuClick: () -> Unit = {}
) {

    val sessions = viewModel?.sessions
        ?.collectAsStateWithLifecycle()
        ?.value ?: emptyList()

    val weeklyGoal = viewModel?.weeklyGoalMinutes
        ?.collectAsStateWithLifecycle()
        ?.value ?: 1200

    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    var editingSession by remember {
        mutableStateOf<StudySessionEntity?>(null)
    }

    var deletingSession by remember {
        mutableStateOf<StudySessionEntity?>(null)
    }

    val weeklyMinutes =
        viewModel?.calculateWeeklyMinutes(sessions) ?: 0

    val totalMinutes =
        viewModel?.calculateTotalMinutes(sessions) ?: 0

    val subjectMinutes =
        viewModel?.calculateSubjectMinutes(sessions) ?: emptyMap()

    val dailyMinutes = remember(sessions) {
        val calendar = Calendar.getInstance()

        val daysFromStart =
            (calendar.get(Calendar.DAY_OF_WEEK) -
                    calendar.firstDayOfWeek + 7) % 7

        calendar.add(Calendar.DAY_OF_YEAR, -daysFromStart)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfWeek = calendar.timeInMillis

        (0..6).map { dayIndex ->
            val dayStart =
                startOfWeek + dayIndex * 24L * 60 * 60 * 1000

            val dayEnd =
                dayStart + 24L * 60 * 60 * 1000

            sessions.filter {
                it.sessionDate >= dayStart &&
                        it.sessionDate < dayEnd
            }.sumOf { it.durationMinutes }
        }
    }

    val dayLabels = remember {
        val calendar = Calendar.getInstance()
        val firstDay = calendar.firstDayOfWeek

        (0..6).map { index ->
            val dayCalendar = Calendar.getInstance()
            dayCalendar.set(Calendar.DAY_OF_WEEK, firstDay)
            dayCalendar.add(Calendar.DAY_OF_YEAR, index)

            SimpleDateFormat(
                "EE",
                Locale.getDefault()
            ).format(dayCalendar.time)
        }
    }

    Scaffold { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),

            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 90.dp
            ),

            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudySphereHeader(
                        eyebrow = "YOUR LEARNING JOURNEY",
                        title = "Study Progress 📊",
                        subtitle = "Track your learning journey.",
                        onMenuClick = onMenuClick
                    )

                }
            }

            item {
                WeeklyGoalCard(
                    weeklyMinutes = weeklyMinutes,
                    weeklyGoal = weeklyGoal,
                    onEditGoal = {
                        showGoalDialog = true
                    }
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Total Study Time",
                        value = formatDuration(totalMinutes),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "This Week",
                        value = formatDuration(weeklyMinutes),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                WeeklyChart(
                    dailyMinutes = dailyMinutes,
                    dayLabels = dayLabels
                )
            }

            item {
                SubjectBreakdown(subjectMinutes)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Study Sessions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    FilledTonalButton(
                        onClick = { showAddDialog = true },
                        contentPadding = PaddingValues(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add study session",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Add",
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            if (sessions.isEmpty()) {

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "📚",
                                style = MaterialTheme.typography.headlineLarge
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "No study sessions yet",
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                "Tap + to record your first study session.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

            } else {

                items(
                    items = sessions,
                    key = { it.id }
                ) { session ->

                    StudySessionCard(
                        session = session,
                        onEdit = {
                            editingSession = session
                        },
                        onDelete = {
                            deletingSession = session
                        }
                    )
                }
            }
        }
    }

    // Add study session dialog

    if (showAddDialog && viewModel != null) {

        StudySessionDialog(
            onDismiss = {
                showAddDialog = false
            },
            onSave = { subject, duration, notes ->

                viewModel.addSession(
                    subject,
                    duration,
                    notes
                )

                showAddDialog = false
            }
        )
    }

    // Edit study session dialog

    editingSession?.let { session ->

        StudySessionDialog(
            session = session,
            onDismiss = {
                editingSession = null
            },
            onSave = { subject, duration, notes ->

                viewModel?.updateSession(
                    session.copy(
                        subject = subject,
                        durationMinutes = duration,
                        notes = notes
                    )
                )

                editingSession = null
            }
        )
    }

    // Delete confirmation dialog

    deletingSession?.let { session ->

        AlertDialog(
            onDismissRequest = {
                deletingSession = null
            },

            title = {
                Text("Delete study session?")
            },

            text = {
                Text(
                    "This study session will be permanently deleted."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        viewModel?.deleteSession(session)
                        deletingSession = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        deletingSession = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Weekly goal editing dialog

    if (showGoalDialog && viewModel != null) {

        WeeklyGoalDialog(
            currentGoal = weeklyGoal,

            onDismiss = {
                showGoalDialog = false
            },

            onSave = { hours ->

                viewModel.updateWeeklyGoal(hours * 60)
                showGoalDialog = false
            }
        )
    }
}

@Composable
private fun WeeklyGoalCard(
    weeklyMinutes: Int,
    weeklyGoal: Int,
    onEditGoal: () -> Unit
) {

    val progress =
        (weeklyMinutes.toFloat() / weeklyGoal.coerceAtLeast(1))
            .coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProgressPurple
        )
    ) {

        Column(
            Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "WEEKLY STUDY GOAL",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = onEditGoal
                ) {
                    Text(
                        "Edit Goal",
                        color = Color.White
                    )
                }
            }

            Text(
                formatDuration(weeklyMinutes),
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                "of ${formatDuration(weeklyGoal)} goal",
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "${(progress * 100).toInt()}% completed",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            Modifier.padding(16.dp)
        ) {

            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WeeklyChart(
    dailyMinutes: List<Int>,
    dayLabels: List<String>
) {

    val maxMinutes =
        (dailyMinutes.maxOrNull() ?: 0).coerceAtLeast(60)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            Modifier.padding(18.dp)
        ) {

            Text(
                "Weekly Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Your daily study time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp),

                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {

                dailyMinutes.forEachIndexed { index, minutes ->

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        Text(
                            if (minutes > 0) formatDuration(minutes)
                            else "",

                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(Modifier.height(5.dp))

                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(
                                    (minutes.toFloat() / maxMinutes * 100f)
                                        .coerceAtLeast(5f)
                                        .dp
                                )
                                .background(
                                    if (minutes > 0) ProgressPurple
                                    else ProgressLightPurple,

                                    RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp
                                    )
                                )
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            dayLabels.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectBreakdown(
    subjectMinutes: Map<String, Int>
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                "Subject Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (subjectMinutes.isEmpty()) {

                Text(
                    "Your subject statistics will appear here.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

            } else {

                val maxMinutes =
                    subjectMinutes.values.maxOrNull()
                        ?.coerceAtLeast(1) ?: 1

                subjectMinutes.toList()
                    .sortedByDescending { it.second }
                    .forEach { (subject, minutes) ->

                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    subject,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    formatDuration(minutes),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            LinearProgressIndicator(
                                progress = {
                                    minutes.toFloat() / maxMinutes
                                },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp),

                                color = ProgressPurple,
                                trackColor = ProgressLightPurple
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun StudySessionCard(
    session: StudySessionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        ProgressLightPurple,
                        CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {
                Text("📖")
            }

            Spacer(Modifier.width(12.dp))

            Column(
                Modifier.weight(1f)
            ) {

                Text(
                    session.subject,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    formatDuration(session.durationMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                    ).format(Date(session.sessionDate)),

                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (session.notes.isNotBlank()) {

                    Text(
                        session.notes,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit session"
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete session",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun StudySessionDialog(
    session: StudySessionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, Int, String) -> Unit
) {

    var subject by remember(session) {
        mutableStateOf(session?.subject ?: "")
    }

    var duration by remember(session) {
        mutableStateOf(
            session?.durationMinutes?.toString() ?: ""
        )
    }

    var notes by remember(session) {
        mutableStateOf(session?.notes ?: "")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (session == null)
                    "Add Study Session"
                else
                    "Edit Study Session"
            )
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = subject,
                    onValueChange = {
                        subject = it
                        errorMessage = ""
                    },
                    label = { Text("Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = {
                        duration = it.filter(Char::isDigit)
                        errorMessage = ""
                    },
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotBlank()) {

                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    val minutes = duration.toIntOrNull()

                    when {

                        subject.isBlank() ->
                            errorMessage = "Please enter a subject."

                        minutes == null || minutes <= 0 ->
                            errorMessage = "Enter a valid duration in minutes."

                        else ->
                            onSave(
                                subject.trim(),
                                minutes,
                                notes.trim()
                            )
                    }
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun WeeklyGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {

    var hours by remember(currentGoal) {
        mutableStateOf((currentGoal / 60).toString())
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Set Weekly Study Goal 🎯")
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "How many hours would you like to study each week?"
                )

                OutlinedTextField(
                    value = hours,
                    onValueChange = {
                        hours = it.filter(Char::isDigit)
                        errorMessage = ""
                    },

                    label = {
                        Text("Weekly goal (hours)")
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotBlank()) {

                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    val goal = hours.toIntOrNull()

                    if (goal == null || goal !in 1..168) {

                        errorMessage =
                            "Please enter a goal between 1 and 168 hours."

                    } else {

                        onSave(goal)
                    }
                }
            ) {
                Text("Save Goal")
            }
        },

        dismissButton = {

            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDuration(minutes: Int): String {

    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return when {

        hours > 0 && remainingMinutes > 0 ->
            "${hours}h ${remainingMinutes}m"

        hours > 0 ->
            "${hours}h"

        else ->
            "${remainingMinutes}m"
    }
}
