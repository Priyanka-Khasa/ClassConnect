package com.runanywhere.classconnect.ui.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ResumeBuilderSection(
    val title: String,
    val subtitle: String,
    val hint: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(
    navController: NavController
) {

    // -------- Resume Sections (Fake Only) --------
    val sections = remember {
        listOf(
            ResumeBuilderSection(
                "Basic Information",
                "Name • Email • Phone • Title",
                "Tap to view sample edit layout",
                Icons.Filled.Edit
            ),
            ResumeBuilderSection(
                "Education Details",
                "University • Degree • CGPA",
                "UI-only preview screen",
                Icons.Filled.UploadFile
            ),
            ResumeBuilderSection(
                "Skills Section",
                "Technical & Soft Skills",
                "Displayed as editable chips (fake)",
                Icons.Filled.DesignServices
            ),
            ResumeBuilderSection(
                "Projects",
                "Display your best work",
                "Includes fake 'Add Project' action",
                Icons.Filled.Edit
            ),
            ResumeBuilderSection(
                "Experience",
                "Internships • Roles • Durations",
                "Shown in preview card only",
                Icons.Filled.Edit
            ),
            ResumeBuilderSection(
                "Achievements",
                "Certificates • Awards • Highlights",
                "Static preview, no real editing",
                Icons.Filled.Edit
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Resume Builder",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF060A1A)
                )
            )
        },
        containerColor = Color(0xFF060A1A)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF060A1A), Color(0xFF0D1533))
                    )
                )
                .padding(16.dp)
        ) {

            // -------- Top Card --------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x332C3E87))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            Icons.Filled.FileUpload,
                            contentDescription = null,
                            tint = Color(0xFF9FA8FF),
                            modifier = Modifier.size(32.dp)
                        )

                        Column {
                            Text(
                                "Student Resume Generator",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Beautiful UI simulation. No real editing or PDF.",
                                color = Color.White.copy(0.65f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* fake click */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF9FA8FF)
                            )
                        ) {
                            Text("Import Resume")
                        }

                        Button(
                            onClick = { /* fake click */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C63FF)
                            )
                        ) {
                            Text("Start Fresh")
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Resume Sections",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(Modifier.height(10.dp))

            // -------- Fake Section List --------
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, true)
            ) {
                items(sections) { sec ->
                    ResumeBuilderSectionCard(sec)
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------- Fake Generate Button --------
            Button(
                onClick = { /* Do nothing, UI-only */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C4DFF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Edit, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Preview Resume (UI Only)")
            }
        }
    }
}

@Composable
fun ResumeBuilderSectionCard(sec: ResumeBuilderSection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* fake click */ },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x442A2F63))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Icon(
                sec.icon,
                contentDescription = null,
                tint = Color(0xFFB39DDB),
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(sec.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(sec.subtitle, color = Color.White.copy(0.8f), style = MaterialTheme.typography.bodySmall)
                Text(sec.hint, color = Color(0xFF9FA8FF), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}