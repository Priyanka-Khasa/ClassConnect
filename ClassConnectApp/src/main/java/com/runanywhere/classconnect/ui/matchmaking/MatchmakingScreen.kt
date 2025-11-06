package com.runanywhere.classconnect.ui.matchmaking

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
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
    val online: Boolean = Random.nextBoolean()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakingScreen(navController: NavController) {
    var matches by remember { mutableStateOf(emptyList<StudentMatch>()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var refreshTrigger by remember { mutableStateOf(false) }

    // Simulate AI matchmaking
    LaunchedEffect(Unit) {
        delay(2000)
        matches = generateSampleMatches()
        isLoading = false
    }

    // Refresh trigger effect
    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger) {
            delay(1500)
            matches = generateSampleMatches()
            isLoading = false
        }
    }

    val filteredMatches = matches.filter { match ->
        (selectedFilter == "All" || match.sharedSkills.any { it.contains(selectedFilter, true) }) &&
                (searchQuery.isEmpty() || match.name.contains(searchQuery, true) ||
                        match.course.contains(searchQuery, true) ||
                        match.sharedSkills.any { it.contains(searchQuery, true) })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MatchmakingHeader()

            Spacer(Modifier.height(20.dp))

            SearchFilterSection(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it }
            )

            Spacer(Modifier.height(16.dp))

            MatchStats(matches = filteredMatches)

            Spacer(Modifier.height(20.dp))

            when {
                isLoading -> LoadingAnimation()
                filteredMatches.isEmpty() -> EmptyMatchesState()
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredMatches) { match ->
                            EnhancedMatchCard(
                                match = match,
                                onInvite = { navController.navigate("chat/${match.name}") }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                isLoading = true
                refreshTrigger = !refreshTrigger
            },
            containerColor = Color(0xFFFF6B6B),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
        }
    }
}

@Composable
fun MatchmakingHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "🔍 Find Your Perfect Study Partner",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Connect with students who share your learning goals",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SearchFilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    "Search by name, course, or skill...",
                    color = Color.White.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(12.dp))

        val filters = listOf("All", "AI", "ML", "DSA", "DBMS", "Web", "Mobile")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = {
                        Text(
                            filter,
                            color = if (selectedFilter == filter) Color.White else Color.White.copy(alpha = 0.8f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF6B6B),
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}

@Composable
fun MatchStats(matches: List<StudentMatch>) {
    val totalMatches = matches.size
    val averageScore = if (matches.isNotEmpty()) matches.map { it.matchScore }.average().toInt() else 0
    val onlineCount = matches.count { it.online }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatItem(Icons.Default.People, "$totalMatches", "Matches")
        StatItem(Icons.Default.TrendingUp, "$averageScore%", "Avg Score")
        StatItem(Icons.Default.Circle, "$onlineCount", "Online", Color(0xFF4CAF50))
    }
}

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, tint: Color = Color.White.copy(0.7f)) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun EnhancedMatchCard(match: StudentMatch, onInvite: () -> Unit) {
    val animatedProgress by animateFloatAsState(
        targetValue = match.matchScore / 100f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInvite() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(match.avatarColor, CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                match.name.split(" ").map { it.first() }.joinToString(""),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        if (match.online) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                                    .align(Alignment.TopEnd)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(match.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${match.course} • ${match.year}", color = Color.White.copy(0.7f), fontSize = 13.sp)
                        Text("Available: ${match.availability}", color = Color(0xFFFFB74D), fontSize = 12.sp)
                    }
                }
                AnimatedMatchScore(match.matchScore, animatedProgress)
            }

            Spacer(Modifier.height(16.dp))
            Text("Shared Skills:", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                match.sharedSkills.forEach { skill ->
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            skill,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onInvite,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Start Chat", color = Color.White)
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Profile", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AnimatedMatchScore(score: Int, progress: Float) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = Color.White.copy(alpha = 0.1f), style = Stroke(6f))
        }
        Canvas(modifier = Modifier.matchParentSize()) {
            drawArc(
                color = when {
                    score >= 90 -> Color(0xFF4CAF50)
                    score >= 75 -> Color(0xFFFFB74D)
                    else -> Color(0xFFFF5252)
                },
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(6f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$score%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Match", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
        }
    }
}

@Composable
fun LoadingAnimation() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse)
        )
        Icon(Icons.Default.PersonSearch, contentDescription = "Searching", tint = Color.White, modifier = Modifier.size(80.dp).scale(scale))
        Spacer(Modifier.height(16.dp))
        Text("Finding your perfect study partners...", color = Color.White.copy(alpha = 0.8f))
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(color = Color.White, modifier = Modifier.fillMaxWidth(0.6f))
    }
}

@Composable
fun EmptyMatchesState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Icon(Icons.Default.GroupOff, contentDescription = "No matches", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(16.dp))
        Text("No matches found", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Try adjusting your filters or check back later", color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
    }
}

fun generateSampleMatches(): List<StudentMatch> {
    val skills = listOf("AI", "ML", "DSA", "DBMS", "Web Dev", "Mobile", "Python", "Java", "React", "Kotlin")
    val names = listOf("Neha Sharma", "Riya Kapoor", "Aisha Khan", "Ananya Singh", "Rahul Verma", "Arjun Patel", "Sneha Reddy", "Vikram Joshi")
    val courses = listOf("Computer Science", "AI & ML", "Data Science", "Software Engineering")
    val years = listOf("2nd Year", "3rd Year", "4th Year", "Graduate")
    val colors = listOf(Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF45B7D1), Color(0xFF96CEB4), Color(0xFFFECA57), Color(0xFFFF9FF3), Color(0xFF54A0FF), Color(0xFF5F27CD))

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
