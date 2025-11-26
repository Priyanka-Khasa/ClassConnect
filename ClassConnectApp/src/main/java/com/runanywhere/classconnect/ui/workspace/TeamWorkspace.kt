package com.runanywhere.classconnect.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ------------------ MODEL ------------------

data class WorkspaceItem(
    val id: Int,
    val title: String,
    val description: String,
    val members: Int
)

// ------------------ SCREEN ------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamWorkspace(navController: NavController) {

    var workspaces by remember {
        mutableStateOf(
            listOf(
                WorkspaceItem(1, "AI Hackathon Team", "Brainstorming AI project ideas", 4),
                WorkspaceItem(2, "DBMS Assignments", "ER Diagram & SQL queries", 3),
                WorkspaceItem(3, "DSA Practice Group", "Daily LeetCode discussion", 5),
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Column {
                        Text("Team Workspace", fontSize = 20.sp, color = Color.White)
                        Text(
                            "Collaborate & manage your teams",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF020415),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF667EEA),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Team")
            }
        },
        containerColor = Color(0xFF020415)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // ---- WORKSPACE LIST ----
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(workspaces, key = { it.id }) { item ->
                    WorkspaceCard(item)
                }
            }
        }

        if (showAddDialog) {
            AddWorkspaceDialog(
                newTitle = newTitle,
                newDesc = newDesc,
                onTitleChange = { newTitle = it },
                onDescChange = { newDesc = it },
                onDismiss = { showAddDialog = false },
                onSave = {
                    if (newTitle.isNotBlank()) {
                        val id =
                            (workspaces.maxOfOrNull { it.id } ?: 0) + 1
                        workspaces =
                            workspaces + WorkspaceItem(id, newTitle, newDesc, (2..6).random())
                        newTitle = ""
                        newDesc = ""
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

// ------------------ WORKSPACE CARD ------------------

@Composable
private fun WorkspaceCard(item: WorkspaceItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0x22FFFFFF)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Icon Bubble
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0x335C6BC0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Workspaces,
                        contentDescription = null,
                        tint = Color(0xFF5C6BC0),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(item.title, color = Color.White, fontSize = 18.sp)
                    Text(
                        item.description,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    "${item.members} members",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ------------------ ADD DIALOG ------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWorkspaceDialog(
    newTitle: String,
    newDesc: String,
    onTitleChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Workspace") },
        text = {
            Column {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Team Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = newDesc,
                    onValueChange = onDescChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = newTitle.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
