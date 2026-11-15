package com.example.studysphere.ui.screens.profile

import androidx.compose.foundation.background
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
import com.example.studysphere.ui.theme.StudySphereTheme

data class StudentProfile(
    val name: String,
    val university: String,
    val degree: String,
    val academicYear: String,
    val semester: String
)

@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit = {},
    onContinue: (StudentProfile) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    var name by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {

        // Back button
        IconButton(onClick = onBack) {
            Text(
                text = "←",
                color = colors.primary,
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Heading
        Text(
            text = "Let's get to know you!",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Set up your student profile to personalize your StudySphere experience.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Profile icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp),
                color = colors.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "👩‍🎓",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Student name
        ProfileTextField(
            label = "Student Name",
            value = name,
            onValueChange = { name = it },
            placeholder = "Enter your full name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // University
        ProfileTextField(
            label = "University",
            value = university,
            onValueChange = { university = it },
            placeholder = "Enter your university"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Degree
        ProfileTextField(
            label = "Degree Programme",
            value = degree,
            onValueChange = { degree = it },
            placeholder = "e.g. BSc in Computer Science"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Academic year
        ProfileDropdown(
            label = "Academic Year",
            selectedValue = academicYear,
            options = listOf(
                "Year 1",
                "Year 2",
                "Year 3",
                "Year 4",
            ),
            onSelect = { academicYear = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Semester
        ProfileDropdown(
            label = "Current Semester",
            selectedValue = semester,
            options = listOf(
                "Semester 1",
                "Semester 2"
            ),
            onSelect = { semester = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Validation message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = colors.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Continue button
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

                    onContinue(
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
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your academic journey starts here 💜",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall,
            color = colors.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Reusable text field
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

// Reusable dropdown
@Composable
fun ProfileDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Select $label") },
                readOnly = true,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Text(
                            text = "⌄",
                            fontSize = 24.sp
                        )
                    }
                }
            )

            // Opens the dropdown when the field is tapped
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 48.dp)
            ) {
                // Dropdown menu is attached below
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Profile Setup - Light",
    showBackground = true
)
@Composable
fun ProfileSetupLightPreview() {
    StudySphereTheme(darkTheme = false) {
        ProfileSetupScreen()
    }
}

@Preview(
    name = "Profile Setup - Dark",
    showBackground = true
)
@Composable
fun ProfileSetupDarkPreview() {
    StudySphereTheme(darkTheme = true) {
        ProfileSetupScreen()
    }
}
