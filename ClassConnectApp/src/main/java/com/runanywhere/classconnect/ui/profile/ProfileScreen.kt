package com.runanywhere.classconnect.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.classconnect.model.UserProfile
import com.runanywhere.classconnect.util.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, sessionManager: SessionManager) {
    val scope = rememberCoroutineScope()
    val gradientColors = listOf(
        Color(0xFF0C0C1C),
        Color(0xFF1A1A2E),
        Color(0xFF16213E),
        Color(0xFF0F3460)
    )

    var profile by remember { mutableStateOf<UserProfile?>(null) }

    // 🔹 Load user profile from DataStore
    LaunchedEffect(Unit) {
        sessionManager.userProfile.collectLatest { saved ->
            profile = saved
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profileSetup") }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color(0xFF66CCFF)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Brush.verticalGradient(gradientColors))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // =======================
                // 🔹 LOADING INDICATOR
                // =======================
                if (profile == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFF66CCFF))
                            Text(
                                "Loading profile...",
                                color = Color.White.copy(0.7f)
                            )
                        }
                    }
                    return@Column
                }

                // =======================
                // 🔹 FINAL PROFILE DETAILS
                // =======================
                val user = profile!!

                Spacer(Modifier.height(24.dp))

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF66CCFF).copy(alpha = 0.2f))
                        .border(3.dp, Color(0xFF66CCFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.imageUri.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = user.imageUri),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Default Profile",
                            tint = Color.White,
                            modifier = Modifier.size(90.dp)
                        )
                    }

                }

                Spacer(Modifier.height(20.dp))

                // Name & Department
                Text(
                    user.name.ifBlank { "Student" },
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                if (user.department.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        user.department,
                        color = Color.White.copy(0.7f),
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(32.dp))

                // =======================
                // 🔹 INFO CARD
                // =======================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (user.year.isNotBlank()) {
                            ProfileInfoRow("Academic Year", user.year)
                        }

                        if (user.college.isNotBlank()) {
                            ProfileInfoRow("College", user.college)
                        }

                        if (user.time.isNotBlank()) {
                            ProfileInfoRow("Preferred Study Time", user.time)
                        }

                        if (user.bio.isNotBlank()) {
                            ProfileInfoRow("Bio", user.bio)
                        }

                        if (user.skills.isNotEmpty()) {
                            ProfileInfoRow("Skills", user.skills.joinToString(", "))
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Logout Button - FIXED: Using coroutine scope for suspend function
                Button(
                    onClick = {
                        scope.launch {
                            // This is now inside a coroutine, so we can call suspend functions
                            sessionManager.clearSession()
                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color.White.copy(0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        )
        Divider(
            color = Color.White.copy(alpha = 0.1f),
            thickness = 1.dp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}