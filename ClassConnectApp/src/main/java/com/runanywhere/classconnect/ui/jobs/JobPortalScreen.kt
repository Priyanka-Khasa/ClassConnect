package com.runanywhere.classconnect.ui.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// ---------------- DATA MODEL -----------------

data class JobListing(
    val title: String,
    val company: String,
    val location: String,
    val jobType: String,
    val salary: String,
    val tags: List<String>
)

// ---------------- MAIN SCREEN -----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobPortalScreen(navController: NavController) {

    var selectedFilter by remember { mutableStateOf("All") }
    var showDetails by remember { mutableStateOf<JobListing?>(null) }

    val jobs = remember {
        listOf(
            JobListing(
                "Android Developer Intern",
                "StartupX Labs",
                "Remote • India",
                "Internship",
                "₹15k – ₹25k / month",
                listOf("Jetpack Compose", "Kotlin")
            ),
            JobListing(
                "Full Stack MERN Engineer",
                "CodeCraft Technologies",
                "Bangalore, India",
                "Full-time",
                "₹6 LPA – ₹12 LPA",
                listOf("React", "Node.js", "MongoDB")
            ),
            JobListing(
                "Machine Learning Intern",
                "AI Research Hub",
                "Remote • Worldwide",
                "Fellowship",
                "₹20k – ₹35k / month",
                listOf("Python", "ML", "Deep Learning")
            ),
            JobListing(
                "SDE 1 – Backend",
                "FinTech Nova",
                "Gurugram, India",
                "Full-time",
                "₹12 LPA – ₹22 LPA",
                listOf("Spring Boot", "PostgreSQL")
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Job Portal", color = Color.White, fontWeight = FontWeight.SemiBold)
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
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.FilterList, null, tint = Color.White)
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
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF060A1A), Color(0xFF0D1533))
                    )
                )
                .padding(16.dp)
        ) {

            // -------- FILTER OPTIONS --------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Remote", "Internships", "AI", "MERN", "SDE").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------- JOB LIST --------
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(jobs) { job ->
                    JobCard(job) {
                        showDetails = job
                    }
                }
            }
        }
    }

    // ---------------- JOB DETAILS MODAL -----------------
    if (showDetails != null) {
        JobDetailsBottomSheet(job = showDetails!!) {
            showDetails = null
        }
    }
}

// ---------------- JOB CARD -----------------

@Composable
fun JobCard(job: JobListing, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x442C3E87))
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    job.title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    job.jobType,
                    color = Color(0xFF80DEEA),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Business,
                    contentDescription = null,
                    tint = Color(0xFFCE93D8),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(job.company, color = Color.White.copy(0.8f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFFFAB91),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(job.location, color = Color.White.copy(0.75f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AccessTime,
                    contentDescription = null,
                    tint = Color(0xFF81D4FA),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(job.salary, color = Color.White.copy(0.75f))
            }

            // Tags
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                job.tags.forEach { t ->
                    AssistChip(onClick = {}, label = { Text(t) })
                }
            }
        }
    }
}

// ---------------- JOB DETAILS SHEET -----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsBottomSheet(job: JobListing, onClose: () -> Unit) {

    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Color(0xFF10172A),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(job.title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(job.company, color = Color.White.copy(0.8f))
            Spacer(Modifier.height(6.dp))
            Text(job.location, color = Color.White.copy(0.7f))
            Spacer(Modifier.height(6.dp))
            Text(job.salary, color = Color.White.copy(0.7f))

            Spacer(Modifier.height(20.dp))

            Text("Required Skills", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            FlowRowRun {
                job.tags.forEach {
                    AssistChip(onClick = {}, label = { Text(it) })
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
            ) {
                Text("Apply Now", color = Color.White)
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

// ------------- FlowRow Alternative (no experimental API) -------------

@Composable
fun FlowRowRun(content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        content()
    }
}
