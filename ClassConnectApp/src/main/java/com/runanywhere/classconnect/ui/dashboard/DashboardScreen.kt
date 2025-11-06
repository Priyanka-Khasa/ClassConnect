
package com.runanywhere.classconnect.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.runanywhere.classconnect.util.SessionManager

import androidx.compose.runtime.mutableFloatStateOf



data class Task(val title: String, val priority: String, val isCompleted: Boolean, val id: Int)
data class QuickAction(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String, val color: Color)
data class Reminder(val title: String, val time: String, val isActive: Boolean, val id: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, sessionManager: SessionManager) {

    val scope = rememberCoroutineScope()



    var tasks by remember {
        mutableStateOf(
            listOf(
                Task("Complete DBMS Assignment", "High", false, 1),
                Task("Revise DSA Notes", "Medium", false, 2),
                Task("Prepare AI Project Pitch", "High", true, 3),
                Task("Read Research Paper", "Low", false, 4),
                Task("Complete Math Homework", "Medium", false, 5)
            )
        )
    }

    var reminders by remember {
        mutableStateOf(
            listOf(
                Reminder("Group Meeting", "2:00 PM", true, 1),
                Reminder("Submit Assignment", "11:59 PM", true, 2),
                Reminder("Study Session", "6:00 PM", false, 3)
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var showWelcome by remember { mutableStateOf(true) }
    var recentlyCompletedTasks by remember { mutableStateOf(setOf<Int>()) }

    // Quick Actions
    val quickActions = listOf(
        QuickAction("Mmaking", Icons.Default.People, "matchmaking", Color(0xFF4158D0)),
        QuickAction("Groups", Icons.Default.Group, "groups", Color(0xFFC850C0)),
        QuickAction("Workspace", Icons.Default.Work, "workspace", Color(0xFFFFCC70)),
        QuickAction("Reminders", Icons.Default.Notifications, "reminders", Color(0xFF4CAF50)),
        QuickAction("Timeline", Icons.Default.DateRange, "timeline", Color(0xFF64B5F6)) // 🆕 new
    )

    val completed = tasks.count { it.isCompleted }
    val productivity = if (tasks.isNotEmpty()) completed.toFloat() / tasks.size else 0f
    val pendingTasks = tasks.size - completed

    // Auto-hide welcome message after 3 seconds
    LaunchedEffect(showWelcome) {
        if (showWelcome) {
            delay(3000)
            showWelcome = false
        }
    }

    // 🔮 Animated gradient background
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
                    start = Offset(shimmerOffset, 0f),
                    end = Offset(0f, shimmerOffset)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🪩 Dynamic Header with welcome message that disappears
            if (showWelcome) {
                AnimatedWelcomeHeader()
                Spacer(Modifier.height(20.dp))
            } else {
                CompactUserHeader()
                Spacer(Modifier.height(20.dp))
            }

            // 🚀 Quick Actions Section
            QuickActionsSection(quickActions, navController)

            Spacer(Modifier.height(24.dp))

            // 🎯 Enhanced Productivity Section with motivational messages
            ProductivitySection(
                progress = productivity,
                completed = completed,
                total = tasks.size,
                recentlyCompleted = recentlyCompletedTasks.size
            )

            Spacer(Modifier.height(24.dp))

            // 🔔 Enhanced Reminders Section
            RemindersSection(reminders = reminders, onReminderToggle = { reminderId ->
                reminders = reminders.map { reminder ->
                    if (reminder.id == reminderId) reminder.copy(isActive = !reminder.isActive) else reminder
                }
            })

            Spacer(Modifier.height(24.dp))

            // 💡 Enhanced AI Suggestion Card
            AICard(
                onAskAI = { navController.navigate("chat") }
            )

            Spacer(Modifier.height(24.dp))

            // 📋 Enhanced Tasks Section with completion animations
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Tasks",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        "$pendingTasks pending • $completed done",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (tasks.isEmpty()) {
                    EmptyState()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        tasks.forEach { task ->
                            if (!task.isCompleted || recentlyCompletedTasks.contains(task.id)) {
                                AnimatedTaskItem(
                                    task = task,
                                    onToggleComplete = {
                                        val wasCompleted = task.isCompleted
                                        tasks = tasks.map {
                                            if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it
                                        }

                                        if (!wasCompleted) {
                                            // Task was just completed
                                            recentlyCompletedTasks = recentlyCompletedTasks + task.id
                                            scope.launch {
                                                delay(2000) // Show for 2 seconds after completion
                                                recentlyCompletedTasks = recentlyCompletedTasks - task.id
                                            }
                                        }
                                    },
                                    isRecentlyCompleted = recentlyCompletedTasks.contains(task.id)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            // ⚙️ Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    text = "Ask AI",
                    icon = Icons.Default.AutoAwesome,
                    gradient = listOf(Color(0xFF4158D0), Color(0xFFC850C0), Color(0xFFFFCC70)),
                    onClick = { navController.navigate("chat") },
                    modifier = Modifier.weight(1f)
                )

                GradientButton(
                    text = "Logout",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    gradient = listOf(Color(0xFFFF5F6D), Color(0xFFFFC371)),
                    onClick = {
                        scope.launch {
                            sessionManager.setLoginState(false)
                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ➕ Floating Add Button
        FloatingAddButton {
            showDialog = true
        }

        // 🧾 Add Task Dialog
        if (showDialog) {
            AddTaskDialog(
                newTask = newTask,
                selectedPriority = selectedPriority,
                onTaskChange = { newTask = it },
                onPriorityChange = { selectedPriority = it },
                onDismiss = { showDialog = false },
                onAddTask = {
                    if (newTask.isNotBlank()) {
                        val newId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                        tasks = tasks + Task(newTask, selectedPriority, false, newId)
                        newTask = ""
                        showDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun QuickActionsSection(actions: List<QuickAction>, navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Quick Access ",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(actions.size) { index ->
                QuickActionCard(actions[index], navController)
            }
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction, navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        onClick = { navController.navigate(action.route) },
        modifier = Modifier
            .width(120.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = action.color.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                action.icon,
                contentDescription = action.title,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                action.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RemindersSection(reminders: List<Reminder>, onReminderToggle: (Int) -> Unit) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Reminders 🔔",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Text(
                "${reminders.count { it.isActive }} active",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        if (reminders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.NotificationsOff,
                    contentDescription = "No reminders",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "No reminders set",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                reminders.forEach { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onToggle = { onReminderToggle(reminder.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isActive)
                Color(0xFF4CAF50).copy(alpha = 0.2f)
            else
                Color.White.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Time",
                    tint = if (reminder.isActive) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        reminder.title,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        reminder.time,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }

            Switch(
                checked = reminder.isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4CAF50),
                    checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
fun AnimatedWelcomeHeader() {
    var scale by remember { mutableFloatStateOf(0.8f) }
    var alpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        scale = 1f
        alpha = 1f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .graphicsLayer { this.alpha = alpha },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome back, Priyanka! ",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ready to conquer your day? ",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Animated celebration icon
            val infiniteTransition = rememberInfiniteTransition()
            val celebrateScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Icon(
                Icons.Default.Celebration,
                contentDescription = "Celebration",
                tint = Color(0xFFFFD700),
                modifier = Modifier
                    .size(40.dp)
                    .scale(celebrateScale)
            )
        }
    }
}

@Composable
fun CompactUserHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Hello, Priyanka! ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Let's make progress today ",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        // User avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF4158D0), Color(0xFFC850C0))),
                    CircleShape
                )
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("P", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun AICard(onAskAI: () -> Unit) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "AI Study Assistant ",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Get personalized study plans and instant help with your coursework",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF4158D0), Color(0xFFC850C0))),
                        CircleShape
                    )
            ) {
                IconButton(
                    onClick = onAskAI,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Ask AI",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductivitySection(progress: Float, completed: Int, total: Int, recentlyCompleted: Int) {
    GlassCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Progress",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                if (recentlyCompleted > 0) {
                    Text(
                        "+$recentlyCompleted completed! ",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Productivity Ring
                ProductivityRing(progress = progress)

                // Enhanced Stats
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$completed/$total", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Tasks Completed", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)

                    Spacer(Modifier.height(12.dp))

                    val motivation = when {
                        progress == 1f -> "Perfect! All done! "
                        progress > 0.8f -> "Almost there! "
                        progress > 0.6f -> "Great progress! "
                        progress > 0.4f -> "Good job! "
                        progress > 0.2f -> "Keep going! "
                        else -> "Let's get started! "
                    }
                    Text(
                        motivation,
                        color = getProgressColor(progress),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedTaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    isRecentlyCompleted: Boolean
) {
    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFFF5252)
        "Medium" -> Color(0xFFFFB74D)
        else -> Color(0xFF4CAF50)
    }

    var scale by remember { mutableFloatStateOf(1f) }
    var alpha by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(isRecentlyCompleted) {
        if (isRecentlyCompleted) {
            // Celebration animation for recently completed tasks
            scale = 1.05f
            delay(200)
            scale = 1f
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .graphicsLayer { this.alpha = alpha },
        colors = CardDefaults.cardColors(
            containerColor = if (isRecentlyCompleted)
                Color(0xFF4CAF50).copy(alpha = 0.2f)
            else
                Color.White.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isRecentlyCompleted) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Priority indicator with animation
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(priorityColor, CircleShape)
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        task.title,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Text(
                        "Priority: ${task.priority}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }

            Row {
                // Animated check button
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.size(40.dp)
                ) {
                    val iconScale by animateFloatAsState(
                        targetValue = if (task.isCompleted) 1.2f else 1f,
                        animationSpec = tween(300)
                    )

                    Icon(
                        if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = "Toggle complete",
                        tint = if (task.isCompleted) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.scale(iconScale)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(gradient))
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun FloatingAddButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = Color(0xFF5B86E5),
            modifier = Modifier.scale(scale),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
        }
    }
}

@Composable
fun AddTaskDialog(
    newTask: String,
    selectedPriority: String,
    onTaskChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddTask: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add New Task",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C5364)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newTask,
                    onValueChange = onTaskChange,
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
                Text("Priority:", fontWeight = FontWeight.Medium, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Low").forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { onPriorityChange(priority) },
                            label = { Text(priority) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (priority) {
                                    "High" -> Color(0xFFFF5252)
                                    "Medium" -> Color(0xFFFFB74D)
                                    else -> Color(0xFF4CAF50)
                                }
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAddTask,
                enabled = newTask.isNotBlank()
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(60.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "All caught up! ",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            "No pending tasks - time for a well-deserved break! 🌿",
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ProductivityRing(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing)
    )
    val ringColor = getProgressColor(progress)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
        // Background ring
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                style = Stroke(8f)
            )
        }

        // Progress ring
        Canvas(modifier = Modifier.matchParentSize()) {
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(8f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }

        // Center text with animation
        val numberScale by animateFloatAsState(
            targetValue = if (animatedProgress > 0) 1.1f else 1f,
            animationSpec = tween(300)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${(progress * 100).toInt()}%",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(numberScale)
            )
        }
    }
}

@Composable
fun getProgressColor(progress: Float): Color {
    return when {
        progress >= 0.8f -> Color(0xFF4CAF50)  // Green
        progress >= 0.5f -> Color(0xFFFFB74D)  // Orange
        else -> Color(0xFFFF5252)              // Red
    }
}