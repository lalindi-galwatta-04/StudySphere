package com.example.studysphere.ui.screens.notes

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studysphere.data.local.entity.NoteEntity
import com.example.studysphere.ui.theme.StudySphereTheme
import com.example.studysphere.ui.screens.components.StudySphereHeader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(
    viewModel: NoteViewModel? = null,
    onMenuClick: () -> Unit = {}
) {

    val colors = MaterialTheme.colorScheme

    // Observe notes from Room
    val notes by viewModel?.notes?.collectAsState()
        ?: remember {
            mutableStateOf(emptyList())
        }

    var searchQuery by remember {
        mutableStateOf("")
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var editingNote by remember {
        mutableStateOf<NoteEntity?>(null)
    }

    var deletingNote by remember {
        mutableStateOf<NoteEntity?>(null)
    }

    // Search notes by title, content or subject
    val filteredNotes = notes.filter { note ->

        note.title.contains(searchQuery, ignoreCase = true) ||
                note.content.contains(searchQuery, ignoreCase = true) ||
                note.subject.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {

        // Shared StudySphere header
        StudySphereHeader(
            eyebrow = "YOUR PERSONAL STUDY SPACE",
            title = "My Notes 📒",
            subtitle = "Keep your learning organized.",
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search your notes...")
            },
            leadingIcon = {
                Text("🔍", fontSize = 18.sp)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            searchQuery = ""
                        }
                    ) {
                        Text("Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Notes heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (searchQuery.isBlank()) {
                    "Recent Notes"
                } else {
                    "Search Results"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Text(
                text = "${filteredNotes.size} Notes",
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes list
        if (filteredNotes.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = if (searchQuery.isBlank()) {
                            "📝"
                        } else {
                            "🔎"
                        },
                        fontSize = 44.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (searchQuery.isBlank()) {
                            "No notes yet!"
                        } else {
                            "No matching notes found."
                        },
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (searchQuery.isBlank()) {
                            "Create your first note and start organizing your studies."
                        } else {
                            "Try searching with another keyword."
                        },
                        color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        } else {

            filteredNotes.forEach { note ->

                NoteCard(
                    note = note,
                    onClick = {
                        editingNote = note
                    },
                    onDelete = {
                        deletingNote = note
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add note button
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
                text = "+  Create New Note",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Add note dialog
    if (showAddDialog) {

        NoteEditorDialog(
            title = "Create New Note ✏️",
            onDismiss = {
                showAddDialog = false
            },
            onSave = { title, content, subject ->

                viewModel?.addNote(
                    title = title,
                    content = content,
                    subject = subject
                )

                showAddDialog = false
            }
        )
    }

    // Edit note dialog
    editingNote?.let { note ->

        NoteEditorDialog(
            title = "Edit Note 📝",
            initialNote = note,
            onDismiss = {
                editingNote = null
            },
            onSave = { title, content, subject ->

                viewModel?.updateNote(
                    note.copy(
                        title = title.trim(),
                        content = content.trim(),
                        subject = subject.trim()
                    )
                )

                editingNote = null
            }
        )
    }

    // Delete confirmation
    deletingNote?.let { note ->

        AlertDialog(
            onDismissRequest = {
                deletingNote = null
            },
            title = {
                Text("Delete Note?")
            },
            text = {
                Text(
                    "Are you sure you want to delete '${note.title}'? " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel?.deleteNote(note)
                        deletingNote = null
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
                        deletingNote = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Individual note card
@Composable
fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val colors = MaterialTheme.colorScheme

    val dateFormatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    val updatedDate = remember(note.updatedAt) {
        dateFormatter.format(Date(note.updatedAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.primaryContainer
                    ) {
                        Text(
                            text = note.subject,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            color = colors.onPrimaryContainer,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                TextButton(
                    onClick = onDelete
                ) {
                    Text(
                        "🗑",
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note.content,
                color = colors.onSurfaceVariant.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = colors.outlineVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Updated: $updatedDate",
                color = colors.onSurfaceVariant.copy(alpha = 0.65f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// Create and Edit Note Dialog
@Composable
fun NoteEditorDialog(
    title: String,
    initialNote: NoteEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {

    var noteTitle by remember(initialNote?.id) {
        mutableStateOf(initialNote?.title ?: "")
    }

    var noteContent by remember(initialNote?.id) {
        mutableStateOf(initialNote?.content ?: "")
    }

    var noteSubject by remember(initialNote?.id) {
        mutableStateOf(initialNote?.subject ?: "")
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
                modifier = Modifier
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = {
                        noteTitle = it
                        errorMessage = ""
                    },
                    label = {
                        Text("Note Title")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteSubject,
                    onValueChange = {
                        noteSubject = it
                        errorMessage = ""
                    },
                    label = {
                        Text("Subject")
                    },
                    placeholder = {
                        Text("e.g. Database Systems")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteContent,
                    onValueChange = {
                        noteContent = it
                        errorMessage = ""
                    },
                    label = {
                        Text("Note Content")
                    },
                    placeholder = {
                        Text("Write your study notes here...")
                    },
                    minLines = 5,
                    modifier = Modifier.fillMaxWidth()
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

                    when {

                        noteTitle.isBlank() -> {
                            errorMessage = "Please enter a note title."
                        }

                        noteSubject.isBlank() -> {
                            errorMessage = "Please enter a subject."
                        }

                        noteContent.isBlank() -> {
                            errorMessage = "Please enter note content."
                        }

                        else -> {
                            onSave(
                                noteTitle,
                                noteContent,
                                noteSubject
                            )
                        }
                    }
                }
            ) {
                Text("Save Note")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Light preview
@Preview(
    name = "Notes - Light",
    showBackground = true
)
@Composable
fun NotesLightPreview() {
    StudySphereTheme(darkTheme = false) {
        NotesScreen()
    }
}

// Dark preview
@Preview(
    name = "Notes - Dark",
    showBackground = true
)
@Composable
fun NotesDarkPreview() {
    StudySphereTheme(darkTheme = true) {
        NotesScreen()
    }
}