package com.example.studysphere

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.studysphere.data.preferences.ProfilePreferences
import com.example.studysphere.ui.navigation.StudySphereNavigation
import com.example.studysphere.ui.screens.profile.ProfileSetupScreen
import com.example.studysphere.ui.screens.welcome.WelcomeScreen
import com.example.studysphere.ui.theme.StudySphereTheme
import com.example.studysphere.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themePreferences = ThemePreferences(applicationContext)
        val profilePreferences = ProfilePreferences(applicationContext)

        setContent {
            var selectedTheme by remember {
                mutableStateOf(themePreferences.getThemeMode())
            }

            StudySphereTheme(themeMode = selectedTheme) {

                // A saved profile means the user has already completed onboarding.
                // New users (no saved profile) start at the Welcome screen.
                var completedProfile by remember {
                    mutableStateOf(profilePreferences.getProfile())
                }

                var showProfile by remember {
                    mutableStateOf(false)
                }

                var showDashboard by remember {
                    mutableStateOf(completedProfile != null)
                }

                var showSuccessDialog by remember {
                    mutableStateOf(false)
                }

                val notificationPermissionLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        Log.d(
                            "StudySphere",
                            if (isGranted) {
                                "Notification permission granted"
                            } else {
                                "Notification permission denied"
                            }
                        )
                    }

                when {
                    // Returning users go directly to the Dashboard.
                    showDashboard && completedProfile != null -> {
                        completedProfile?.let { profile ->
                            StudySphereNavigation(
                                profile = profile,
                                selectedTheme = selectedTheme,
                                onThemeChange = { newTheme ->
                                    themePreferences.saveThemeMode(newTheme)
                                    selectedTheme = newTheme
                                },
                                onProfileUpdate = { updatedProfile ->
                                    profilePreferences.saveProfile(updatedProfile)
                                    completedProfile = updatedProfile
                                }
                            )
                        }
                    }

                    // New users continue here after tapping Get Started.
                    showProfile -> {
                        ProfileSetupScreen(
                            onBack = {
                                showProfile = false
                            },
                            onContinue = { profile ->
                                profilePreferences.saveProfile(profile)
                                completedProfile = profile
                                showSuccessDialog = true
                            }
                        )
                    }

                    // First-launch Welcome screen.
                    else -> {
                        WelcomeScreen(
                            onGetStarted = {
                                showProfile = true
                            }
                        )
                    }
                }

                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showSuccessDialog = false
                        },
                        title = {
                            Text("Profile Completed! 🎉")
                        },
                        text = {
                            Text(
                                "Great job, ${completedProfile?.name}! " +
                                        "Your StudySphere journey starts now."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showSuccessDialog = false
                                    showProfile = false
                                    showDashboard = true

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val permissionStatus =
                                            ContextCompat.checkSelfPermission(
                                                this@MainActivity,
                                                Manifest.permission.POST_NOTIFICATIONS
                                            )

                                        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                                            notificationPermissionLauncher.launch(
                                                Manifest.permission.POST_NOTIFICATIONS
                                            )
                                        }
                                    }
                                }
                            ) {
                                Text("Go to Dashboard")
                            }
                        }
                    )
                }
            }
        }
    }
}
