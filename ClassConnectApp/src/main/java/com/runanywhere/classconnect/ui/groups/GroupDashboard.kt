package com.runanywhere.classconnect.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// -------------------------------------------------------------
//                 GROUP DASHBOARD SCREEN (FINAL)
// -------------------------------------------------------------

@Composable
fun GroupDashboard(navController: NavController) {

    val gradientBackground = Brush.verticalGradient(
        listOf(
            Color(0xFF0C0C1C),
            Color(0xFF1A1A2E),
            Color(0xFF0F3460)
        )
    )

    // Fake data (you can replace with API / ViewModel later)
    val groups = remember {
        listOf(
            GroupItem("Android Developers", "Create, learn & build apps together."),
            GroupItem("AI / ML Club", "Discuss ML, AI & research papers."),
            GroupItem("Web Dev Community", "React, Next.js, MERN & more."),
            GroupItem("Cloud & DevOps", "Docker, K8s, AWS & automation.")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ---------- HEADER ----------
            Text(
                text = "Your Groups",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ---------- GROUP LIST ----------
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(groups) { group ->
                    GroupCard(
                        group = group,
                        onOpenChat = {
                            navController.navigate("chat")
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
//                        GROUP ITEM DATA
// -------------------------------------------------------------

data class GroupItem(
    val title: String,
    val description: String
)

// -------------------------------------------------------------
//                     GROUP CARD COMPOSABLE
// -------------------------------------------------------------

@Composable
fun GroupCard(
    group: GroupItem,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    tint = Color(0xFF66CCFF),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = group.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.description,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- OPEN CHAT BUTTON (ONLY CLICKABLE ACTION) ----------
            Button(
                onClick = onOpenChat,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66CCFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Message,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Open Chat",
                    color = Color.White
                )
            }
        }
    }
}
