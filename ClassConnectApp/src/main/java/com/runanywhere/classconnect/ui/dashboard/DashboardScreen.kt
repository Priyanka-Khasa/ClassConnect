
package com.runanywhere.classconnect.ui.dashboard

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.classconnect.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateFloat


// ————————————————————————————————————————————————————————————————————————————
//  DATA
// ————————————————————————————————————————————————————————————————————————————
data class Task(val title: String, val priority: String, val isCompleted: Boolean, val id: Int)
data class QuickAction(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String,
    val color: Color
)
data class Reminder(val title: String, val time: String, val isActive: Boolean, val id: Int)
data class FocusSession(val date: String, val duration: Int, val focusScore: Int)

// ————————————————————————————————————————————————————————————————————————————
//  MAIN DASHBOARD SCREEN
// ————————————————————————————————————————————————————————————————————————————
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, sessionManager: SessionManager) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // — State
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

    val focusSessions by remember {
        mutableStateOf(
            listOf(
                FocusSession("Today", 45, 85),
                FocusSession("Yesterday", 120, 92),
                FocusSession("Week Avg", 90, 78)
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var showWelcome by remember { mutableStateOf(true) }
    var currentTime by remember { mutableStateOf("") }
    var recentlyCompletedTasks by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = java.text.SimpleDateFormat(
                "EEE, dd MMM · hh:mm a",
                java.util.Locale.getDefault()
            ).format(java.util.Date())
            delay(1_000)
        }
    }

    val quickActions = listOf(
        QuickAction("Matchmaking", Icons.Default.People, "matchmaking", Color(0xFF6C63FF)),
        QuickAction("Study Groups", Icons.Default.Group, "groups", Color(0xFF00BFA6)),
        QuickAction("Workspace", Icons.Default.Work, "workspace", Color(0xFFFF8A65)),
        QuickAction("Reminders", Icons.Default.Notifications, "reminders", Color(0xFF64B5F6)),
        QuickAction("Timeline", Icons.Default.DateRange, "timeline", Color(0xFFFFC400)),
        QuickAction("Focus Mode", Icons.Default.CenterFocusStrong, "focus", Color(0xFF7C4DFF))
    )

    val completed = tasks.count { it.isCompleted }
    val productivity = if (tasks.isNotEmpty()) completed.toFloat() / tasks.size else 0f

    // Auto-hide welcome chip
    LaunchedEffect(Unit) {
        delay(3_000)
        showWelcome = false
    }

    // ————————————————— UI LAYOUT —————————————————
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
    ) {
        // Parallax Aurora
        AuroraBackground()

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopHeader(currentTime = currentTime, showWelcome = showWelcome)

            Spacer(Modifier.height(22.dp))
            QuickActionsGrid(quickActions, onClick = { action ->
                if (action.title == "Focus Mode") {
                    val intent = Intent(
                        context,
                        com.runanywhere.classconnect.ui.focus.FocusCameraActivity::class.java
                    )
                    context.startActivity(intent)
                } else navController.navigate(action.route)
            })

            Spacer(Modifier.height(22.dp))
            FrostCard {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Focus Analytics", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF64FFDA))
                }
                Spacer(Modifier.height(16.dp))
                WeeklyFocusChart(sessions = focusSessions)
            }

            Spacer(Modifier.height(22.dp))

            // ✅ BEAUTIFUL TASKS LIST SECTION
            FrostCard {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("My Tasks", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("${tasks.size - completed} pending", color = Color.White.copy(.7f), fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))

                if (tasks.isEmpty()) {
                    EmptyState(icon = Icons.Default.CheckCircle, label = "No tasks yet")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        tasks.take(4).forEach { task ->
                            BeautifulTaskItem(
                                task = task,
                                onToggle = { taskId ->
                                    tasks = tasks.map { t ->
                                        if (t.id == taskId) t.copy(isCompleted = !t.isCompleted) else t
                                    }
                                }
                            )
                        }
                    }

                    if (tasks.size > 4) {
                        Spacer(Modifier.height(16.dp))
                        TextButton(
                            onClick = { /* Navigate to full tasks screen */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("View all ${tasks.size} tasks", color = Color(0xFF86C5FF))
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // ✅ PRODUCTIVITY SECTION - standalone
            FrostCard {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Productivity", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    if (recentlyCompletedTasks.isNotEmpty()) {
                        Text("+${recentlyCompletedTasks.size}", color = Color(0xFF69F0AE), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Progress Ring
                    Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        HaloProgressRing(progress = productivity, ringSize = 120.dp)
                    }

                    // Progress Stats
                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProgressStat("Completed", completed, tasks.size, Color(0xFF69F0AE))
                        ProgressStat("In Progress", tasks.size - completed, tasks.size, Color(0xFFFFB74D))
                        ProgressStat("High Priority", tasks.count { it.priority == "High" && !it.isCompleted }, null, Color(0xFFFF6E6E))
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "${(productivity * 100).toInt()}% overall completion",
                    color = Color.White.copy(.8f),
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(22.dp))
            FrostCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("AI Study Assistant", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Ask doubts, get summaries & smart plans",
                            color = Color.White.copy(.8f), fontSize = 14.sp
                        )
                    }
                    GlowingRoundButton(icon = Icons.Default.AutoAwesome) {
                        navController.navigate("chat")
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            FrostCard {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Upcoming Reminders", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("${reminders.count { it.isActive }} active", color = Color.White.copy(.7f), fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))
                if (reminders.isEmpty()) {
                    EmptyState(icon = Icons.Default.NotificationsOff, label = "No reminders yet")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        reminders.take(3).forEach { r ->
                            ReminderRow(r) { id ->
                                reminders = reminders.map { old -> if (old.id == id) old.copy(isActive = !old.isActive) else old }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(26.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GradientButton(
                    text = "AI Assistant",
                    icon = Icons.Default.AutoAwesome,
                    gradient = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
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
            Spacer(Modifier.height(90.dp))
        }

        FloatingAdd(onClick = { showDialog = true })

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

// ————————————————————————————————————————————————————————————————————————————
//  COMPONENTS
// ————————————————————————————————————————————————————————————————————————————

@Composable
private fun BeautifulTaskItem(task: Task, onToggle: (Int) -> Unit) {
    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFFF6E6E)
        "Medium" -> Color(0xFFFFB74D)
        else -> Color(0xFF69F0AE)
    }

    val inf = rememberInfiniteTransition()
    val glow by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(glow),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) Color(0x1A69F0AE) else Color(0x14FFFFFF)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority Indicator
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(priorityColor, RoundedCornerShape(2.dp))
            )

            Spacer(Modifier.width(12.dp))

            // Task Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.priority,
                    color = priorityColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            // Checkbox with custom styling
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) Color(0xFF69F0AE) else Color.White.copy(0.1f))
                    .border(
                        1.5.dp,
                        if (task.isCompleted) Color(0xFF69F0AE) else Color.White.copy(0.3f),
                        CircleShape
                    )
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onToggle(task.id)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressStat(label: String, current: Int, total: Int? = null, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(
                text = if (total != null) "$current/$total" else "$current",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White.copy(0.7f),
                fontSize = 11.sp
            )
        }
    }
}

// ————————————————————————————————————————————————————————————————————————————
//  VISUALS
// ————————————————————————————————————————————————————————————————————————————
@Composable
private fun AuroraBackground() {
    val inf = rememberInfiniteTransition()
    val t1 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse)
    )
    val t2 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse)
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
        blob(w * (.2f + .05f * t1), h * (.2f + .1f * t2), w * .45f, listOf(Color(0x33226CE0), Color.Transparent))
        blob(w * (.85f - .05f * t2), h * (.35f - .1f * t1), w * .35f, listOf(Color(0x3330E3CA), Color.Transparent))
        blob(w * (.5f + .02f * t2), h * (.9f), w * .55f, listOf(Color(0x33F9A826), Color.Transparent))
    }
}

@Composable
private fun TopHeader(currentTime: String, showWelcome: Boolean) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(currentTime, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Light)
                AnimatedVisibility(visible = showWelcome) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Welcome to ClassConnect", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFFD700)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0x14FFFFFF))
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2))),
                        CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = .25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(actions: List<QuickAction>, onClick: (QuickAction) -> Unit) {
    Text("Quick Access", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(12.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(actions) { action -> QuickActionCard(action, onClick) }
    }
}

@Composable
private fun QuickActionCard(action: QuickAction, onClick: (QuickAction) -> Unit) {
    val inf = rememberInfiniteTransition()
    val pul by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    Card(
        onClick = { onClick(action) },
        modifier = Modifier
            .width(150.dp)
            .height(120.dp)
            .scale(pul),
        colors = CardDefaults.cardColors(containerColor = action.color.copy(alpha = .9f)),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(action.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(action.title, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
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
private fun HaloProgressRing(progress: Float, ringSize: Dp = 140.dp) {
    val anim by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1200, easing = FastOutSlowInEasing)
    )

    val ringColor = when {
        anim >= .8f -> Color(0xFF69F0AE)
        anim >= .5f -> Color(0xFFFFB74D)
        else -> Color(0xFFFF6E6E)
    }

    Box(
        modifier = Modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        // Background Glow
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .blur(30.dp, BlurredEdgeTreatment.Unbounded)
        ) {
            val radius = kotlin.math.min(this.size.width, this.size.height) / 2f
            drawCircle(color = ringColor.copy(alpha = 0.25f), radius = radius)
        }

        // Main Progress Ring
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 14f

            drawArc(
                color = Color.White.copy(alpha = 0.12f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke)
            )

            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * anim,
                useCenter = false,
                style = Stroke(width = stroke)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(anim * 100).toInt()}%",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Completion",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun WeeklyFocusChart(sessions: List<FocusSession>) {
    val max = (sessions.maxOfOrNull { it.duration } ?: 1).coerceAtLeast(1)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        sessions.forEach { s ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val height = (s.duration / max.toFloat()).coerceIn(0f, 1f)
                FocusBar(height, score = s.focusScore)
                Spacer(Modifier.height(8.dp))
                Text(s.date, color = Color.White.copy(.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun FocusBar(height: Float, score: Int) {
    val anim by animateFloatAsState(
        targetValue = height,
        animationSpec = tween(900, easing = FastOutSlowInEasing)
    )
    val color = when {
        score >= 80 -> Color(0xFF69F0AE)
        score >= 60 -> Color(0xFFFFD54F)
        else -> Color(0xFFFF6E6E)
    }
    Box(
        Modifier
            .width(42.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(.06f))
            .drawBehind {
                val h = size.height * anim
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(color.copy(.9f), color.copy(.5f))),
                    topLeft = Offset(0f, size.height - h),
                    size = Size(size.width, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                )
            }
    )
}

@Composable
private fun ReminderRow(reminder: Reminder, onToggle: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (reminder.isActive) Color(0x334CAF50) else Color(0x14FFFFFF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = if (reminder.isActive) Color(0xFF4CAF50) else Color.White.copy(.6f)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(reminder.title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(reminder.time, color = Color.White.copy(.7f), fontSize = 12.sp)
            }
            Switch(
                checked = reminder.isActive,
                onCheckedChange = { onToggle(reminder.id) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4CAF50),
                    checkedTrackColor = Color(0x804CAF50)
                )
            )
        }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(.5f), modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(8.dp))
        Text(label, color = Color.White.copy(.75f), fontSize = 14.sp)
    }
}

@Composable
private fun GlowingRoundButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val pul by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(66.dp)
                .blur(24.dp, BlurredEdgeTreatment.Unbounded)
                .background(Color(0x55667EEA), CircleShape)
        )
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier
                .size(58.dp)
                .scale(pul),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF667EEA))
        ) { Icon(icon, null, tint = Color.White) }
    }
}

@Composable
private fun GradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            Modifier
                .background(Brush.horizontalGradient(gradient))
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FloatingAdd(onClick: () -> Unit) {
    val inf = rememberInfiniteTransition()
    val s by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = Color(0xFF667EEA),
            modifier = Modifier.scale(s),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        }
    }
}

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
        containerColor = Color(0xFF11182C),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = { Text("Create New Task", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = newTask,
                    onValueChange = onTaskChange,
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x15FFFFFF),
                        unfocusedContainerColor = Color(0x0DFFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(.9f),
                        focusedIndicatorColor = Color(0xFF66CCFF),
                        unfocusedIndicatorColor = Color.White.copy(.4f),
                        cursorColor = Color.White
                    )
                )
                Spacer(Modifier.height(16.dp))
                Text("Priority", fontWeight = FontWeight.Medium, color = Color.White.copy(.85f))
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("High" to Color(0xFFFF6E6E), "Medium" to Color(0xFFFFB74D), "Low" to Color(0xFF69F0AE)).forEach { (p, col) ->
                        FilterChip(
                            selected = selectedPriority == p,
                            onClick = { onPriorityChange(p) },
                            label = {
                                Text(
                                    p,
                                    color = if (selectedPriority == p) Color.White else Color.White.copy(.85f)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = col,
                                containerColor = Color.White.copy(.08f)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667EEA)),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Add Task", fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(.8f))
            ) { Text("Cancel") }
        }
    )
}
