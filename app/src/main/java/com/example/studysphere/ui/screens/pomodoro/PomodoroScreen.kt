package com.example.studysphere.ui.screens.pomodoro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysphere.ui.screens.components.StudySphereHeader
import kotlinx.coroutines.delay
import kotlin.Int

private val PomodoroPurple = Color(0xFF8B6BCB)
private val PomodoroLightPurple = Color(0xFFEAE1FA)

@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    scheduledSubjects: List<String> = emptyList(),
    onMenuClick: () -> Unit = {}
) {

    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()
    val completedSessions by viewModel.completedFocusSessions.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }
    var useCustomSubject by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Automatically focus the subject field when custom subject is selected

    LaunchedEffect(useCustomSubject) {
        if (useCustomSubject) {
            delay(150)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // DYNAMIC TIMER DURATIONS

    val focusDuration = viewModel.getDurationSeconds(PomodoroMode.FOCUS)
    val shortBreakDuration = viewModel.getDurationSeconds(PomodoroMode.SHORT_BREAK)
    val longBreakDuration = viewModel.getDurationSeconds(PomodoroMode.LONG_BREAK)

    val totalSeconds = viewModel.getDurationSeconds(mode)

    val progress = if (totalSeconds > 0) {
        (remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    } else {
        0f
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = "%02d:%02d".format(minutes, seconds)

    val modeTitle = when (mode) {
        PomodoroMode.FOCUS -> "Focus Time"
        PomodoroMode.SHORT_BREAK -> "Short Break"
        PomodoroMode.LONG_BREAK -> "Long Break"
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 30.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // SHARED STUDYSPHERE HEADER
            StudySphereHeader(
                title = "Pomodoro Timer 🍅",
                subtitle = "Focus on one task at a time.",
                eyebrow = "YOUR FOCUS SPACE",
                onMenuClick = onMenuClick
            )

            // TIMER MODES

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(5.dp),

                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                ModeButton(
                    text = "Focus",
                    selected = mode == PomodoroMode.FOCUS,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.selectMode(PomodoroMode.FOCUS)
                }

                ModeButton(
                    text = "Short Break",
                    selected = mode == PomodoroMode.SHORT_BREAK,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.selectMode(PomodoroMode.SHORT_BREAK)
                }

                ModeButton(
                    text = "Long Break",
                    selected = mode == PomodoroMode.LONG_BREAK,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.selectMode(PomodoroMode.LONG_BREAK)
                }
            }

            // SUBJECT SELECTION

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "Choose your study subject",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = if (useCustomSubject) {
                            selectedSubject
                        } else {
                            selectedSubject.ifBlank { "" }
                        },

                        onValueChange = {
                            if (useCustomSubject) {
                                viewModel.selectSubject(it)
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),

                        enabled = !isRunning,

                        readOnly = !useCustomSubject,

                        label = {
                            Text("Study subject")
                        },

                        placeholder = {
                            Text(
                                if (useCustomSubject) {
                                    "Enter your subject"
                                } else {
                                    "Select a subject"
                                }
                            )
                        },

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),

                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    expanded = true
                                    keyboardController?.hide()
                                },
                                enabled = !isRunning
                            ) {
                                Text(
                                    "⌄",
                                    fontSize = 24.sp
                                )
                            }
                        },

                        shape = RoundedCornerShape(14.dp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = PomodoroPurple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = PomodoroPurple,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = PomodoroPurple
                        )
                    )

                    DropdownMenu(
                        expanded = expanded && !isRunning,

                        onDismissRequest = {
                            expanded = false
                        },

                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(
                                MaterialTheme.colorScheme.surface
                            )
                    ) {

                        scheduledSubjects.forEach { subject ->

                            DropdownMenuItem(
                                text = {
                                    Text(subject)
                                },

                                onClick = {
                                    viewModel.selectSubject(subject)
                                    useCustomSubject = false
                                    expanded = false
                                    keyboardController?.hide()
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
                                    color = PomodoroPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },

                            onClick = {
                                viewModel.selectSubject("")
                                useCustomSubject = true
                                expanded = false
                            }
                        )
                    }
                }
            }

            // CIRCULAR TIMER

            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {

                    val strokeWidth = 13.dp.toPx()

                    drawCircle(
                        color = PomodoroLightPurple,
                        style = Stroke(strokeWidth)
                    )

                    drawArc(
                        color = PomodoroPurple,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        modeTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        timeText,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Text(
                        when {
                            isRunning -> "Stay focused..."
                            mode == PomodoroMode.FOCUS &&
                                    selectedSubject.isBlank() ->
                                "Choose a subject to begin"
                            else -> "Ready when you are"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // TIMER CONTROLS

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FilledIconButton(
                    onClick = {
                        viewModel.resetTimer()
                    },
                    modifier = Modifier.size(54.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        "↻",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        if (isRunning) {
                            viewModel.pauseTimer()
                        } else {
                            viewModel.startTimer()
                        }
                    },

                    enabled = isRunning ||
                            mode != PomodoroMode.FOCUS ||
                            selectedSubject.isNotBlank(),

                    modifier = Modifier
                        .height(58.dp)
                        .width(150.dp),

                    shape = RoundedCornerShape(30.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = PomodoroPurple,
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        if (isRunning) "Ⅱ" else "▶",
                        fontSize = 22.sp
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        if (isRunning) "Pause" else "Start",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // COMPLETED FOCUS SESSIONS

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Completed Focus Sessions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        repeat(4) { index ->

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (
                                            completedSessions > 0 &&
                                            (
                                                    index < completedSessions % 4 ||
                                                            completedSessions % 4 == 0
                                                    )
                                        ) PomodoroPurple
                                        else PomodoroLightPurple,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🍅", fontSize = 19.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "$completedSessions focus sessions completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // DYNAMIC DURATION DESCRIPTION

            Text(
                "${focusDuration / 60} min focus  •  " +
                        "${shortBreakDuration / 60} min short break  •  " +
                        "${longBreakDuration / 60} min long break",

                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModeButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 4.dp,
            vertical = 10.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) PomodoroPurple
            else Color.Transparent,
            contentColor = if (selected) Color.White
            else MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp
        )
    ) {
        Text(
            text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
