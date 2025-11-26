package com.runanywhere.classconnect.ui.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


// --------------------------------------------------
// RESULT MODEL
// --------------------------------------------------
data class ReviewResult(
    val atsScore: Int,
    val recruiterScore: Int,
    val summary: String,
    val skillsMatch: List<String>,
    val improvement: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeReviewScreen(
    navController: NavController
) {
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ReviewResult?>(null) }

    // Smooth Loading
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            delay(1700)

            result = ReviewResult(
                atsScore = (70..99).random(),
                recruiterScore = (75..95).random(),
                summary = "Your resume structure is clear and well-suited for tech roles.",
                skillsMatch = listOf("Java", "React", "Machine Learning", "DSA", "Problem Solving"),
                improvement = "Add quantified achievements, reorder important skills at the top, and attach GitHub + LinkedIn links."
            )

            isAnalyzing = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Resume Review", color = Color.White, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF050916)
                )
            )
        },
        containerColor = Color(0xFF050916)
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF050916), Color(0xFF0D1326))
                    )
                )
                .padding(18.dp)
        ) {

            // --------------------------------------------------
            // UPLOAD CARD
            // --------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x332C3E87))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.FileUpload, null, tint = Color(0xFFB39DDB), modifier = Modifier.size(34.dp))

                    Text("Upload your Resume (PDF)",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (selectedFile == null) {
                        Text("No file selected",
                            color = Color.White.copy(0.6f)
                        )
                    } else {
                        Text(selectedFile!!,
                            color = Color(0xFF80DEEA),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            selectedFile = "Priyanka_Resume.pdf"
                            result = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Choose PDF")
                    }

                    Button(
                        onClick = {
                            if (selectedFile != null) {
                                result = null
                                isAnalyzing = true
                            }
                        },
                        enabled = selectedFile != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Analyze Resume")
                    }
                }
            }

            // --------------------------------------------------
            // LOADING VIEW
            // --------------------------------------------------
            if (isAnalyzing) {
                Spacer(Modifier.height(40.dp))
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF7C4DFF))
                    Spacer(Modifier.height(12.dp))
                    Text("Scanning your resume...", color = Color.White.copy(0.85f))
                }
                return@Column
            }

            // --------------------------------------------------
            // RESULT VIEW
            // --------------------------------------------------
            if (result != null) {
                Spacer(Modifier.height(26.dp))

                Text(
                    "Review Summary",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(14.dp))

                ResumeATSCard(result!!)
            }
        }
    }
}

// --------------------------------------------------
// MAIN RESULT CARD
// --------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResumeATSCard(result: ReviewResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x442C3E87))
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ATS & Recruiter Score Row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreBox("ATS Score", result.atsScore)
                ScoreBox("Recruiter Match", result.recruiterScore)
            }

            // Summary
            Text(
                result.summary,
                color = Color.White.copy(0.85f)
            )

            // Skills Match
            Text(
                "Matched Skills:",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow {
                result.skillsMatch.forEach { skill ->
                    AssistChip(
                        onClick = {},
                        label = { Text(skill) },
                        modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
                    )
                }
            }

            // Improvement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x3321214A))
                    .padding(12.dp)
            ) {
                Icon(
                    Icons.Filled.TipsAndUpdates,
                    contentDescription = null,
                    tint = Color(0xFF80DEEA),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    result.improvement,
                    color = Color.White.copy(0.85f)
                )
            }
        }
    }
}


// --------------------------------------------------
// SCORE BOX
// --------------------------------------------------
@Composable
fun ScoreBox(title: String, score: Int) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x221F2C78))
            .padding(12.dp)
            .width(140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = Color.White.copy(0.8f))
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD54F))
            Spacer(Modifier.width(4.dp))
            Text(
                "$score / 100",
                color = Color(0xFFFFD54F),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
