package com.example.studysphere.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

import com.example.studysphere.data.local.StudyPreferences
import com.example.studysphere.data.local.StudySphereDatabase

import com.example.studysphere.data.repository.AssignmentRepository
import com.example.studysphere.data.repository.ExamRepository
import com.example.studysphere.data.repository.LectureRepository
import com.example.studysphere.data.repository.NoteRepository
import com.example.studysphere.data.repository.StudySessionRepository
import com.example.studysphere.data.repository.ReminderRepository

import com.example.studysphere.ui.screens.dashboard.DashboardScreen
import com.example.studysphere.ui.screens.profile.StudentProfile

import com.example.studysphere.ui.screens.schedule.ScheduleScreen
import com.example.studysphere.ui.screens.schedule.ScheduleViewModel
import com.example.studysphere.ui.screens.schedule.ScheduleViewModelFactory

import com.example.studysphere.ui.screens.notes.NotesScreen
import com.example.studysphere.ui.screens.notes.NoteViewModel
import com.example.studysphere.ui.screens.notes.NoteViewModelFactory

import com.example.studysphere.ui.screens.progress.ProgressScreen
import com.example.studysphere.ui.screens.progress.ProgressViewModel
import com.example.studysphere.ui.screens.progress.ProgressViewModelFactory

import com.example.studysphere.ui.screens.pomodoro.PomodoroScreen
import com.example.studysphere.ui.screens.pomodoro.PomodoroViewModel
import com.example.studysphere.ui.screens.pomodoro.PomodoroViewModelFactory

import com.example.studysphere.ui.screens.assignments.AssignmentScreen
import com.example.studysphere.ui.screens.assignments.AssignmentViewModel
import com.example.studysphere.ui.screens.assignments.AssignmentViewModelFactory

import com.example.studysphere.ui.screens.exams.ExamScreen
import com.example.studysphere.ui.screens.exams.ExamViewModel
import com.example.studysphere.ui.screens.exams.ExamViewModelFactory

import com.example.studysphere.ui.screens.calendar.CalendarScreen

import com.example.studysphere.ui.screens.reminders.RemindersScreen
import com.example.studysphere.ui.screens.reminders.ReminderViewModel
import com.example.studysphere.ui.screens.reminders.ReminderViewModelFactory

import com.example.studysphere.ui.screens.settings.SettingsScreen
import com.example.studysphere.ui.screens.settings.EditProfileScreen

import com.example.studysphere.ui.theme.AppThemeMode


// --------------------------------------------------
// NAVIGATION DESTINATIONS
// --------------------------------------------------

private enum class AppDestination(
    val label: String,
    val emoji: String
) {
    HOME("Home", "🏠"),
    SCHEDULE("Schedule", "📅"),
    ASSIGNMENTS("Assignments", "📝"),
    NOTES("Notes", "📒"),
    PROGRESS("Progress", "📊"),
    POMODORO("Pomodoro", "🍅"),
    CALENDAR("Calendar", "🗓️"),
    EXAMS("Exams", "📚"),
    REMINDERS("Reminders", "🔔"),
    SETTINGS("Settings", "⚙️"),
    EDIT_PROFILE("Edit Profile", "✏️")
}

private val bottomDestinations = listOf(
    AppDestination.HOME,
    AppDestination.SCHEDULE,
    AppDestination.ASSIGNMENTS,
    AppDestination.NOTES
)


// --------------------------------------------------
// MAIN NAVIGATION
// --------------------------------------------------

@Composable
fun StudySphereNavigation(
    profile: StudentProfile,
    selectedTheme: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit,
    onProfileUpdate: (StudentProfile) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    val database = remember {
        StudySphereDatabase.getDatabase(context)
    }


    // --------------------------------------------------
    // LECTURE REPOSITORY AND VIEWMODEL
    // --------------------------------------------------

    val lectureRepository = remember {
        LectureRepository(database.lectureDao())
    }

    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(lectureRepository)
    )

    val scheduledLectures by scheduleViewModel.lectures
        .collectAsStateWithLifecycle()

    val scheduledSubjects = scheduledLectures
        .map { it.subject }
        .distinct()
        .sorted()


    // --------------------------------------------------
    // NOTES REPOSITORY AND VIEWMODEL
    // --------------------------------------------------

    val noteRepository = remember {
        NoteRepository(database.noteDao())
    }

    val noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(noteRepository)
    )


    // --------------------------------------------------
    // STUDY SESSION REPOSITORY
    // --------------------------------------------------

    val studySessionRepository = remember {
        StudySessionRepository(database.studySessionDao())
    }

    val studyPreferences = remember {
        StudyPreferences(context)
    }


    // --------------------------------------------------
    // PROGRESS VIEWMODEL
    // --------------------------------------------------

    val progressViewModel: ProgressViewModel = viewModel(
        factory = ProgressViewModelFactory(
            studySessionRepository,
            studyPreferences
        )
    )

    val dashboardStudySessions by progressViewModel.sessions
        .collectAsStateWithLifecycle()

    val dashboardWeeklyGoal by progressViewModel.weeklyGoalMinutes
        .collectAsStateWithLifecycle()


    // --------------------------------------------------
    // POMODORO VIEWMODEL
    // --------------------------------------------------

    val pomodoroViewModel: PomodoroViewModel = viewModel(
        factory = PomodoroViewModelFactory(
            studySessionRepository,
            studyPreferences
        )
    )


    // --------------------------------------------------
    // ASSIGNMENT REPOSITORY AND VIEWMODEL
    // --------------------------------------------------

    val assignmentRepository = remember {
        AssignmentRepository(database.assignmentDao())
    }

    // --------------------------------------------------
    // EXAM REPOSITORY
    // --------------------------------------------------

    val examRepository = remember {
        ExamRepository(database.examDao())
    }


    // --------------------------------------------------
    // REMINDER REPOSITORY AND VIEWMODEL
    // --------------------------------------------------

    val reminderRepository = remember {
        ReminderRepository(database.reminderDao())
    }

    // --------------------------------------------------
    // ASSIGNMENT VIEWMODEL
    // --------------------------------------------------

    val assignmentViewModel: AssignmentViewModel = viewModel(
        factory = AssignmentViewModelFactory(
            repository = assignmentRepository,
            reminderRepository = reminderRepository,
            context = context
        )
    )

    val calendarAssignments by assignmentViewModel.assignments
        .collectAsStateWithLifecycle()

    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(
            context = context,
            repository = reminderRepository
        )
    )

    val dashboardReminders by reminderViewModel.reminders
        .collectAsStateWithLifecycle()


    // --------------------------------------------------
    // EXAM VIEWMODEL
    // --------------------------------------------------

    val examViewModel: ExamViewModel = viewModel(
        factory = ExamViewModelFactory(
            examRepository = examRepository,
            reminderRepository = reminderRepository,
            context = context
        )
    )

    // Keep the calendar updated whenever exams are added, edited, or deleted.
    val calendarExams by examViewModel.exams
        .collectAsStateWithLifecycle()


    // --------------------------------------------------
    // SELECTED DESTINATION
    // --------------------------------------------------

    var selectedDestination by remember {
        mutableStateOf(AppDestination.HOME)
    }


    // --------------------------------------------------
    // NAVIGATION DRAWER
    // --------------------------------------------------

    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet(
                drawerContainerColor = colors.surface,
                drawerShape = RoundedCornerShape(
                    topEnd = 24.dp,
                    bottomEnd = 24.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {

                    Spacer(modifier = Modifier.height(28.dp))


                    // BRAND HEADER

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Surface(
                            shape = RoundedCornerShape(15.dp),
                            color = colors.primary,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "StudySphere",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )

                            Text(
                                text = "Your academic companion",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        color = colors.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    // STUDENT PROFILE CARD

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = colors.primaryContainer
                    ) {

                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Surface(
                                shape = CircleShape,
                                color = colors.primary,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = profile.name.firstOrNull()
                                            ?.uppercase() ?: "S",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onPrimary
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimaryContainer
                                )

                                Text(
                                    text = profile.university,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onPrimaryContainer.copy(
                                        alpha = 0.75f
                                    )
                                )

                                Text(
                                    text = "${profile.academicYear} • ${profile.semester}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onPrimaryContainer.copy(
                                        alpha = 0.75f
                                    )
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(28.dp))


                    // MAIN MENU

                    DrawerSectionHeading("MAIN MENU")

                    listOf(
                        AppDestination.HOME,
                        AppDestination.SCHEDULE,
                        AppDestination.ASSIGNMENTS,
                        AppDestination.NOTES
                    ).forEach { destination ->

                        DrawerNavigationItem(
                            destination = destination,
                            selected = selectedDestination == destination,
                            onClick = {
                                selectedDestination = destination

                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        color = colors.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))


                    // STUDY TOOLS

                    DrawerSectionHeading("STUDY TOOLS")

                    listOf(
                        AppDestination.PROGRESS,
                        AppDestination.POMODORO,
                        AppDestination.CALENDAR,
                        AppDestination.EXAMS,
                        AppDestination.REMINDERS
                    ).forEach { destination ->

                        DrawerNavigationItem(
                            destination = destination,
                            selected = selectedDestination == destination,
                            onClick = {
                                selectedDestination = destination

                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        color = colors.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))


                    // PREFERENCES

                    DrawerSectionHeading("PREFERENCES")

                    DrawerNavigationItem(
                        destination = AppDestination.SETTINGS,
                        selected = selectedDestination == AppDestination.SETTINGS,
                        onClick = {
                            selectedDestination = AppDestination.SETTINGS

                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )


                    Spacer(modifier = Modifier.height(28.dp))

                    HorizontalDivider(
                        color = colors.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))


                    // FOOTER

                    Text(
                        text = "STUDY SMART. STAY ORGANIZED.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

    ) {


        // --------------------------------------------------
        // MAIN SCAFFOLD
        // --------------------------------------------------

        Scaffold(
            containerColor = colors.background,

            bottomBar = {

                NavigationBar(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface
                ) {

                    bottomDestinations.forEach { destination ->

                        NavigationBarItem(

                            selected = selectedDestination == destination,

                            onClick = {
                                selectedDestination = destination
                            },

                            icon = {
                                Text(destination.emoji)
                            },

                            label = {
                                Text(destination.label)
                            },

                            alwaysShowLabel = true,

                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.primary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primaryContainer,
                                unselectedIconColor = colors.onSurfaceVariant,
                                unselectedTextColor = colors.onSurfaceVariant
                            )
                        )
                    }
                }
            }

        ) { innerPadding ->


            // --------------------------------------------------
            // SCREEN CONTENT
            // --------------------------------------------------

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                when (selectedDestination) {


                    // HOME

                    AppDestination.HOME -> {

                        DashboardScreen(
                            profile = profile,
                            lectures = scheduledLectures,
                            assignments = calendarAssignments,
                            studySessions = dashboardStudySessions,
                            weeklyGoalMinutes = dashboardWeeklyGoal,
                            reminders = dashboardReminders,

                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // SCHEDULE

                    AppDestination.SCHEDULE -> {

                        ScheduleScreen(
                            viewModel = scheduleViewModel,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // ASSIGNMENTS

                    AppDestination.ASSIGNMENTS -> {

                        AssignmentScreen(
                            viewModel = assignmentViewModel,
                            scheduledSubjects = scheduledSubjects,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // NOTES

                    AppDestination.NOTES -> {

                        NotesScreen(
                            viewModel = noteViewModel,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // PROGRESS

                    AppDestination.PROGRESS -> {

                        ProgressScreen(
                            viewModel = progressViewModel,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // POMODORO

                    AppDestination.POMODORO -> {

                        PomodoroScreen(
                            viewModel = pomodoroViewModel,
                            scheduledSubjects = scheduledSubjects,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // CALENDAR

                    AppDestination.CALENDAR -> {

                        CalendarScreen(
                            lectures = scheduledLectures,
                            assignments = calendarAssignments,
                            exams = calendarExams,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // EXAMS

                    AppDestination.EXAMS -> {

                        ExamScreen(
                            viewModel = examViewModel,
                            scheduledSubjects = scheduledSubjects,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // REMINDERS

                    AppDestination.REMINDERS -> {

                        RemindersScreen(
                            viewModel = reminderViewModel,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // SETTINGS

                    AppDestination.SETTINGS -> {

                        SettingsScreen(
                            profile = profile,
                            selectedTheme = selectedTheme,
                            onThemeChange = onThemeChange,
                            onEditProfile = {
                                selectedDestination = AppDestination.EDIT_PROFILE
                            },
                            reminderViewModel = reminderViewModel,
                            studyPreferences = studyPreferences,
                            progressViewModel = progressViewModel,
                            pomodoroViewModel = pomodoroViewModel,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }


                    // EDIT PROFILE

                    AppDestination.EDIT_PROFILE -> {

                        EditProfileScreen(
                            profile = profile,

                            onSave = { updatedProfile ->

                                onProfileUpdate(updatedProfile)

                                selectedDestination =
                                    AppDestination.SETTINGS
                            },

                            onCancel = {
                                selectedDestination =
                                    AppDestination.SETTINGS
                            }
                        )
                    }
                }
            }
        }
    }
}


// --------------------------------------------------
// DRAWER SECTION HEADING
// --------------------------------------------------

@Composable
private fun DrawerSectionHeading(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            horizontal = 12.dp,
            vertical = 8.dp
        )
    )
}


// --------------------------------------------------
// DRAWER NAVIGATION ITEM
// --------------------------------------------------

@Composable
private fun DrawerNavigationItem(
    destination: AppDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    NavigationDrawerItem(

        icon = {
            Text(
                text = destination.emoji,
                fontSize = 21.sp
            )
        },

        label = {
            Text(
                text = destination.label,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
            )
        },

        selected = selected,

        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),

        shape = RoundedCornerShape(14.dp),

        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = colors.primaryContainer,
            selectedTextColor = colors.onPrimaryContainer,
            selectedIconColor = colors.onPrimaryContainer,
            unselectedTextColor = colors.onSurface,
            unselectedIconColor = colors.onSurfaceVariant
        )
    )
}
