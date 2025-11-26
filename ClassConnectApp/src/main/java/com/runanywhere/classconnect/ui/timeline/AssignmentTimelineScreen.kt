package com.runanywhere.classconnect.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.runanywhere.classconnect.data.AssignmentTask
import com.runanywhere.classconnect.viewmodels.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentTimelineScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Assignment Timeline",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0D0F1A)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, null)
            }
        },
        containerColor = Color(0xFF0D0F1A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (tasks.isEmpty()) {
                item {
                    EmptyTimeline()
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    AssignmentGlassCard(
                        task = task,
                        onToggle = { viewModel.toggleTaskCompleted(task.id, it) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, dueAt ->
                viewModel.addTask(title, desc, dueAt)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EmptyTimeline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No assignments yet.\nTap + to add your first task!",
            color = Color.White.copy(0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun AssignmentGlassCard(
    task: AssignmentTask,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    val now = System.currentTimeMillis()
    val millisLeft = task.dueAtMillis - now

    val isOverdue = millisLeft < 0 && !task.isCompleted
    val daysLeft = (millisLeft / (1000f * 60 * 60 * 24)).toInt()

    // NEON GLASS CARD COLORS
    val bgColor = when {
        task.isCompleted -> Color(0xFF4CAF50).copy(alpha = 0.18f)
        isOverdue -> Color(0xFFFF5252).copy(alpha = 0.18f)
        daysLeft < 1 -> Color(0xFFFFC107).copy(alpha = 0.18f)
        else -> Color(0xFF6C63FF).copy(alpha = 0.20f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .blur(0.5.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(18.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    task.title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        tint = Color.White.copy(alpha = 0.7f),
                        contentDescription = "Delete"
                    )
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    task.description,
                    color = Color.White.copy(0.8f)
                )
            }

            Spacer(Modifier.height(14.dp))

            // Neon progress bar
            LinearProgressIndicator(
                progress = when {
                    task.isCompleted -> 1f
                    millisLeft <= 0 -> 1f
                    else -> {
                        val window = 7 * 24 * 60 * 60 * 1000L
                        (1 - millisLeft.toFloat() / window).coerceIn(0f, 1f)
                    }
                },
                color = when {
                    task.isCompleted -> Color(0xFF00E676)
                    isOverdue -> Color(0xFFFF5252)
                    daysLeft < 1 -> Color(0xFFFFEB3B)
                    else -> Color(0xFF7E8BFF)
                },
                trackColor = Color.White.copy(alpha = 0.16f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                        when {
                            task.isCompleted -> "Completed • ${sdf.format(Date(task.dueAtMillis))}"
                            isOverdue -> "Overdue • ${sdf.format(Date(task.dueAtMillis))}"
                            daysLeft < 1 -> "Due Today • ${sdf.format(Date(task.dueAtMillis))}"
                            else -> "$daysLeft days left • ${sdf.format(Date(task.dueAtMillis))}"
                        },
                    color = Color.White.copy(0.75f)
                )

                IconButton(
                    onClick = { onToggle(!task.isCompleted) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Toggle",
                        tint = if (task.isCompleted) Color(0xFF00E676)
                        else Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(30.dp)
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("0") }
    var hours by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Assignment", fontWeight = FontWeight.SemiBold) },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    val d = days.toIntOrNull() ?: 0
                    val h = hours.toIntOrNull() ?: 0

                    val due = System.currentTimeMillis() +
                            d * 24 * 60 * 60 * 1000L +
                            h * 60 * 60 * 1000L

                    onConfirm(title.trim(), desc.trim(), due)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Days") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Hours") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    "Due date = current time + Days + Hours",
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        }
    )
}
