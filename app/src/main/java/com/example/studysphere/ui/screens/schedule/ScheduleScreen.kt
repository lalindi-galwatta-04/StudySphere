package com.example.studysphere.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studysphere.data.local.entity.LectureEntity
import com.example.studysphere.ui.screens.components.StudySphereHeader
import com.example.studysphere.ui.theme.StudySphereTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

private val timeFormatter =
    DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

private fun parseTime(value: String): LocalTime? {
    return try {
        LocalTime.parse(value.trim().uppercase(Locale.US), timeFormatter)
    } catch (e: Exception) {
        null
    }
}

data class Lecture(
    val id: String = UUID.randomUUID().toString(),
    val day: String,
    val subject: String,
    val startTime: String,
    val endTime: String,
    val location: String
)

private fun LectureEntity.toLecture(): Lecture {
    return Lecture(
        id = id,
        day = day,
        subject = subject,
        startTime = startTime,
        endTime = endTime,
        location = location
    )
}

private fun Lecture.toEntity(): LectureEntity {
    return LectureEntity(
        id = id,
        day = day,
        subject = subject,
        startTime = startTime,
        endTime = endTime,
        location = location
    )
}

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel? = null,
    onMenuClick: () -> Unit = {}
) {

    val colors = MaterialTheme.colorScheme

    val lectureFlow = viewModel?.lectures
        ?: remember {
            MutableStateFlow(emptyList())
        }

    val databaseLectures by lectureFlow.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel?.initializeSampleLectures()
    }

    val previewLectures = remember {
        listOf(
            Lecture(
                day = "Mon",
                subject = "Data Structures",
                startTime = "09:00 AM",
                endTime = "11:00 AM",
                location = "Lecture Hall 01"
            ),
            Lecture(
                day = "Mon",
                subject = "Database Systems",
                startTime = "11:30 AM",
                endTime = "01:30 PM",
                location = "Lecture Hall 03"
            ),
            Lecture(
                day = "Tue",
                subject = "Software Engineering",
                startTime = "09:00 AM",
                endTime = "11:00 AM",
                location = "Computer Lab"
            ),
            Lecture(
                day = "Wed",
                subject = "Object Oriented Programming",
                startTime = "10:00 AM",
                endTime = "12:00 PM",
                location = "Lecture Hall 02"
            ),
            Lecture(
                day = "Thu",
                subject = "Information Security",
                startTime = "01:00 PM",
                endTime = "03:00 PM",
                location = "Lecture Hall 04"
            ),
            Lecture(
                day = "Fri",
                subject = "Mobile Application Development",
                startTime = "09:00 AM",
                endTime = "11:00 AM",
                location = "Computer Lab"
            )
        )
    }

    val lectures = if (viewModel == null) {
        previewLectures
    } else {
        databaseLectures.map { it.toLecture() }
    }

    val days = listOf(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    )

    var selectedDay by remember {
        mutableStateOf("Mon")
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var editingLecture by remember {
        mutableStateOf<Lecture?>(null)
    }

    var deletingLecture by remember {
        mutableStateOf<Lecture?>(null)
    }

    val dailyLectures = lectures
        .filter { it.day == selectedDay }
        .sortedBy {
            parseTime(it.startTime) ?: LocalTime.MAX
        }

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

        // SHARED HEADER

        StudySphereHeader(
            eyebrow = "YOUR ACADEMIC PLANNER",
            title = "My Schedule 📅",
            subtitle = "Organize your university week.",
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        // DAY SELECTOR

        Text(
            text = "Select Day",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            days.forEach { day ->

                val isSelected = selectedDay == day

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedDay = day
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) {
                        colors.primary
                    } else {
                        colors.surfaceVariant
                    }
                ) {
                    Box(
                        modifier = Modifier.padding(
                            vertical = 15.dp,
                            horizontal = 2.dp
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) {
                                colors.onPrimary
                            } else {
                                colors.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // DAILY CLASSES

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedDay's Classes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Text(
                text = "${dailyLectures.size} Classes",
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (dailyLectures.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("☀️", fontSize = 42.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No classes scheduled!",
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant
                    )

                    Text(
                        text = "Enjoy your free day or add a new class.",
                        color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        } else {

            dailyLectures.forEach { lecture ->

                LectureCard(
                    lecture = lecture,
                    onEdit = {
                        editingLecture = lecture
                    },
                    onDelete = {
                        deletingLecture = lecture
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ADD BUTTON

        Button(
            onClick = {
                showAddDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "+  Add New Lecture",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // ADD LECTURE DIALOG

    if (showAddDialog) {

        LectureFormDialog(
            title = "Add New Lecture 📚",
            selectedDay = selectedDay,
            existingLectures = lectures,
            onDismiss = {
                showAddDialog = false
            },
            onSave = { lecture ->
                viewModel?.addLecture(lecture.toEntity())
                showAddDialog = false
            }
        )
    }

    // EDIT LECTURE DIALOG

    editingLecture?.let { lecture ->

        LectureFormDialog(
            title = "Edit Lecture ✏️",
            selectedDay = lecture.day,
            initialLecture = lecture,
            existingLectures = lectures,
            onDismiss = {
                editingLecture = null
            },
            onSave = { updatedLecture ->

                viewModel?.updateLecture(updatedLecture.toEntity())

                editingLecture = null
            }
        )
    }

    // DELETE CONFIRMATION

    deletingLecture?.let { lecture ->

        AlertDialog(
            onDismissRequest = {
                deletingLecture = null
            },
            title = {
                Text("Delete Lecture?")
            },
            text = {
                Text(
                    "Are you sure you want to delete " +
                            "${lecture.subject}? This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel?.deleteLecture(lecture.toEntity())
                        deletingLecture = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = colors.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deletingLecture = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


// --------------------------------------------------
// LECTURE CARD
// --------------------------------------------------

@Composable
fun LectureCard(
    lecture: Lecture,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.primaryContainer
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = lecture.startTime,
                            color = colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Text(
                            text = "to ${lecture.endTime}",
                            color = colors.onPrimaryContainer,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = lecture.subject,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "📍 ${lecture.location}",
                        color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = colors.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = onEdit) {
                    Text(
                        "✏️ Edit",
                        color = colors.primary
                    )
                }

                TextButton(onClick = onDelete) {
                    Text(
                        "🗑 Delete",
                        color = colors.error
                    )
                }
            }
        }
    }
}


// --------------------------------------------------
// ADD AND EDIT FORM
// --------------------------------------------------

@Composable
fun LectureFormDialog(
    title: String,
    selectedDay: String,
    existingLectures: List<Lecture>,
    initialLecture: Lecture? = null,
    onDismiss: () -> Unit,
    onSave: (Lecture) -> Unit
) {

    var subject by remember(initialLecture?.id) {
        mutableStateOf(initialLecture?.subject ?: "")
    }

    var startTime by remember(initialLecture?.id) {
        mutableStateOf(initialLecture?.startTime ?: "")
    }

    var endTime by remember(initialLecture?.id) {
        mutableStateOf(initialLecture?.endTime ?: "")
    }

    var location by remember(initialLecture?.id) {
        mutableStateOf(initialLecture?.location ?: "")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Day: $selectedDay",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = {
                        subject = it
                        errorMessage = ""
                    },
                    label = { Text("Subject Name") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = startTime,
                    onValueChange = {
                        startTime = it
                        errorMessage = ""
                    },
                    label = { Text("Start Time (09:00 AM)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = {
                        endTime = it
                        errorMessage = ""
                    },
                    label = { Text("End Time (11:00 AM)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        errorMessage = ""
                    },
                    label = { Text("Lecture Hall / Location") },
                    singleLine = true
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {

                    val parsedStart = parseTime(startTime)
                    val parsedEnd = parseTime(endTime)

                    when {

                        subject.isBlank() ||
                                startTime.isBlank() ||
                                endTime.isBlank() ||
                                location.isBlank() -> {

                            errorMessage = "Please complete all fields."
                        }

                        parsedStart == null || parsedEnd == null -> {

                            errorMessage =
                                "Use the time format HH:MM AM/PM."
                        }

                        !parsedEnd.isAfter(parsedStart) -> {

                            errorMessage =
                                "End time must be after start time."
                        }

                        else -> {

                            val hasOverlap = existingLectures.any { existing ->

                                if (
                                    existing.day != selectedDay ||
                                    existing.id == initialLecture?.id
                                ) {
                                    false
                                } else {

                                    val existingStart =
                                        parseTime(existing.startTime)

                                    val existingEnd =
                                        parseTime(existing.endTime)

                                    existingStart != null &&
                                            existingEnd != null &&
                                            parsedStart.isBefore(existingEnd) &&
                                            parsedEnd.isAfter(existingStart)
                                }
                            }

                            if (hasOverlap) {

                                errorMessage =
                                    "This lecture overlaps with another class."

                            } else {

                                onSave(
                                    Lecture(
                                        id = initialLecture?.id
                                            ?: UUID.randomUUID().toString(),
                                        day = selectedDay,
                                        subject = subject.trim(),
                                        startTime = parsedStart.format(timeFormatter),
                                        endTime = parsedEnd.format(timeFormatter),
                                        location = location.trim()
                                    )
                                )
                            }
                        }
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


// --------------------------------------------------
// PREVIEWS
// --------------------------------------------------

@Preview(
    name = "Schedule - Light",
    showBackground = true
)
@Composable
fun ScheduleLightPreview() {
    StudySphereTheme(darkTheme = false) {
        ScheduleScreen()
    }
}

@Preview(
    name = "Schedule - Dark",
    showBackground = true
)
@Composable
fun ScheduleDarkPreview() {
    StudySphereTheme(darkTheme = true) {
        ScheduleScreen()
    }
}
