package com.example.studysphere.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studysphere.ui.screens.profile.ProfileDropdown
import com.example.studysphere.ui.screens.profile.ProfileTextField
import com.example.studysphere.ui.screens.profile.StudentProfile

@Composable
fun EditProfileScreen(
    profile: StudentProfile,
    onSave: (StudentProfile) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember(profile) {
        mutableStateOf(profile.name)
    }

    var university by remember(profile) {
        mutableStateOf(profile.university)
    }

    var degree by remember(profile) {
        mutableStateOf(profile.degree)
    }

    var academicYear by remember(profile) {
        mutableStateOf(profile.academicYear)
    }

    var semester by remember(profile) {
        mutableStateOf(profile.semester)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Update your student information below.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        ProfileTextField(
            label = "Student Name",
            value = name,
            onValueChange = { name = it },
            placeholder = "Enter your full name"
        )

        ProfileTextField(
            label = "University",
            value = university,
            onValueChange = { university = it },
            placeholder = "Enter your university"
        )

        ProfileTextField(
            label = "Degree Programme",
            value = degree,
            onValueChange = { degree = it },
            placeholder = "Enter your degree programme"
        )

        ProfileDropdown(
            label = "Academic Year",
            selectedValue = academicYear,
            options = listOf(
                "Year 1",
                "Year 2",
                "Year 3",
                "Year 4"
            ),
            onSelect = { academicYear = it }
        )

        ProfileDropdown(
            label = "Current Semester",
            selectedValue = semester,
            options = listOf(
                "Semester 1",
                "Semester 2"
            ),
            onSelect = { semester = it }
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                if (
                    name.isBlank() ||
                    university.isBlank() ||
                    degree.isBlank() ||
                    academicYear.isBlank() ||
                    semester.isBlank()
                ) {
                    errorMessage = "Please complete all the fields."
                } else {
                    errorMessage = ""

                    onSave(
                        StudentProfile(
                            name = name.trim(),
                            university = university.trim(),
                            degree = degree.trim(),
                            academicYear = academicYear,
                            semester = semester
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text("Save Changes")
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text("Cancel")
        }
    }
}
