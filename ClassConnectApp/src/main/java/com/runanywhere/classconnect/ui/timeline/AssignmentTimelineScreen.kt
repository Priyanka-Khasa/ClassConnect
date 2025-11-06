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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.runanywhere.classconnect.data.AssignmentTask
import com.runanywhere.classconnect.ChatViewModel
import kotlinx.coroutines.launch
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
            TopAppBar(
                title = { Text("📅 Assignment Timeline") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Task")
                    }
                }
            )
        },
        containerColor = Color(0xFF101820),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("New Task") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF101820))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (tasks.isEmpty()) {
                item {
                    Text(
                        "No tasks yet. Tap + to add your first assignment.",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    AssignmentCard(
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
private fun AssignmentCard(
    task: AssignmentTask,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val now = System.currentTimeMillis()
    val millisLeft = task.dueAtMillis - now
    val daysLeft = (millisLeft / (1000 * 60 * 60 * 24f))
    val isOverdue = millisLeft < 0 && !task.isCompleted

    // progress heuristic: closer to due = higher progress (0..1)
    val progress = when {
        task.isCompleted -> 1f
        millisLeft <= 0 -> 1f
        else -> {
            // assume a 10-day planning window for the visual
            val total = 10 * 24 * 60 * 60 * 1000L
            (1f - (millisLeft / total.toFloat())).coerceIn(0f, 1f)
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (task.isCompleted) Color(0xFF4CAF50).copy(alpha = 0.18f)
        else if (isOverdue) Color(0xFFFF5252).copy(alpha = 0.18f)
        else Color(0xFF2196F3).copy(alpha = 0.18f),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.White.copy(0.8f))
                }
            }

            if (task.description.isNotBlank()) {
                Text(
                    task.description,
                    color = Color.White.copy(0.85f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(10.dp))
            }

            LinearProgressIndicator(
                progress = progress,
                color = when {
                    task.isCompleted -> Color(0xFF4CAF50)
                    isOverdue -> Color(0xFFFF5252)
                    daysLeft <= 1 -> Color(0xFFFFC107)
                    else -> Color(0xFF66CCFF)
                },
                trackColor = Color.White.copy(0.12f),
                modifier = Modifier.fillMaxWidth().height(6.dp)
            )

            Spacer(Modifier.height(6.dp))

            val statusText = when {
                task.isCompleted -> "✅ Completed • ${sdf.format(Date(task.dueAtMillis))}"
                isOverdue -> "❌ Deadline passed • was due ${sdf.format(Date(task.dueAtMillis))}"
                daysLeft < 1 -> "⏰ Due today • ${sdf.format(Date(task.dueAtMillis))}"
                else -> "⏳ ${daysLeft.toInt()} days left • due ${sdf.format(Date(task.dueAtMillis))}"
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(statusText, color = Color.White.copy(0.75f), style = MaterialTheme.typography.labelMedium)
                AssistChip(
                    onClick = { onToggle(!task.isCompleted) },
                    label = { Text(if (task.isCompleted) "Mark Incomplete" else "Mark Done") },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = if (task.isCompleted) Color(0xFF4CAF50) else Color.White
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, dueAtMillis: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("3") }    // default 3 days
    var hours by remember { mutableStateOf("0") }   // default 0 hours

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val d = days.toLongOrNull() ?: 0L
                    val h = hours.toLongOrNull() ?: 0L
                    val dueAt = System.currentTimeMillis() + d*24*60*60*1000 + h*60*60*1000
                    onConfirm(title.trim(), description.trim(), dueAt)
                },
                enabled = title.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("New Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description (optional)") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = days, onValueChange = { days = it },
                        label = { Text("Days") }, modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = hours, onValueChange = { hours = it },
                        label = { Text("Hours") }, modifier = Modifier.weight(1f)
                    )
                }
                Text("Set how long from now the task should be due (Days + Hours).")
            }
        }
    )
}