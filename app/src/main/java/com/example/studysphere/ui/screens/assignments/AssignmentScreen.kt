package com.example.studysphere.ui.screens.assignments

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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysphere.data.local.entity.AssignmentEntity
import com.example.studysphere.ui.screens.components.StudySphereHeader
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.background

private val AssignmentPurple = Color(0xFF9270D5)

private val assignmentFilters = listOf(
    "ALL",
    "PENDING",
    "IN_PROGRESS",
    "COMPLETED"
)

@Composable
fun AssignmentScreen(
    viewModel: AssignmentViewModel,
    scheduledSubjects: List<String> = emptyList(),
    onMenuClick: () -> Unit = {}
) {
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var showAssignmentDialog by remember { mutableStateOf(false) }

    var assignmentToEdit by remember {
        mutableStateOf<AssignmentEntity?>(null)
    }

    var assignmentToDelete by remember {
        mutableStateOf<AssignmentEntity?>(null)
    }

    val pendingCount = assignments.count { it.status == "PENDING" }
    val inProgressCount = assignments.count { it.status == "IN_PROGRESS" }
    val completedCount = assignments.count { it.status == "COMPLETED" }

    val filteredAssignments = assignments
        .filter {
            selectedFilter == "ALL" || it.status == selectedFilter
        }
        .sortedBy { it.dueDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // SHARED STUDYSPHERE HEADER
        StudySphereHeader(
            title = "Assignments 📝",
            subtitle = "Keep track of your university deadlines.",
            onMenuClick = onMenuClick,
            eyebrow = "YOUR ACADEMIC TASK MANAGER"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AssignmentSummaryCard(
                title = "Pending",
                count = pendingCount,
                modifier = Modifier.weight(1f),
                color = Color(0xFFE5A13B)
            )

            AssignmentSummaryCard(
                title = "In Progress",
                count = inProgressCount,
                modifier = Modifier.weight(1f),
                color = AssignmentPurple
            )

            AssignmentSummaryCard(
                title = "Completed",
                count = completedCount,
                modifier = Modifier.weight(1f),
                color = Color(0xFF3DAB7A)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add assignment button
        Button(
            onClick = {
                assignmentToEdit = null
                showAssignmentDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AssignmentPurple
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Add Assignment",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your Assignments",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontally scrollable filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            assignmentFilters.forEach { filter ->

                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter.replace("_", " ")
                                .lowercase()
                                .replaceFirstChar { it.uppercase() },
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Assignment list
        if (filteredAssignments.isEmpty()) {

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
                        text = if (selectedFilter == "ALL") {
                            "No assignments yet"
                        } else {
                            "No ${selectedFilter.lowercase().replace("_", " ")} assignments"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Add an assignment to get started!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        } else {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredAssignments.forEach { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        onEdit = {
                            assignmentToEdit = assignment
                            showAssignmentDialog = true
                        },
                        onDelete = {
                            assignmentToDelete = assignment
                        },
                        onStatusChange = { newStatus ->
                            viewModel.updateAssignment(
                                assignment.copy(status = newStatus)
                            )
                        }
                    )
                }
            }
        }
    }

    // Add / Edit dialog
    if (showAssignmentDialog) {
        AssignmentEditorDialog(
            assignment = assignmentToEdit,
            scheduledSubjects = scheduledSubjects,
            onDismiss = {
                showAssignmentDialog = false
                assignmentToEdit = null
            },
            onSave = { title, description, subject, dueDate, priority, status ->

                if (assignmentToEdit == null) {
                    viewModel.addAssignment(
                        title = title,
                        description = description,
                        subject = subject,
                        dueDate = dueDate,
                        priority = priority
                    )
                } else {
                    viewModel.updateAssignment(
                        assignmentToEdit!!.copy(
                            title = title,
                            description = description,
                            subject = subject,
                            dueDate = dueDate,
                            priority = priority,
                            status = status
                        )
                    )
                }

                showAssignmentDialog = false
                assignmentToEdit = null
            }
        )
    }

    // Delete confirmation
    assignmentToDelete?.let { assignment ->

        AlertDialog(
            onDismissRequest = {
                assignmentToDelete = null
            },
            title = {
                Text("Delete Assignment?")
            },
            text = {
                Text("Are you sure you want to delete \"${assignment.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAssignment(assignment)
                        assignmentToDelete = null
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
                        assignmentToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AssignmentSummaryCard(
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
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun AssignmentCard(
    assignment: AssignmentEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    val priorityColor = when (assignment.priority) {
        "HIGH" -> Color(0xFFE45B63)
        "MEDIUM" -> Color(0xFFE5A13B)
        else -> Color(0xFF3DAB7A)
    }

    val isOverdue = isAssignmentOverdue(
        assignment.dueDate,
        assignment.status
    )

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
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = assignment.subject,
                        style = MaterialTheme.typography.bodySmall,
                        color = AssignmentPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit assignment"
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete assignment",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (assignment.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = assignment.description,
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
                    color = priorityColor.copy(alpha = 0.13f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${assignment.priority} PRIORITY",
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        color = priorityColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = when {
                        assignment.status == "COMPLETED" -> "Completed"
                        isOverdue -> "Overdue"
                        else -> "Due ${formatAssignmentDate(assignment.dueDate)}"
                    },
                    color = when {
                        assignment.status == "COMPLETED" ->
                            Color(0xFF3DAB7A)
                        isOverdue ->
                            MaterialTheme.colorScheme.error
                        else ->
                            MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box {
                OutlinedButton(
                    onClick = { showStatusMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Status: ${
                            assignment.status.replace("_", " ")
                                .lowercase()
                                .replaceFirstChar { it.uppercase() }
                        }"
                    )
                }

                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false }
                ) {
                    listOf(
                        "PENDING",
                        "IN_PROGRESS",
                        "COMPLETED"
                    ).forEach { status ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    status.replace("_", " ")
                                        .lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            },
                            onClick = {
                                onStatusChange(status)
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignmentEditorDialog(
    assignment: AssignmentEntity?,
    scheduledSubjects: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String,
        Long,
        String,
        String
    ) -> Unit
) {
    var title by remember(assignment?.id) {
        mutableStateOf(assignment?.title ?: "")
    }

    var description by remember(assignment?.id) {
        mutableStateOf(assignment?.description ?: "")
    }

    var subject by remember(assignment?.id) {
        mutableStateOf(assignment?.subject ?: "")
    }

    var priority by remember(assignment?.id) {
        mutableStateOf(assignment?.priority ?: "MEDIUM")
    }

    var status by remember(assignment?.id) {
        mutableStateOf(assignment?.status ?: "PENDING")
    }

    var isCustomSubject by remember(assignment?.id) {
        mutableStateOf(
            assignment != null &&
                    assignment.subject !in scheduledSubjects
        )
    }

    var showSubjectMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf(false) }

    val focusRequester = remember {
        FocusRequester()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
            assignment?.dueDate ?: System.currentTimeMillis()
    )

    val selectedDueDate = datePickerState.selectedDateMillis

    // Automatically focus and open keyboard for custom subject
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
                if (assignment == null) {
                    "Add Assignment"
                } else {
                    "Edit Assignment"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        showValidationError = false
                    },
                    label = { Text("Assignment Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Text(
                    text = "Subject",
                    style = MaterialTheme.typography.labelLarge
                )

                // Custom subject field or scheduled subject dropdown
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
                            label = { Text("Select subject") },
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
                            scheduledSubjects
                                .distinct()
                                .sorted()
                                .forEach { scheduledSubject ->

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
                                        color = AssignmentPurple,
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
                    text = "Due Date",
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        selectedDueDate?.let {
                            formatAssignmentDate(it)
                        } ?: "Choose due date"
                    )
                }

                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { option ->

                        FilterChip(
                            selected = priority == option,
                            onClick = { priority = option },
                            label = {
                                Text(
                                    option.lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            }
                        )
                    }
                }

                if (assignment != null) {

                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Column {
                        listOf(
                            "PENDING",
                            "IN_PROGRESS",
                            "COMPLETED"
                        ).forEach { option ->

                            FilterChip(
                                selected = status == option,
                                onClick = { status = option },
                                label = {
                                    Text(
                                        option.replace("_", " ")
                                            .lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    )
                                }
                            )
                        }
                    }
                }

                if (showValidationError) {
                    Text(
                        text = "Please enter a title, select a subject and choose a due date.",
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
                        selectedDueDate == null
                    ) {
                        showValidationError = true
                    } else {
                        keyboardController?.hide()

                        onSave(
                            title.trim(),
                            description.trim(),
                            subject.trim(),
                            selectedDueDate,
                            priority,
                            status
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

private fun formatAssignmentDate(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    formatter.timeZone = TimeZone.getTimeZone("UTC")

    return formatter.format(Date(timestamp))
}

private fun isAssignmentOverdue(
    dueDate: Long,
    status: String
): Boolean {
    if (status == "COMPLETED") return false

    val formatter = SimpleDateFormat(
        "yyyyMMdd",
        Locale.getDefault()
    )

    formatter.timeZone = TimeZone.getTimeZone("UTC")

    val today = formatter.format(Date())
    val deadline = formatter.format(Date(dueDate))

    return dueDate < 0L || today > deadline
}
