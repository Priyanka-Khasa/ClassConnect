package com.runanywhere.classconnect.ui.workspace

import androidx.navigation.NavController
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.ui.graphics.graphicsLayer

// --- Data models ---
data class TeamNote(val id: Int, val title: String, val content: String, val author: String)
data class TeamTask(val id: Int, val title: String, var completed: Boolean = false, val assignedTo: String)

// --- Main Composable ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TeamWorkspace(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Notes") }
    var notes by remember { mutableStateOf(sampleNotes()) }
    var tasks by remember { mutableStateOf(sampleTasks()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }

    // --- Background ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "👩‍💻 Team Workspace",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                "Collaborate on notes, tasks & goals in real time",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(20.dp))

            // --- Tabs ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Notes", "To-Do").forEach { tab ->
                    Button(
                        onClick = { selectedTab = tab },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == tab) Color(0xFFFF6B6B) else Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(tab, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Content Switch ---
            AnimatedContent(targetState = selectedTab, transitionSpec = {
                fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
            }, label = "tab-animation") { tab ->
                when (tab) {
                    "Notes" -> NotesSection(notes)
                    "To-Do" -> TasksSection(tasks) { id ->
                        tasks = tasks.map {
                            if (it.id == id) it.copy(completed = !it.completed) else it
                        }
                    }
                }
            }
        }

        // --- Add Button ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFFFF6B6B),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }

        // --- Add Dialog ---
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedTab == "Notes") {
                                notes = notes + TeamNote(
                                    id = notes.size + 1,
                                    title = newTitle,
                                    content = newContent,
                                    author = listOf("Neha", "Riya", "Ananya").random()
                                )
                            } else {
                                tasks = tasks + TeamTask(
                                    id = tasks.size + 1,
                                    title = newTitle,
                                    assignedTo = listOf("Aisha", "Arjun", "Rahul").random()
                                )
                            }
                            newTitle = ""
                            newContent = ""
                            showAddDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Add", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text(
                        if (selectedTab == "Notes") "New Note" else "New Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            placeholder = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (selectedTab == "Notes") {
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newContent,
                                onValueChange = { newContent = it },
                                placeholder = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

// --- Notes Section ---
@Composable
fun NotesSection(notes: List<TeamNote>) {
    if (notes.isEmpty()) {
        EmptyState("No notes yet", "Add your first collaborative note")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(notes) { note ->
                NoteCard(note)
            }
        }
    }
}

@Composable
fun NoteCard(note: TeamNote) {
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, easing = FastOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = animatedAlpha),   // ✅ fixed
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(note.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(note.content, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(10.dp))
            Text("✍️ by ${note.author}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}

// --- Tasks Section ---
@Composable
fun TasksSection(tasks: List<TeamTask>, onToggle: (Int) -> Unit) {
    if (tasks.isEmpty()) {
        EmptyState("No tasks yet", "Add some tasks for your team")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tasks) { task ->
                TaskItem(task, onToggle)
            }
        }
    }
}

@Composable
fun TaskItem(task: TeamTask, onToggle: (Int) -> Unit) {
    val bg = if (task.completed) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(task.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text("👤 ${task.assignedTo}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }

            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggle(task.id) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = Color.White
                )
            )
        }
    }
}

// --- Empty State ---
@Composable
fun EmptyState(title: String, message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
    ) {
        Icon(
            Icons.Default.Lightbulb,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(message, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}

// --- Fake Data ---
fun sampleNotes() = listOf(
    TeamNote(1, "AI Study Plan", "Focus on transformer architectures this week.", "Riya"),
    TeamNote(2, "Database Revision", "Normalize schema before next review.", "Rahul")
)

fun sampleTasks() = listOf(
    TeamTask(1, "Finish TensorFlow tutorial", false, "Neha"),
    TeamTask(2, "Write project summary", true, "Aisha"),
    TeamTask(3, "Team meeting 7 PM", false, "Arjun")
)
