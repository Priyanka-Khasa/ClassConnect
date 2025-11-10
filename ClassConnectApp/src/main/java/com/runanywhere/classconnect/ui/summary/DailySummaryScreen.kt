package com.runanywhere.classconnect.ui.summary

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.classconnect.ui.focus.FocusStorage
import com.runanywhere.classconnect.ui.focus.FocusSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class DailySummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todaySessions: List<FocusSession> = runBlocking { FocusStorage.loadSessions(this@DailySummaryActivity) }
            .filter { session ->
                val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                date.format(Date(session.startTime)) == date.format(Date())
            }

        val totalTimeSec = todaySessions.sumOf { it.durationSec }
        val totalTimeMin = totalTimeSec / 60

        val completedTasks = intent.getIntExtra("completed", 0)
        val pendingTasks = intent.getIntExtra("pending", 0)

        setContent {
            // Beautiful gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2),
                                Color(0xFFf093fb)
                            )
                        )
                    )
            ) {
                DailySummaryScreen(
                    studyMinutes = totalTimeMin,
                    completed = completedTasks,
                    pending = pendingTasks
                )
            }
        }
    }
}

@Composable
fun DailySummaryScreen(studyMinutes: Int, completed: Int, pending: Int) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition()

    // Floating animation for main card
    val floatAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with animated emoji
        AnimatedHeader()

        // Main Stats Card with floating animation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    clip = true
                )
                .offset(y = floatAnimation.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Study Time Card
                StatCard(
                    icon = Icons.Default.Schedule,
                    title = "Study Time",
                    value = "$studyMinutes",
                    unit = "minutes",
                    color = Color(0xFF667eea),
                    gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                    )
                )

                // Completed Tasks Card
                StatCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Tasks Completed",
                    value = "$completed",
                    unit = "tasks",
                    color = Color(0xFF4CAF50),
                    gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
                    )
                )

                // Pending Tasks Card
                StatCard(
                    icon = Icons.Default.Task,
                    title = "Tasks Pending",
                    value = "$pending",
                    unit = "tasks",
                    color = Color(0xFFFFB74D),
                    gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800))
                    )
                )
            }
        }

        // Progress Ring
        ProgressRing(
            completed = completed,
            pending = pending,
            studyMinutes = studyMinutes
        )

        // Motivational Message
        MotivationalMessage(completed, studyMinutes)

        // Close Button
        Button(
            onClick = {
                (context as? Activity)?.finishAffinity()
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            border = BorderStroke(2.dp, Color(0xFF667eea))
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = Color(0xFF667eea)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Close App",
                color = Color(0xFF667eea),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AnimatedHeader() {
    var emojiIndex by remember { mutableStateOf(0) }
    val emojis = listOf("🎯", "🚀", "⭐", "🏆", "💫")

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            emojiIndex = (emojiIndex + 1) % emojis.size
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = emojis[emojiIndex],
            fontSize = 48.sp
        )

        Text(
            text = "Today's Summary",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Great work today! Keep shining",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    unit: String,
    color: Color,
    gradient: Brush
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(gradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = Color.Black.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = unit,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProgressRing(completed: Int, pending: Int, studyMinutes: Int) {
    val totalTasks = completed + pending
    val progress = if (totalTasks > 0) completed.toFloat() / totalTasks.toFloat() else 0f

    Card(
        modifier = Modifier
            .size(200.dp)
            .shadow(8.dp, RoundedCornerShape(100.dp)),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Progress circle
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(180.dp),
                color = Color(0xFF667eea),
                strokeWidth = 8.dp,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color(0xFF667eea),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Completion",
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun MotivationalMessage(completed: Int, studyMinutes: Int) {
    val message = when {
        completed >= 5 && studyMinutes >= 60 -> " You're on fire! Amazing productivity!"
        completed >= 3 -> " Excellent work! You're making great progress!"
        studyMinutes >= 30 -> "Good start! Every minute counts!"
        else -> "Every journey begins with a single step!"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF667eea),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}