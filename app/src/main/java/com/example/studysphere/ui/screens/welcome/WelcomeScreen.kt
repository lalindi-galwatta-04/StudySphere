package com.example.studysphere.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studysphere.ui.theme.StudySphereTheme

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {

        // Decorative background circle
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 190.dp, y = (-90).dp)
                .clip(CircleShape)
                .background(
                    colors.primary.copy(alpha = 0.08f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // App Branding
            Text(
                text = "StudySphere",
                modifier = Modifier.padding(top = 36.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.primary
            )

            // Main Illustration and Welcome Message
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📚",
                        fontSize = 72.sp
                    )
                }

                Spacer(modifier = Modifier.height(38.dp))

                Text(
                    text = "Your University Life,",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Organized.",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Manage your classes, assignments, notes " +
                            "and study goals — all in one place.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "A smarter way to manage your student life.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// LIGHT THEME PREVIEW
@Preview(
    name = "Welcome - Light Theme",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun WelcomeScreenLightPreview() {
    StudySphereTheme(darkTheme = false) {
        WelcomeScreen()
    }
}

// DARK THEME PREVIEW
@Preview(
    name = "Welcome - Dark Theme",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun WelcomeScreenDarkPreview() {
    StudySphereTheme(darkTheme = true) {
        WelcomeScreen()
    }
}
