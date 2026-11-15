package com.example.studysphere.ui.screens.reminders

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysphere.data.local.entity.ReminderEntity
import com.example.studysphere.ui.screens.components.StudySphereHeader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ReminderLightLavender = Color(0xFFF0EBFA)

@Composable
fun RemindersScreen(
    viewModel: ReminderViewModel,
    onMenuClick: () -> Unit = {}
) {
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var reminderToEdit by remember {
        mutableStateOf<ReminderEntity?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 36.dp)
    ) {
        StudySphereHeader(
            title = "Reminders 🔔",
            subtitle = "Stay on top of your academic tasks.",
            eyebrow = "YOUR STUDY NOTIFICATIONS",
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = ReminderLightLavender
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔔", fontSize = 42.sp)

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Never miss a deadline!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF453568)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Manage your lectures, assignments and study reminders in one place.",
                        fontSize = 13.sp,
                        color = Color(0xFF655681),
                        lineHeight = 19.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Upcoming Reminders",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = {
                    reminderToEdit = null
                    showAddDialog = true
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Add")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (reminders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔔", fontSize = 45.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No reminders yet",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Create your first reminder to keep track of your study activities.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                }
            }
        } else {
            reminders.sortedBy { it.reminderTime }.forEach { reminder ->

                ReminderItem(
                    reminder = reminder,
                    onToggle = {
                        viewModel.toggleReminder(reminder)
                    },
                    onEdit = {
                        reminderToEdit = reminder
                        showAddDialog = true
                    },
                    onDelete = {
                        viewModel.deleteReminder(reminder)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showAddDialog) {
        AddReminderDialog(
            initialReminder = reminderToEdit,
            onDismiss = {
                showAddDialog = false
                reminderToEdit = null
            },
            onSave = { title, description, type, reminderTime ->

                val existingReminder = reminderToEdit

                if (existingReminder == null) {
                    viewModel.addReminder(
                        title = title,
                        description = description,
                        type = type,
                        reminderTime = reminderTime
                    )
                } else {
                    viewModel.updateReminder(
                        existingReminder.copy(
                            title = title,
                            description = description,
                            type = type,
                            reminderTime = reminderTime
                        )
                    )
                }

                showAddDialog = false
                reminderToEdit = null
            }
        )
    }
}

@Composable
private fun ReminderItem(
    reminder: ReminderEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat(
            "dd MMM yyyy • hh:mm a",
            Locale.getDefault()
        )
    }

    val isExamReminder = reminder.type.equals("EXAM", ignoreCase = true)
    val isAssignmentReminder =
        reminder.type.equals("ASSIGNMENT", ignoreCase = true) &&
                reminder.relatedItemId != null

    // Exam and assignment reminders linked to academic records are automatic.
    // Manually created reminders have no relatedItemId and remain editable.
    val isAutomaticallyScheduled =
        reminder.relatedItemId != null &&
                (isExamReminder || isAssignmentReminder)

    val emoji = when (reminder.type) {
        "LECTURE" -> "📚"
        "ASSIGNMENT" -> "📝"
        "EXAM" -> "🎓"
        else -> "⏰"
    }

    val categoryLabel = when {
        isExamReminder -> "Exam Reminder"
        isAssignmentReminder -> "Assignment Reminder"
        else -> reminder.type.lowercase()
            .replaceFirstChar { it.uppercase() }
    }

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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(emoji, fontSize = 28.sp)

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = reminder.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = categoryLabel,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (!isAutomaticallyScheduled) {
                    Switch(
                        checked = reminder.isEnabled,
                        onCheckedChange = { onToggle() }
                    )
                }
            }

            if (reminder.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = reminder.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dateFormat.format(
                    Date(reminder.reminderTime)
                ),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isAutomaticallyScheduled) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isAssignmentReminder) {
                        "Automatically scheduled 24 hours before your assignment due date."
                    } else {
                        "Automatically scheduled 24 hours before your exam."
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onEdit) {
                        Text("Edit")
                    }

                    TextButton(onClick = onDelete) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddReminderDialog(
    initialReminder: ReminderEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Long) -> Unit
) {
    val context = LocalContext.current

    var title by remember(initialReminder) {
        mutableStateOf(initialReminder?.title ?: "")
    }

    var description by remember(initialReminder) {
        mutableStateOf(initialReminder?.description ?: "")
    }

    var selectedType by remember(initialReminder) {
        mutableStateOf(initialReminder?.type ?: "CUSTOM")
    }

    var expanded by remember { mutableStateOf(false) }

    val initialCalendar = remember(initialReminder) {
        Calendar.getInstance().apply {
            timeInMillis = initialReminder?.reminderTime
                ?: System.currentTimeMillis()

            if (initialReminder == null) {
                add(Calendar.HOUR_OF_DAY, 1)
            }
        }
    }

    var selectedDate by remember(initialReminder) {
        mutableLongStateOf(initialCalendar.timeInMillis)
    }

    var selectedHour by remember(initialReminder) {
        mutableIntStateOf(
            initialCalendar.get(Calendar.HOUR_OF_DAY)
        )
    }

    var selectedMinute by remember(initialReminder) {
        mutableIntStateOf(
            initialCalendar.get(Calendar.MINUTE)
        )
    }

    val dateFormat = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    val timeText = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        selectedHour,
        selectedMinute
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = if (initialReminder == null)
                    "Create Reminder"
                else
                    "Edit Reminder",
                fontWeight = FontWeight.Bold
            )
        },

        text = {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder title") },
                    placeholder = {
                        Text("e.g. Study Data Structures")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Text(
                    text = "Reminder category",
                    style = MaterialTheme.typography.labelLarge
                )

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            selectedType.lowercase()
                                .replaceFirstChar { it.uppercase() }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        listOf(
                            "CUSTOM",
                            "LECTURE",
                            "ASSIGNMENT"
                        ).forEach { type ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        type.lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    )
                                },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Reminder date and time",
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                        }

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val updated = Calendar.getInstance().apply {
                                    timeInMillis = selectedDate
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, day)
                                }

                                selectedDate = updated.timeInMillis
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            if (
                                initialReminder == null ||
                                selectedDate >= System.currentTimeMillis()
                            ) {
                                datePicker.minDate =
                                    System.currentTimeMillis()
                            }
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "📅  ${dateFormat.format(Date(selectedDate))}"
                    )
                }

                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                selectedHour = hour
                                selectedMinute = minute
                            },
                            selectedHour,
                            selectedMinute,
                            false
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🕒  $timeText")
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = selectedDate
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE, selectedMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    onSave(
                        title.trim(),
                        description.trim(),
                        selectedType,
                        calendar.timeInMillis
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text(
                    if (initialReminder == null)
                        "Save Reminder"
                    else
                        "Save Changes"
                )
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
