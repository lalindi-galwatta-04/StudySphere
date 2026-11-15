
package com.example.studysphere.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studysphere.data.local.StudyPreferences
import com.example.studysphere.ui.screens.profile.StudentProfile
import com.example.studysphere.ui.screens.components.StudySphereHeader
import com.example.studysphere.ui.screens.progress.ProgressViewModel
import com.example.studysphere.ui.screens.pomodoro.PomodoroViewModel
import com.example.studysphere.ui.screens.reminders.ReminderViewModel
import com.example.studysphere.ui.theme.AppThemeMode

@Composable
fun SettingsScreen(
    profile: StudentProfile,
    selectedTheme: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit,
    onEditProfile: () -> Unit,
    reminderViewModel: ReminderViewModel,
    studyPreferences: StudyPreferences,
    progressViewModel: ProgressViewModel,
    pomodoroViewModel: PomodoroViewModel,
    onMenuClick: () -> Unit = {}
) {

    // NOTIFICATION PREFERENCES

    var masterEnabled by remember(reminderViewModel) {
        mutableStateOf(reminderViewModel.areNotificationsEnabled())
    }

    var assignmentsEnabled by remember(reminderViewModel) {
        mutableStateOf(reminderViewModel.isAssignmentReminderEnabled())
    }

    var lecturesEnabled by remember(reminderViewModel) {
        mutableStateOf(reminderViewModel.isLectureReminderEnabled())
    }

    var studyEnabled by remember(reminderViewModel) {
        mutableStateOf(reminderViewModel.isStudyReminderEnabled())
    }

    var examsEnabled by remember(reminderViewModel) {
        mutableStateOf(reminderViewModel.isExamReminderEnabled())
    }

    // STUDY PREFERENCES

    var weeklyGoalHours by remember(studyPreferences) {
        mutableIntStateOf(
            (studyPreferences.getWeeklyGoal() / 60).coerceIn(1, 40)
        )
    }

    var focusDuration by remember(studyPreferences) {
        mutableIntStateOf(
            studyPreferences.getFocusDuration().coerceIn(5, 120)
        )
    }

    var shortBreakDuration by remember(studyPreferences) {
        mutableIntStateOf(
            studyPreferences.getShortBreakDuration().coerceIn(1, 30)
        )
    }

    var longBreakDuration by remember(studyPreferences) {
        mutableIntStateOf(
            studyPreferences.getLongBreakDuration().coerceIn(5, 60)
        )
    }

    var longBreakInterval by remember(studyPreferences) {
        mutableIntStateOf(
            studyPreferences.getLongBreakInterval().coerceIn(2, 8)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // SETTINGS HEADER

        StudySphereHeader(
            title = "Settings ⚙️",
            subtitle = "Personalise your StudySphere experience.",
            eyebrow = "YOUR STUDYSPACE PREFERENCES",
            onMenuClick = onMenuClick
        )

        // PROFILE SECTION

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.titleLarge
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = profile.name.firstOrNull()
                                    ?.uppercase() ?: "S",
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 12.dp
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = profile.university,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider()

                    ProfileDetail("Degree Programme", profile.degree)
                    ProfileDetail("Academic Year", profile.academicYear)
                    ProfileDetail("Current Semester", profile.semester)

                    Button(
                        onClick = onEditProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("✏️  Edit Profile")
                    }
                }
            }
        }

        // APPEARANCE SECTION

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Choose how StudySphere looks on your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ThemeOption(
                title = "System Default",
                description = "Follow your device's theme settings",
                emoji = "📱",
                selected = selectedTheme == AppThemeMode.SYSTEM,
                onClick = { onThemeChange(AppThemeMode.SYSTEM) }
            )

            ThemeOption(
                title = "Light Mode",
                description = "Use a bright and clean appearance",
                emoji = "☀️",
                selected = selectedTheme == AppThemeMode.LIGHT,
                onClick = { onThemeChange(AppThemeMode.LIGHT) }
            )

            ThemeOption(
                title = "Dark Mode",
                description = "Use a darker appearance",
                emoji = "🌙",
                selected = selectedTheme == AppThemeMode.DARK,
                onClick = { onThemeChange(AppThemeMode.DARK) }
            )
        }

        // NOTIFICATION PREFERENCES

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Choose which reminders StudySphere can send you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ALL NOTIFICATIONS

                    NotificationSwitch(
                        title = "All Notifications",
                        description = "Enable or disable all StudySphere reminders",
                        checked = masterEnabled,
                        onCheckedChange = { enabled ->

                            masterEnabled = enabled

                            if (enabled) {
                                assignmentsEnabled = true
                                lecturesEnabled = true
                                studyEnabled = true
                                examsEnabled = true

                                reminderViewModel.setAssignmentRemindersEnabled(true)
                                reminderViewModel.setLectureRemindersEnabled(true)
                                reminderViewModel.setStudyRemindersEnabled(true)
                                reminderViewModel.setExamRemindersEnabled(true)
                            }

                            reminderViewModel.setNotificationsEnabled(enabled)
                        }
                    )

                    HorizontalDivider()

                    // ASSIGNMENT REMINDERS

                    NotificationSwitch(
                        title = "Assignment Reminders",
                        description = "Receive assignment deadline alerts",
                        checked = assignmentsEnabled,
                        enabled = masterEnabled,
                        onCheckedChange = { enabled ->

                            assignmentsEnabled = enabled

                            reminderViewModel.setAssignmentRemindersEnabled(enabled)

                            if (
                                !enabled &&
                                !lecturesEnabled &&
                                !studyEnabled &&
                                !examsEnabled
                            ) {
                                masterEnabled = false
                                reminderViewModel.setNotificationsEnabled(false)
                            }
                        }
                    )

                    // LECTURE REMINDERS

                    NotificationSwitch(
                        title = "Lecture Reminders",
                        description = "Receive upcoming lecture alerts",
                        checked = lecturesEnabled,
                        enabled = masterEnabled,
                        onCheckedChange = { enabled ->

                            lecturesEnabled = enabled

                            reminderViewModel.setLectureRemindersEnabled(enabled)

                            if (
                                !enabled &&
                                !assignmentsEnabled &&
                                !studyEnabled &&
                                !examsEnabled
                            ) {
                                masterEnabled = false
                                reminderViewModel.setNotificationsEnabled(false)
                            }
                        }
                    )

                    // STUDY REMINDERS

                    NotificationSwitch(
                        title = "Study Reminders",
                        description = "Receive your custom study reminders",
                        checked = studyEnabled,
                        enabled = masterEnabled,
                        onCheckedChange = { enabled ->

                            studyEnabled = enabled

                            reminderViewModel.setStudyRemindersEnabled(enabled)

                            if (
                                !enabled &&
                                !assignmentsEnabled &&
                                !lecturesEnabled &&
                                !examsEnabled
                            ) {
                                masterEnabled = false
                                reminderViewModel.setNotificationsEnabled(false)
                            }
                        }
                    )

                    // EXAM REMINDERS

                    NotificationSwitch(
                        title = "Exam Reminders",
                        description = "Receive alerts one day before your exams",
                        checked = examsEnabled,
                        enabled = masterEnabled,
                        onCheckedChange = { enabled ->

                            examsEnabled = enabled

                            reminderViewModel.setExamRemindersEnabled(enabled)

                            if (
                                !enabled &&
                                !assignmentsEnabled &&
                                !lecturesEnabled &&
                                !studyEnabled
                            ) {
                                masterEnabled = false
                                reminderViewModel.setNotificationsEnabled(false)
                            }
                        }
                    )
                }
            }

            Text(
                text = "Your notification preferences are saved automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // STUDY PREFERENCES

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Study Preferences",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Customise your study routine and Pomodoro sessions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {

                    // WEEKLY STUDY GOAL

                    PreferenceSlider(
                        title = "Weekly Study Goal",
                        description = "Set your target study hours per week",
                        valueText = "$weeklyGoalHours hours",
                        value = weeklyGoalHours.toFloat(),
                        valueRange = 1f..40f,
                        steps = 38,
                        onValueChange = { value ->

                            weeklyGoalHours = value.toInt()

                            progressViewModel.updateWeeklyGoal(
                                weeklyGoalHours * 60
                            )
                        }
                    )

                    HorizontalDivider()

                    // FOCUS DURATION

                    PreferenceSlider(
                        title = "Focus Duration",
                        description = "How long would you like each focus session to last?",
                        valueText = "$focusDuration minutes",
                        value = focusDuration.toFloat(),
                        valueRange = 5f..120f,
                        steps = 22,
                        onValueChange = { value ->

                            focusDuration = (value.toInt() / 5) * 5

                            studyPreferences.saveFocusDuration(focusDuration)

                            pomodoroViewModel.refreshDuration()
                        }
                    )

                    HorizontalDivider()

                    // SHORT BREAK DURATION

                    PreferenceSlider(
                        title = "Short Break Duration",
                        description = "Choose the length of your short breaks",
                        valueText = "$shortBreakDuration minutes",
                        value = shortBreakDuration.toFloat(),
                        valueRange = 1f..30f,
                        steps = 28,
                        onValueChange = { value ->

                            shortBreakDuration = value.toInt()

                            studyPreferences.saveShortBreakDuration(
                                shortBreakDuration
                            )

                            pomodoroViewModel.refreshDuration()
                        }
                    )

                    HorizontalDivider()

                    // LONG BREAK DURATION

                    PreferenceSlider(
                        title = "Long Break Duration",
                        description = "Choose the length of your longer breaks",
                        valueText = "$longBreakDuration minutes",
                        value = longBreakDuration.toFloat(),
                        valueRange = 5f..60f,
                        steps = 10,
                        onValueChange = { value ->

                            longBreakDuration = (value.toInt() / 5) * 5

                            studyPreferences.saveLongBreakDuration(
                                longBreakDuration
                            )

                            pomodoroViewModel.refreshDuration()
                        }
                    )

                    HorizontalDivider()

                    // LONG BREAK INTERVAL

                    PreferenceSlider(
                        title = "Long Break Interval",
                        description = "Number of focus sessions before a long break",
                        valueText = "Every $longBreakInterval sessions",
                        value = longBreakInterval.toFloat(),
                        valueRange = 2f..8f,
                        steps = 5,
                        onValueChange = { value ->

                            longBreakInterval = value.toInt()

                            studyPreferences.saveLongBreakInterval(
                                longBreakInterval
                            )
                        }
                    )
                }
            }

            Text(
                text = "Your study preferences are saved automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// PROFILE DETAIL

@Composable
private fun ProfileDetail(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// NOTIFICATION SWITCH COMPONENT

@Composable
private fun NotificationSwitch(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

// STUDY PREFERENCE SLIDER

@Composable
private fun PreferenceSlider(
    title: String,
    description: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = valueText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Slider(
            value = value.coerceIn(valueRange),
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}

// APPEARANCE THEME OPTION

@Composable
private fun ThemeOption(
    title: String,
    description: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.padding(horizontal = 6.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick
            )
        }
    }
}
