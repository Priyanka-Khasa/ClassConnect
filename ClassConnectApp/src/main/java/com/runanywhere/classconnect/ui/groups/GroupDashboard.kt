package com.runanywhere.classconnect.ui.groups

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Group(
    val id: String,
    val name: String,
    val members: List<String>,
    val progress: Int,
    val upcomingTask: String,
    val subject: String,
    val memberCount: Int,
    val activeMembers: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDashboard(navController: NavController) {
    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    // Simulate network delay
    LaunchedEffect(Unit) {
        delay(800)
        groups = sampleGroups()
        loading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
    ) {
        GroupsAuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GroupsTopHeader()

            Spacer(Modifier.height(22.dp))

            FrostCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search groups...", color = Color.White.copy(0.5f)) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            GroupsStatsOverview(groups)
            Spacer(Modifier.height(22.dp))

            if (loading) {
                LoadingGroups()
            } else {
                Text(
                    "Your Study Groups",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                val filteredGroups = if (searchQuery.isBlank()) {
                    groups
                } else {
                    groups.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.subject.contains(searchQuery, ignoreCase = true)
                    }
                }

                if (filteredGroups.isEmpty()) {
                    EmptyGroupsState()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredGroups) { group ->
                            PremiumGroupCard(
                                group = group,
                                onOpenChat = { navController.navigate("chat") },
                                onAISuggest = { /* future feature */ }
                            )
                        }
                    }
                }
            }
        }

        // Floating Button (fixed)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            val inf = rememberInfiniteTransition(label = "groups_fab")
            val s by inf.animateFloat(
                1f, 1.1f,
                infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "s"
            )
            FloatingActionButton(
                onClick = { /* add new group */ },
                containerColor = Color(0xFF6C63FF),
                modifier = Modifier.scale(s),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Group", tint = Color.White)
            }
        }
    }
}

@Composable
private fun GroupsAuroraBackground() {
    val inf = rememberInfiniteTransition(label = "aurora")
    val t1 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "t1"
    )

    val t2 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "t2"
    )


    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        fun blob(cx: Float, cy: Float, r: Float, c: List<Color>) {
            drawCircle(
                brush = Brush.radialGradient(c, center = Offset(cx, cy), radius = r),
                radius = r,
                center = Offset(cx, cy)
            )
        }

        blob(w * (0.1f + 0.03f * t1), h * (0.15f + 0.08f * t2), w * 0.4f, listOf(Color(0x334F46E4), Color.Transparent))
        blob(w * (0.9f - 0.04f * t2), h * (0.25f - 0.06f * t1), w * 0.3f, listOf(Color(0x3330E3CA), Color.Transparent))
        blob(w * (0.4f + 0.02f * t2), h * (0.8f), w * 0.5f, listOf(Color(0x33F9A826), Color.Transparent))
    }
}

@Composable
private fun GroupsTopHeader() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Study Groups", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Collaborate & learn together", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
        }
    }
}

@Composable
private fun GroupsStatsOverview(groups: List<Group>) {
    FrostCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            GroupStatItem(groups.size.toString(), "Total Groups", Color(0xFF6C63FF))
            GroupStatItem(groups.sumOf { it.activeMembers }.toString(), "Active Members", Color(0xFF00BFA6))
            val avgProgress = if (groups.isNotEmpty()) groups.sumOf { it.progress } / groups.size else 0
            GroupStatItem("$avgProgress%", "Avg Progress", Color(0xFFFF8A65))
        }
    }
}

@Composable
private fun GroupStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
private fun FrostCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun LoadingGroups() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HaloProgressRing(progress = 0.7f, ringSize = 80.dp)
        Spacer(Modifier.height(16.dp))
        Text("Loading your groups...", color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun EmptyGroupsState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Group, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text("No groups found", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("Create your first study group to get started", color = Color.White.copy(alpha = 0.6f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun PremiumGroupCard(group: Group, onOpenChat: () -> Unit, onAISuggest: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(group.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(group.subject, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                GroupProgressRing(progress = group.progress / 100f)
            }
            Spacer(Modifier.height(12.dp))
            GradientButton("Open Chat", Icons.AutoMirrored.Filled.Chat, listOf(Color(0xFF667EEA), Color(0xFF764BA2)), onOpenChat)
        }
    }
}

@Composable
private fun GroupProgressRing(progress: Float) {
    val safeProgress = if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)
    val anim by animateFloatAsState(targetValue = safeProgress, animationSpec = tween(800), label = "")
    val ringColor = getProgressColor(progress)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(50.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(Color.White.copy(alpha = 0.1f), style = Stroke(4f))
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360 * anim,
                useCenter = false,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }
        Text("${(safeProgress * 100).toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GradientButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, gradient: List<Color>, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            Modifier.background(Brush.horizontalGradient(gradient)).fillMaxSize().padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun HaloProgressRing(progress: Float, ringSize: Dp = 140.dp) {
    val safeProgress = if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)
    val anim by animateFloatAsState(targetValue = safeProgress, animationSpec = tween(800), label = "")
    val ringColor = getProgressColor(progress)

    Box(modifier = Modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize().blur(10.dp, BlurredEdgeTreatment.Unbounded)) {
            drawCircle(color = ringColor.copy(alpha = 0.25f))
        }
        Canvas(modifier = Modifier.matchParentSize()) {
            drawArc(
                color = Color.White.copy(alpha = 0.12f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8f)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * anim,
                useCenter = false,
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
        }
        Text("${(anim * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

private fun getProgressColor(progress: Float): Color =
    when {
        progress >= 0.8f -> Color(0xFF69F0AE)
        progress >= 0.5f -> Color(0xFFFFB74D)
        else -> Color(0xFFFF6E6E)
    }

private fun sampleGroups(): List<Group> = listOf(
    Group("group_1", "DBMS Project Team", listOf("Priyanka", "Neha", "Riya", "Aisha"), 80, "Finish ER Diagram", "Database Management", 4, 3),
    Group("group_2", "AI Research Squad", listOf("Priyanka", "Ananya", "Rahul", "Karan", "Meera"), 65, "Write Literature Review", "Artificial Intelligence", 5, 4),
    Group("group_3", "DSA Practice Group", listOf("Priyanka", "Aisha", "Meera", "Rohan"), 92, "Solve 10 Tree Problems", "Data Structures", 4, 2)
)
