package com.runanywhere.classconnect.ui.matchmaking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.random.Random

data class StudentMatch(
    val id: Int,
    val name: String,
    val course: String,
    val year: String,
    val sharedSkills: List<String>,
    val matchScore: Int,
    val availability: String,
    val avatarColor: Color,
    val online: Boolean
)

private val gradientBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF050816),
        Color(0xFF0B1120),
        Color(0xFF020617)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakingScreen(navController: NavController) {

    var selectedCategory by remember { mutableStateOf("All") }
    val allMatches = remember { generateFakeMatches() }

    val filteredMatches = when (selectedCategory) {
        "Study" -> allMatches.filter {
            it.course.contains("DSA", true) ||
                    it.course.contains("Exam", true)
        }

        "Project" -> allMatches.filter {
            it.course.contains("Project", true) ||
                    it.course.contains("Hackathon", true)
        }

        "Revision" -> allMatches.filter {
            it.course.contains("Revision", true) ||
                    it.course.contains("Mock", true)
        }

        else -> allMatches
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Smart Matchmaking",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Find your perfect study buddy",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                MatchmakingHeader()
                Spacer(Modifier.height(16.dp))

                MatchCategoryRow(
                    selected = selectedCategory,
                    onSelectedChange = { selectedCategory = it }
                )

                Spacer(Modifier.height(12.dp))

                if (filteredMatches.isEmpty()) {
                    EmptyMatchesState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(filteredMatches, key = { it.id }) { match ->
                            MatchCard(
                                match = match,
                                onStartChat = { navController.navigate("chat") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchmakingHeader() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF0F172A)
        ),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF38BDF8),
                                Color(0xFF1D4ED8)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "AI-powered matchmaking",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    "We match you with peers based on skills, goals & productivity time.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MatchCategoryRow(
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    val categories = listOf("All", "Study", "Project", "Revision")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->

            FilterChip(
                selected = selected == category,
                onClick = { onSelectedChange(category) },
                label = {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        color = if (selected == category) Color(0xFF0F172A) else Color.White
                    )
                },
                leadingIcon = if (category == "All") {
                    {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF38BDF8),
                    containerColor = Color(0xFF0F172A),
                    selectedLabelColor = Color(0xFF0F172A),
                    labelColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            )
        }
    }
}

@Composable
private fun MatchCard(
    match: StudentMatch,
    onStartChat: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,          // ⭐ FIXED: No ripple override → no crash
                onClick = onStartChat
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF020617).copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(match.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = match.name.first().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    if (match.online) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(10.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFF020617), CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            match.name,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF22C55E).copy(alpha = 0.18f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "${match.matchScore}%",
                                color = Color(0xFF22C55E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        "${match.course} • ${match.year}",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )

                    Text(
                        "Best time: ${match.availability}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.08f))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Shared skills",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    match.sharedSkills.take(3).forEach { skill ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(skill, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFACC15),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "High compatibility",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onStartChat,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF38BDF8)
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Start Chat",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMatchesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.GroupOff,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("No matches found", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            "Try adjusting your filters or check back later",
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
        )
    }
}

private fun generateFakeMatches(): List<StudentMatch> {
    val names = listOf(
        "Aarav Sharma", "Priya Verma", "Rohan Gupta", "Ishita Singh",
        "Kabir Mehta", "Ananya Iyer", "Vikram Rao", "Sara Khan"
    )
    val courses = listOf(
        "DSA Revision Sprint",
        "Operating Systems Project",
        "DBMS Mock Interviews",
        "Web Dev Hackathon Team",
        "GATE 2026 Study Group",
        "System Design Deep Dive"
    )
    val years = listOf("1st year", "2nd year", "3rd year", "Final year")
    val skills = listOf(
        "DSA in Java",
        "System Design",
        "React & Node.js",
        "Machine Learning",
        "Competitive Programming",
        "Android (Kotlin)",
        "DevOps Basics"
    )
    val colors = listOf(
        Color(0xFF38BDF8),
        Color(0xFFF97316),
        Color(0xFF22C55E),
        Color(0xFFE11D48),
        Color(0xFFA855F7),
        Color(0xFFEC4899)
    )

    return names.mapIndexed { index, name ->
        StudentMatch(
            id = index,
            name = name,
            course = courses.random(),
            year = years.random(),
            sharedSkills = skills.shuffled().take(Random.nextInt(2, 5)),
            matchScore = Random.nextInt(70, 98),
            availability = listOf("6-9 PM", "3-6 PM", "Morning", "Weekend", "Flexible").random(),
            avatarColor = colors[index % colors.size],
            online = Random.nextBoolean()
        )
    }.shuffled()
}
