package com.runanywhere.classconnect.ui.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class CourseBanner(
    val title: String,
    val instructor: String,
    val duration: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavController) {

    val courseList = remember {
        listOf(
            CourseBanner("Python & Machine Learning", "MLExperts", "38 hrs"),
            CourseBanner("MERN Stack Bootcamp", "WebMasters", "55 hrs"),
            CourseBanner("Android Jetpack Compose", "ComposeLabs", "50 hrs"),
            CourseBanner("Master DSA in 60 Days", "Tech Academy", "42 hrs")
        )
    }

    val categories = listOf("AI / ML", "Web Dev", "Android", "Data Science", "Beginner", "Advanced")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF0B1020))
                .padding(16.dp)
        ) {

            FakeSearchBar()

            Spacer(Modifier.height(18.dp))

            Text(
                "Explore Categories",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            CategoryRow(categories)

            Spacer(Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(courseList) { course ->
                    CourseBannerCard(course)
                }
            }
        }
    }
}

@Composable
fun FakeSearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0x22FFFFFF))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = Color.White.copy(0.7f)
        )
        Spacer(Modifier.width(12.dp))
        Text("Search courses...", color = Color.White.copy(0.6f))
    }
}

@Composable
fun CategoryRow(categories: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { cat ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(cat, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CourseBannerCard(course: CourseBanner) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Fake */ },
        colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF))
    ) {

        // Gradient header block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = " ${course.title}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Column(Modifier.padding(16.dp)) {

            Text(
                "Instructor: ${course.instructor}",
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(0.9f),
                fontSize = 14.sp
            )

            Text(
                "Duration: ${course.duration}",
                color = Color.White.copy(0.7f),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667EEA)
                )
            ) {
                Text("Enroll Now")
            }
        }
    }
}
