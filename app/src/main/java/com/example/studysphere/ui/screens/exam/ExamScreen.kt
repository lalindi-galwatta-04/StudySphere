
package com.example.studysphere.ui.screens.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysphere.data.local.entity.ExamEntity
import com.example.studysphere.ui.screens.components.StudySphereHeader
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val examFilters = listOf("ALL", "UPCOMING", "PAST")

// StudySphere lavender accent
private val ExamLavender = Color(0xFF9370D6)
private val ExamLavenderText = Color(0xFF2D2140)

@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    scheduledSubjects: List<String> = emptyList(),
    onMenuClick: () -> Unit = {}
) {
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val accentColor = MaterialTheme.colorScheme.primary

    var selectedFilter by remember { mutableStateOf("ALL") }
    var showExamDialog by remember { mutableStateOf(false) }
    var examToEdit by remember { mutableStateOf<ExamEntity?>(null) }
    var examToDelete by remember { mutableStateOf<ExamEntity?>(null) }

    val todayCount = exams.count { isExamToday(it.examDate) }
    val upcomingCount = exams.count { isExamUpcoming(it.examDate) }
    val pastCount = exams.count { isExamPast(it.examDate) }

    val filteredExams = exams.filter {
        when (selectedFilter) {
            "UPCOMING" -> isExamUpcoming(it.examDate)
            "PAST" -> isExamPast(it.examDate)
            else -> true
        }
    }.sortedWith(
        compareBy<ExamEntity> { it.examDate }.thenBy { it.startTime }
    )

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
            title = "Exams 📚",
            subtitle = "Stay prepared for your upcoming examinations.",
            onMenuClick = onMenuClick,
            eyebrow = "YOUR EXAM MANAGER"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ExamSummaryCard(
                "Upcoming",
                upcomingCount,
                Modifier.weight(1f),
                accentColor
            )

            ExamSummaryCard(
                "Today",
                todayCount,
                Modifier.weight(1f),
                Color(0xFFE5A13B)
            )

            ExamSummaryCard(
                "Past",
                pastCount,
                Modifier.weight(1f),
                Color(0xFF7B8794)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                examToEdit = null
                showExamDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ExamLavender,
                contentColor = ExamLavenderText
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Add Exam",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Your Exams",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            examFilters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            filter.lowercase().replaceFirstChar {
                                it.uppercase()
                            },
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredExams.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        when (selectedFilter) {
                            "UPCOMING" -> "No upcoming exams"
                            "PAST" -> "No past exams"
                            else -> "No exams yet"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "Add an exam to keep your examination schedule organised!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredExams.forEach { exam ->
                    ExamCard(
                        exam = exam,
                        onEdit = {
                            examToEdit = exam
                            showExamDialog = true
                        },
                        onDelete = {
                            examToDelete = exam
                        }
                    )
                }
            }
        }
    }

    if (showExamDialog) {
        ExamEditorDialog(
            exam = examToEdit,
            scheduledSubjects = scheduledSubjects,
            onDismiss = {
                showExamDialog = false
                examToEdit = null
            },
            onSave = {
                    subject,
                    title,
                    description,
                    examDate,
                    startTime,
                    endTime,
                    venue,
                    notes ->

                val existing = examToEdit

                if (existing == null) {
                    viewModel.addExam(
                        subject,
                        title,
                        description,
                        examDate,
                        startTime,
                        endTime,
                        venue,
                        notes
                    )
                } else {
                    viewModel.updateExam(
                        existing.copy(
                            subject = subject,
                            title = title,
                            description = description,
                            examDate = examDate,
                            startTime = startTime,
                            endTime = endTime,
                            venue = venue,
                            notes = notes
                        )
                    )
                }

                showExamDialog = false
                examToEdit = null
            }
        )
    }

    examToDelete?.let { exam ->
        AlertDialog(
            onDismissRequest = {
                examToDelete = null
            },
            title = {
                Text("Delete Exam?")
            },
            text = {
                Text("Are you sure you want to delete \"${exam.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExam(exam)
                        examToDelete = null
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
                        examToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ExamSummaryCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun ExamCard(
    exam: ExamEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStatus = when {
        isExamToday(exam.examDate) -> "Today"
        isExamPast(exam.examDate) -> "Past"
        else -> "Upcoming"
    }

    val accentColor = MaterialTheme.colorScheme.primary

    val statusColor = when (dateStatus) {
        "Today" -> Color(0xFFE5A13B)
        "Past" -> Color(0xFF7B8794)
        else -> accentColor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        exam.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        exam.subject,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit exam"
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete exam",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (exam.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    exam.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.13f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        dateStatus.uppercase(),
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    formatExamDate(exam.examDate),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (exam.startTime.isNotBlank() || exam.endTime.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Time: ${exam.startTime.ifBlank { "Not set" }}${if (exam.endTime.isNotBlank()) " – ${exam.endTime}" else ""}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (exam.venue.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Venue: ${exam.venue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (exam.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Notes: ${exam.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExamEditorDialog(
    exam: ExamEntity?,
    scheduledSubjects: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        subject: String,
        title: String,
        description: String,
        examDate: Long,
        startTime: String,
        endTime: String,
        venue: String,
        notes: String
    ) -> Unit
) {
    var title by remember(exam?.id) {
        mutableStateOf(exam?.title ?: "")
    }

    var description by remember(exam?.id) {
        mutableStateOf(exam?.description ?: "")
    }

    var subject by remember(exam?.id) {
        mutableStateOf(exam?.subject ?: "")
    }

    var startTime by remember(exam?.id) {
        mutableStateOf(exam?.startTime ?: "")
    }

    var endTime by remember(exam?.id) {
        mutableStateOf(exam?.endTime ?: "")
    }

    var venue by remember(exam?.id) {
        mutableStateOf(exam?.venue ?: "")
    }

    var notes by remember(exam?.id) {
        mutableStateOf(exam?.notes ?: "")
    }

    var isCustomSubject by remember(exam?.id) {
        mutableStateOf(
            exam != null && exam.subject !in scheduledSubjects
        )
    }

    var showSubjectMenu by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showValidationError by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
            exam?.examDate ?: System.currentTimeMillis()
    )

    val selectedExamDate = datePickerState.selectedDateMillis

    LaunchedEffect(isCustomSubject) {
        if (isCustomSubject) {
            delay(250)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (exam == null) "Add Exam" else "Edit Exam"
            )
        },

        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        showValidationError = false
                    },
                    label = {
                        Text("Exam Title")
                    },
                    placeholder = {
                        Text("e.g. Final Examination")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                Text(
                    "Subject",
                    style = MaterialTheme.typography.labelLarge
                )

                if (isCustomSubject) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = {
                            subject = it
                            showValidationError = false
                        },
                        label = {
                            Text("Enter custom subject")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    keyboardController?.hide()
                                    isCustomSubject = false
                                    subject = ""
                                }
                            ) {
                                Text("Choose")
                            }
                        }
                    )
                } else {
                    Box {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text("Select subject")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showSubjectMenu = true
                                },
                            trailingIcon = {
                                TextButton(
                                    onClick = {
                                        showSubjectMenu = true
                                    }
                                ) {
                                    Text("Select")
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = showSubjectMenu,
                            onDismissRequest = {
                                showSubjectMenu = false
                            }
                        ) {
                            scheduledSubjects.distinct().sorted().forEach {
                                    scheduledSubject ->

                                DropdownMenuItem(
                                    text = {
                                        Text(scheduledSubject)
                                    },
                                    onClick = {
                                        subject = scheduledSubject
                                        showSubjectMenu = false
                                    }
                                )
                            }

                            if (scheduledSubjects.isNotEmpty()) {
                                HorizontalDivider()
                            }

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "＋ Custom Subject",
                                        color = ExamLavender,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                onClick = {
                                    subject = ""
                                    isCustomSubject = true
                                    showSubjectMenu = false
                                }
                            )
                        }
                    }
                }

                Text(
                    "Exam Date",
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedButton(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        selectedExamDate?.let {
                            formatExamDate(it)
                        } ?: "Choose exam date"
                    )
                }

                OutlinedTextField(
                    value = startTime,
                    onValueChange = {
                        startTime = it
                    },
                    label = {
                        Text("Start Time (e.g. 09:00 AM)")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = {
                        endTime = it
                    },
                    label = {
                        Text("End Time (e.g. 12:00 PM)")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = {
                        venue = it
                    },
                    label = {
                        Text("Venue")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                    },
                    label = {
                        Text("Additional Notes")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                if (showValidationError) {
                    Text(
                        "Please enter an exam title, select a subject and choose an exam date.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    if (
                        title.isBlank() ||
                        subject.isBlank() ||
                        selectedExamDate == null
                    ) {
                        showValidationError = true
                    } else {
                        keyboardController?.hide()

                        onSave(
                            subject.trim(),
                            title.trim(),
                            description.trim(),
                            selectedExamDate,
                            startTime.trim(),
                            endTime.trim(),
                            venue.trim(),
                            notes.trim()
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(
                onClick = {
                    keyboardController?.hide()
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatExamDate(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    formatter.timeZone = TimeZone.getTimeZone("UTC")

    return formatter.format(Date(timestamp))
}

private fun examDateKey(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "yyyyMMdd",
        Locale.getDefault()
    )

    formatter.timeZone = TimeZone.getTimeZone("UTC")

    return formatter.format(Date(timestamp))
}

private fun isExamToday(timestamp: Long): Boolean =
    examDateKey(timestamp) == examDateKey(System.currentTimeMillis())

private fun isExamUpcoming(timestamp: Long): Boolean =
    examDateKey(timestamp) >= examDateKey(System.currentTimeMillis())

private fun isExamPast(timestamp: Long): Boolean =
    examDateKey(timestamp) < examDateKey(System.currentTimeMillis())
