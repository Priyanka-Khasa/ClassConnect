
package com.runanywhere.classconnect.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.runanywhere.classconnect.model.UserProfile
import com.runanywhere.classconnect.util.SessionManager
import com.runanywhere.classconnect.viewmodels.ChatViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class Task(val title: String, val priority: String, val isCompleted: Boolean, val id: Int)
data class QuickAction(val title: String, val icon: ImageVector, val route: String, val color: Color)
data class Reminder(val title: String, val time: String, val isActive: Boolean, val id: Int)
data class FocusSession(val date: String, val duration: Int, val score: Int, val mood: String = "😊")

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: ChatViewModel,
    sessionManager: SessionManager
) {
    val userProfile by sessionManager.userProfile.collectAsState(initial = null)

    var tasks by remember {
        mutableStateOf(
            listOf(
                Task("Complete DBMS Assignment", "High", false, 1),
                Task("Revise DSA Notes", "Medium", false, 2),
                Task("Prepare AI Project Pitch", "High", true, 3),
                Task("Read Research Paper", "Low", false, 4),
            )
        )
    }

    var reminders by remember {
        mutableStateOf(
            listOf(
                Reminder("Group Meeting", "2:00 PM", true, 1),
                Reminder("Submit Assignment", "11:59 PM", true, 2),
            )
        )
    }

    val focusSessions = listOf(
        FocusSession("Mon", 45, 85, "😊"),
        FocusSession("Tue", 100, 90, "🚀"),
        FocusSession("Wed", 75, 78, "😴"),
        FocusSession("Thu", 120, 92, "🔥"),
        FocusSession("Fri", 60, 75, "😅"),
        FocusSession("Sat", 90, 88, "😊"),
        FocusSession("Sun", 0, 0, "🌴")
    )

    var showTaskDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf("") }
    var newReminderTitle by remember { mutableStateOf("") }
    var newReminderTime by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }

    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("EEE, dd MMM  ·  hh:mm a", Locale.getDefault()).format(Date())
            delay(60_000)
        }
    }

    // ALL IMPORTANT QUICK ACTIONS INCLUDED
    val quickActions = listOf(
        QuickAction("Matchmaking", Icons.Default.People, "matchmaking", Color(0xFF6C63FF)),
        QuickAction("Study Groups", Icons.Default.Group, "groups", Color(0xFF00BFA6)),
        QuickAction("Workspace", Icons.Default.Work, "workspace", Color(0xFFFF8A65)),
        QuickAction("Timeline", Icons.Default.DateRange, "timeline", Color(0xFFFFC400)),
        QuickAction("Focus Mode", Icons.Default.CenterFocusStrong, "focus", Color(0xFF7C4DFF)),
        QuickAction("Leaderboard", Icons.Default.Leaderboard, "focusLeaderboard", Color(0xFF7C4DFF)),
        QuickAction("Courses", Icons.Default.MenuBook, "courses", Color(0xFF42A5F5)),
        QuickAction("Resume Builder", Icons.Default.Edit, "resumeBuilder", Color(0xFF7C4DFF)),
        QuickAction("Resume Checker", Icons.Default.CheckCircle, "resumeReview", Color(0xFF26C6DA)),
        QuickAction("Job Portal", Icons.Default.Work, "jobPortal", Color(0xFFFFB74D)),
        QuickAction("Tools Hub", Icons.Default.Build, "toolsHub", Color(0xFF14B8A6))
    )

    // Animated floating particles
    val infiniteTransition = rememberInfiniteTransition()
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1C))
    ) {
        // Enhanced background with particles
        AnimatedBackground(particleOffset)

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // Enhanced Header with Glass Morphism
            GlassHeader(
                currentTime = currentTime,
                userProfile = userProfile,
                onAvatarClick = { navController.navigate("profile") }
            )

            Spacer(Modifier.height(24.dp))

            // TODAY'S OVERVIEW - Glass cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pending Tasks
                GlassStatCard(
                    title = "Pending",
                    value = tasks.count { !it.isCompleted }.toString(),
                    subtitle = "Tasks",
                    color = Color(0xFFFF6B6B),
                    modifier = Modifier.weight(1f)
                )

                // Focus Score
                GlassStatCard(
                    title = "Focus",
                    value = "${focusSessions.last().score}",
                    subtitle = "Today",
                    color = Color(0xFF4ECDC4),
                    modifier = Modifier.weight(1f)
                )

                // Productivity
                GlassStatCard(
                    title = "Productiv",
                    value = "${if (tasks.isNotEmpty()) (tasks.count { it.isCompleted }.toFloat() / tasks.size * 100).toInt() else 0}%",
                    subtitle = "Today",
                    color = Color(0xFFBA68C8),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // QUICK ACTIONS - Redesigned as TWO ROWS for better organization
            QuickActionsSection(quickActions, navController)

            Spacer(Modifier.height(24.dp))

            // FOCUS ANALYTICS - Completely redesigned
            FocusAnalyticsSection(focusSessions)

            Spacer(Modifier.height(24.dp))

            // TASKS - Modern design
            TasksSection(
                tasks = tasks,
                onTaskToggle = { id ->
                    tasks = tasks.map {
                        if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                    }
                },
                onTaskDelete = { id ->
                    tasks = tasks.filterNot { it.id == id }
                }
            )

            Spacer(Modifier.height(24.dp))

            // REMINDERS - Modern design with add option
            RemindersSection(
                reminders = reminders,
                onReminderToggle = { id ->
                    reminders = reminders.map {
                        if (it.id == id) it.copy(isActive = !it.isActive) else it
                    }
                },
                onAddReminder = { showReminderDialog = true }
            )

            Spacer(Modifier.height(80.dp))
        }

        // Dual Floating Action Buttons for both Tasks and Reminders
        DualFloatingButtons(
            onAddTask = { showTaskDialog = true },
            onAddReminder = { showReminderDialog = true }
        )
    }

    // Task Dialog
    if (showTaskDialog) {
        AddTaskDialog(
            newTask = newTask,
            selectedPriority = selectedPriority,
            onTaskChange = { newTask = it },
            onPriorityChange = { selectedPriority = it },
            onDismiss = {
                showTaskDialog = false
                newTask = ""
            },
            onAddTask = {
                if (newTask.isNotBlank()) {
                    val id = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                    tasks = tasks + Task(newTask.trim(), selectedPriority, false, id)
                    newTask = ""
                    showTaskDialog = false
                }
            }
        )
    }

    // Reminder Dialog
    if (showReminderDialog) {
        AddReminderDialog(
            newReminderTitle = newReminderTitle,
            newReminderTime = newReminderTime,
            onTitleChange = { newReminderTitle = it },
            onTimeChange = { newReminderTime = it },
            onDismiss = {
                showReminderDialog = false
                newReminderTitle = ""
                newReminderTime = ""
            },
            onAddReminder = {
                if (newReminderTitle.isNotBlank() && newReminderTime.isNotBlank()) {
                    val id = (reminders.maxOfOrNull { it.id } ?: 0) + 1
                    reminders = reminders + Reminder(newReminderTitle.trim(), newReminderTime.trim(), true, id)
                    newReminderTitle = ""
                    newReminderTime = ""
                    showReminderDialog = false
                }
            }
        )
    }
}

@Composable
private fun DualFloatingButtons(
    onAddTask: () -> Unit,
    onAddReminder: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Main FAB
        Card(
            onClick = { expanded = !expanded },
            colors = CardDefaults.cardColors(containerColor = Color(0x80FFFFFF)),
            shape = CircleShape,
            modifier = Modifier
                .size(64.dp)
                .padding(20.dp)
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF0A0F1C),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Task FAB - appears when expanded
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideIn(initialOffset = { IntOffset(0, 40) }),
            exit = fadeOut() + slideOut(targetOffset = { IntOffset(0, 40) })
        ) {
            Card(
                onClick = {
                    onAddTask()
                    expanded = false
                },
                colors = CardDefaults.cardColors(containerColor = Color(0x8067C6EA)),
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .padding(bottom = 90.dp, end = 24.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Task,
                        contentDescription = "Add Task",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Reminder FAB - appears when expanded
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideIn(initialOffset = { IntOffset(0, 80) }),
            exit = fadeOut() + slideOut(targetOffset = { IntOffset(0, 80) })
        ) {
            Card(
                onClick = {
                    onAddReminder()
                    expanded = false
                },
                colors = CardDefaults.cardColors(containerColor = Color(0x804CAF50)),
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .padding(bottom = 150.dp, end = 24.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Add Reminder",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(actions: List<QuickAction>, navController: NavController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Quick Actions",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${actions.size} features",
                color = Color.White.copy(0.6f),
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(16.dp))

        // First row - Study Features
        Text(
            "Study & Productivity",
            color = Color.White.copy(0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(actions.take(6)) { action ->
                GlassActionCard(action) { navController.navigate(it.route) }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Second row - Career Features
        Text(
            "Career & Jobs",
            color = Color.White.copy(0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(actions.drop(6)) { action ->
                GlassActionCard(action) { navController.navigate(it.route) }
            }
        }
    }
}

@Composable
private fun GlassActionCard(action: QuickAction, onClick: (QuickAction) -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Card(
        onClick = { onClick(action) },
        colors = CardDefaults.cardColors(containerColor = Color(0x40FFFFFF)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .size(110.dp, 95.dp)
            .graphicsLayer {
                rotationZ = rotation * 0.01f // Subtle rotation effect
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(action.color.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    action.icon,
                    contentDescription = null,
                    tint = action.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                action.title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun GlassHeader(
    currentTime: String,
    userProfile: UserProfile?,
    onAvatarClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x40FFFFFF)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Welcome back! 👋",
                    color = Color.White.copy(0.8f),
                    fontSize = 14.sp
                )
                Text(
                    currentTime,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Enhanced Avatar with glow effect
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF667EEA), Color.Transparent),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width / 2
                            ),
                            radius = size.width / 2
                        )
                    }
                    .clip(CircleShape)
                    .background(Color(0x44FFFFFF))
                    .border(1.dp, Color.White.copy(0.3f), CircleShape)
                    .pointerInput(Unit) { detectTapGestures { onAvatarClick() } },
                contentAlignment = Alignment.Center
            ) {
                val imageUri = userProfile?.imageUri
                val letter = userProfile?.name?.firstOrNull()?.uppercase() ?: "U"

                if (!imageUri.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Text(letter, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GlassStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x40FFFFFF)),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                title,
                color = Color.White.copy(0.7f),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                subtitle,
                color = Color.White.copy(0.5f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun FocusAnalyticsSection(sessions: List<FocusSession>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x40FFFFFF)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Focus Analytics",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "This Week",
                    color = Color.White.copy(0.6f),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))

            // Enhanced chart with mood indicators
            FocusChartWithMood(sessions)

            Spacer(Modifier.height(16.dp))

            // Weekly stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Avg. Score", "${sessions.filter { it.score > 0 }.map { it.score }.average().toInt()}")
                StatItem("Total Hours", "${sessions.sumOf { it.duration } / 60}h")
                StatItem("Best Day", sessions.maxByOrNull { it.score }?.date ?: "-")
            }
        }
    }
}

@Composable
private fun FocusChartWithMood(sessions: List<FocusSession>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        val maxDuration = sessions.maxOf { it.duration }.toFloat()

        // Grid lines
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw grid
            for (i in 1..4) {
                val y = size.height * (i / 4f)
                drawLine(
                    color = Color.White.copy(0.1f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }
        }

        // Bars with gradient
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            sessions.forEach { session ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.height(140.dp)
                ) {
                    // Mood indicator
                    Text(
                        session.mood,
                        color = Color.White.copy(0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(4.dp))

                    // Animated bar
                    val heightRatio = (session.duration / maxDuration).coerceIn(0f, 1f)
                    val height = (heightRatio * 100).dp
                    AnimatedFocusBar(
                        height = height,
                        score = session.score,
                        modifier = Modifier.width(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))

                    // Day label
                    Text(
                        session.date,
                        color = Color.White.copy(0.7f),
                        fontSize = 10.sp
                    )

                    // Score
                    Text(
                        "${session.score}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedFocusBar(height: Dp, score: Int, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color = when {
        score >= 85 -> Color(0xFF4ECDC4)
        score >= 70 -> Color(0xFF45B7D1)
        else -> Color(0xFF96CEB4)
    }

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = alpha))
    )
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            color = Color.White.copy(0.6f),
            fontSize = 10.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TasksSection(
    tasks: List<Task>,
    onTaskToggle: (Int) -> Unit,
    onTaskDelete: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x40FFFFFF)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Tasks",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${tasks.count { it.isCompleted }}/${tasks.size}",
                    color = Color.White.copy(0.6f),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))

            if (tasks.isEmpty()) {
                Text(
                    "No tasks yet. Add your first task using the + button below!",
                    color = Color.White.copy(0.5f),
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tasks.forEach { task ->
                        ModernTaskItem(
                            task = task,
                            onToggle = onTaskToggle,
                            onDelete = onTaskDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernTaskItem(
    task: Task,
    onToggle: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFFF6B6B)
        "Medium" -> Color(0xFFFFD166)
        else -> Color(0xFF06D6A0)
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onToggle(task.id) },
                        onLongPress = { showDeleteConfirm = true }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(4.dp, 24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    task.title,
                    color = if (task.isCompleted) Color.White.copy(0.5f) else Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "${task.priority} Priority • Tap to complete • Long press to delete",
                    color = Color.White.copy(0.5f),
                    fontSize = 10.sp
                )
            }

            // Checkbox with custom design
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) priorityColor else Color.Transparent)
                    .border(2.dp, priorityColor, CircleShape)
                    .pointerInput(Unit) { detectTapGestures { onToggle(task.id) } },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(task.id)
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1A1F2C)
        )
    }
}

@Composable
private fun RemindersSection(
    reminders: List<Reminder>,
    onReminderToggle: (Int) -> Unit,
    onAddReminder: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x40FFFFFF)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Upcoming Reminders",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onAddReminder,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Reminder",
                        tint = Color.White.copy(0.8f)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            if (reminders.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No reminders set",
                        color = Color.White.copy(0.5f),
                        fontSize = 13.sp
                    )
                    Text(
                        "Tap + to add your first reminder",
                        color = Color.White.copy(0.4f),
                        fontSize = 11.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    reminders.forEach { reminder ->
                        ModernReminderItem(reminder, onReminderToggle)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernReminderItem(reminder: Reminder, onToggle: (Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = if (reminder.isActive) Color(0xFF4ECDC4) else Color.White.copy(0.3f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    reminder.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    reminder.time,
                    color = Color.White.copy(0.6f),
                    fontSize = 12.sp
                )
            }

            Switch(
                checked = reminder.isActive,
                onCheckedChange = { onToggle(reminder.id) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4ECDC4),
                    checkedTrackColor = Color(0xFF4ECDC4).copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.White.copy(alpha = 0.3f),
                    uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun AnimatedBackground(particleOffset: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Base gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0A0F1C), Color(0xFF1A1F2C))
            ),
            size = size
        )

        // Animated particles
        val particleCount = 50
        for (i in 0 until particleCount) {
            val x = (size.width * (i.toFloat() / particleCount + particleOffset)) % size.width
            val y = size.height * (i % 3) / 3f
            val radius = (i % 5 + 2).toFloat()

            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                center = Offset(x, y),
                radius = radius
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    newTask: String,
    selectedPriority: String,
    onTaskChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddTask: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = newTask,
                    onValueChange = onTaskChange,
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4ECDC4),
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        cursorColor = Color(0xFF4ECDC4)
                    )
                )
                Spacer(Modifier.height(16.dp))
                Text("Priority", color = Color.White, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Low").forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { onPriorityChange(priority) },
                            label = { Text(priority) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedLabelColor = Color.White,
                                selectedContainerColor = when (priority) {
                                    "High" -> Color(0xFFFF6B6B)
                                    "Medium" -> Color(0xFFFFD166)
                                    else -> Color(0xFF06D6A0)
                                },
                                labelColor = Color.White.copy(0.8f),
                                containerColor = Color.White.copy(0.1f)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAddTask,
                enabled = newTask.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ECDC4))
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF1A1F2C)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
    newReminderTitle: String,
    newReminderTime: String,
    onTitleChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddReminder: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Reminder") },
        text = {
            Column {
                OutlinedTextField(
                    value = newReminderTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Reminder Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4ECDC4),
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        cursorColor = Color(0xFF4ECDC4)
                    )
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = newReminderTime,
                    onValueChange = onTimeChange,
                    label = { Text("Time (e.g., 2:00 PM)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4ECDC4),
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        cursorColor = Color(0xFF4ECDC4)
                    )
                )
                Text(
                    "Example: 2:00 PM, 11:59 PM, 9:30 AM",
                    color = Color.White.copy(0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAddReminder,
                enabled = newReminderTitle.isNotBlank() && newReminderTime.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ECDC4))
            ) {
                Text("Add Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF1A1F2C)
    )
}