package com.runanywhere.classconnect.ui.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusLeaderboardScreen(navController: NavController) {

    val students = remember {
        listOf(
            "Priyanka" to 210,
            "Riya" to 175,
            "Mehul" to 130,
            "Aditya" to 85,
            "Sakshi" to 60,
            "Sunny" to 70,
            "Aditi" to 30
        ).sortedByDescending { it.second }
    }

    val yourName = "Priyanka"
    val yourRank = students.indexOfFirst { it.first == yourName } + 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // ------- HEADER WITH GRADIENT -------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF7C4DFF), Color(0xFF5A36D6))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your Rank",
                        color = Color.White.copy(0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "#$yourRank",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Great job! Stay focused 🎯",
                        color = Color.White.copy(0.9f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -------- LIST OF STUDENTS --------
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(students) { index, (name, mins) ->
                    LeaderboardRow(
                        rank = index + 1,
                        name = name,
                        minutes = mins,
                        highlight = name == yourName
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, name: String, minutes: Int, highlight: Boolean) {

    val medalEmoji = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> " "
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight)
                Color(0xFFEEE5FF)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Medal Circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (highlight) Color(0xFF7C4DFF)
                            else Color(0x33000000)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = medalEmoji.ifBlank { rank.toString() },
                        color = if (highlight) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = name,
                    fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
                    color = if (highlight) Color(0xFF4A148C) else Color.Black
                )
            }

            Text(
                text = "${minutes / 60}h ${minutes % 60}m",
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
    }
}
