package com.runanywhere.classconnect.ui.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Simple session manager for demo
    val sessionManager = remember {
        object {
            fun setLoginState(loggedIn: Boolean) {
                // Demo implementation
            }
        }
    }

    // 🧠 Form State - FIX: Use immutable state for collections
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) } // FIX: Use Set instead of MutableList
    var selectedTime by remember { mutableStateOf("Evening") }

    // 🌈 Static Gradient Background (simpler approach)
    val gradientColors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))

    // ✨ Simple float animation for button
    val infiniteTransition = rememberInfiniteTransition()
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            HeaderSection()

            Spacer(Modifier.height(16.dp))

            // 🪟 Glassmorphic Card
            ProfileFormCard(
                name = name,
                department = department,
                year = year,
                college = college,
                bio = bio,
                selectedSkills = selectedSkills,
                selectedTime = selectedTime,
                onNameChange = { name = it },
                onDepartmentChange = { department = it },
                onYearChange = { year = it },
                onCollegeChange = { college = it },
                onBioChange = { bio = it },
                onSkillToggle = { skill ->
                    selectedSkills = if (selectedSkills.contains(skill)) {
                        selectedSkills - skill
                    } else {
                        selectedSkills + skill
                    }
                },
                onTimeSelect = { selectedTime = it }
            )

            Spacer(Modifier.height(30.dp))

            // 💾 Animated Save Button
            SaveButton(
                scaleAnim = scaleAnim,
                enabled = name.isNotBlank() && department.isNotBlank(),
                onClick = {
                    scope.launch {
                        sessionManager.setLoginState(true)
                        navController.navigate("dashboard") {
                            popUpTo("profileSetup") { inclusive = true }
                        }
                    }
                }
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Set Up Your Profile ✨",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Tell us more so ClassConnect can personalize your experience.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 8.dp),
            lineHeight = 20.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ProfileFormCard(
    name: String,
    department: String,
    year: String,
    college: String,
    bio: String,
    selectedSkills: Set<String>,
    selectedTime: String,
    onNameChange: (String) -> Unit,
    onDepartmentChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onCollegeChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSkillToggle: (String) -> Unit,
    onTimeSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            // Personal Information Section
            PersonalInfoSection(
                name = name,
                department = department,
                year = year,
                college = college,
                bio = bio,
                onNameChange = onNameChange,
                onDepartmentChange = onDepartmentChange,
                onYearChange = onYearChange,
                onCollegeChange = onCollegeChange,
                onBioChange = onBioChange
            )

            Spacer(Modifier.height(16.dp))

            // Skills Section
            SkillsSection(
                selectedSkills = selectedSkills,
                onSkillToggle = onSkillToggle
            )

            Spacer(Modifier.height(16.dp))

            // Study Time Section
            StudyTimeSection(
                selectedTime = selectedTime,
                onTimeSelect = onTimeSelect
            )
        }
    }
}

@Composable
fun PersonalInfoSection(
    name: String,
    department: String,
    year: String,
    college: String,
    bio: String,
    onNameChange: (String) -> Unit,
    onDepartmentChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onCollegeChange: (String) -> Unit,
    onBioChange: (String) -> Unit
) {
    Column {
        ProfileTextField(
            label = "Full Name",
            value = name,
            onChange = onNameChange,
            isRequired = true
        )
        ProfileTextField(
            label = "Department",
            value = department,
            onChange = onDepartmentChange,
            isRequired = true
        )
        ProfileTextField(
            label = "Year (e.g., 2nd Year)",
            value = year,
            onChange = onYearChange
        )
        ProfileTextField(
            label = "College/University",
            value = college,
            onChange = onCollegeChange
        )
        ProfileTextField(
            label = "Short Bio",
            value = bio,
            onChange = onBioChange,
            singleLine = false
        )
    }
}

@Composable
fun SkillsSection(
    selectedSkills: Set<String>,
    onSkillToggle: (String) -> Unit
) {
    Column {
        Text("Select Your Skills 🎯", color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        val skills = listOf("DSA", "ML", "AI", "IoT", "Web", "Android", "Cloud", "Embedded")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            skills.forEach { skill ->
                FilterChip(
                    selected = selectedSkills.contains(skill),
                    onClick = { onSkillToggle(skill) },
                    label = {
                        Text(
                            skill,
                            color = if (selectedSkills.contains(skill)) Color.White else Color.White.copy(alpha = 0.8f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF66CCFF).copy(alpha = 0.6f),
                        containerColor = Color.White.copy(alpha = 0.15f),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun StudyTimeSection(
    selectedTime: String,
    onTimeSelect: (String) -> Unit
) {
    Column {
        Text("Preferred Study Time ⏰", color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Morning", "Afternoon", "Evening", "Night").forEach { time ->
                FilterChip(
                    selected = selectedTime == time,
                    onClick = { onTimeSelect(time) },
                    label = {
                        Text(
                            time,
                            color = if (selectedTime == time) Color.White else Color.White.copy(alpha = 0.8f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF66CCFF).copy(alpha = 0.6f),
                        containerColor = Color.White.copy(alpha = 0.15f),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    isRequired: Boolean = false,
    singleLine: Boolean = true
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = {
                Text(
                    text = if (isRequired) "$label *" else label,
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.12f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                focusedIndicatorColor = Color(0xFF66CCFF),
                unfocusedIndicatorColor = Color.White.copy(alpha = 0.4f),
                cursorColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 3
        )
        if (isRequired && value.isEmpty()) {
            Text(
                "This field is required",
                color = Color(0xFFFF6B6B),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun SaveButton(
    scaleAnim: Float,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .scale(scaleAnim),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF66CCFF),
            disabledContainerColor = Color(0xFF66CCFF).copy(alpha = 0.5f)
        ),
        enabled = enabled
    ) {
        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text("Save & Continue", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// Add this import at the top if FlowRow is not recognized
// import androidx.compose.foundation.layout.FlowRow