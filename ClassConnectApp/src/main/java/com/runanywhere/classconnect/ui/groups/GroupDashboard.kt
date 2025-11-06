package com.runanywhere.classconnect.ui.groups

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Group(
    val id: String, // Added unique identifier
    val name: String,
    val members: List<String>,
    val progress: Int,
    val upcomingTask: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GroupDashboard(navController: NavController) {
    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1500)
        groups = sampleGroups()
        loading = false
    }

    val infiniteTransition = rememberInfiniteTransition()
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Safe background color for fallback
    val backgroundColor = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF232526), Color(0xFF414345)),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        if (loading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading your teams...", color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "👩‍💻 Your Study Groups",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        count = groups.size,
                        key = { index -> groups[index].id } // Add key for better performance
                    ) { index ->
                        GroupCard(
                            group = groups[index],
                            onOpenChat = {
                                navController.navigate("chat")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCard(group: Group, onOpenChat: () -> Unit) {
    val progressAnim by animateFloatAsState(
        targetValue = group.progress / 100f,
        animationSpec = tween(1000),
        label = "progress_animation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with group name and progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Progress circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(50.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.2f),
                            style = Stroke(6f)
                        )
                        drawArc(
                            color = Color(0xFF4CAF50),
                            startAngle = -90f,
                            sweepAngle = 360 * progressAnim,
                            useCenter = false,
                            style = Stroke(6f)
                        )
                    }
                    Text(
                        text = "${group.progress}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Members section
            Text(
                text = "Members:",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                group.members.forEach { member ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00BCD4).copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.first().toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Upcoming task
            Text(
                text = "Next Task: ${group.upcomingTask}",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onOpenChat,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open Chat")
                }

                OutlinedButton(
                    onClick = {
                        // AI Suggest Task functionality
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Suggest Task")
                }
            }
        }
    }
}

private fun sampleGroups(): List<Group> = listOf(
    Group(
        id = "group_1",
        name = "DBMS Project Team",
        members = listOf("Priyanka", "Neha", "Riya"),
        progress = Random.nextInt(60, 95),
        upcomingTask = "Finish ER Diagram"
    ),
    Group(
        id = "group_2",
        name = "AI Research Squad",
        members = listOf("Priyanka", "Ananya", "Rahul"),
        progress = Random.nextInt(40, 85),
        upcomingTask = "Write Literature Review"
    ),
    Group(
        id = "group_3",
        name = "DSA Practice Group",
        members = listOf("Priyanka", "Aisha", "Meera"),
        progress = Random.nextInt(70, 100),
        upcomingTask = "Solve 10 Tree Problems"
    )
)