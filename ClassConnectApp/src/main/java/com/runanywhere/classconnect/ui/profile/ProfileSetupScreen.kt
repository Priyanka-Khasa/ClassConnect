package com.runanywhere.classconnect.ui.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

    // Form State
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var selectedTime by remember { mutableStateOf("Evening") }

    // Validation states
    var showValidationErrors by remember { mutableStateOf(false) }

    // Premium Gradient Background
    val gradientColors = listOf(
        Color(0xFF0C0C1C),
        Color(0xFF1A1A2E),
        Color(0xFF16213E),
        Color(0xFF0F3460)
    )

    // Animation
    val infiniteTransition = rememberInfiniteTransition()
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header Section
            HeaderSection()

            Spacer(Modifier.height(24.dp))

            // Premium Glassmorphic Card
            ProfileFormCard(
                name = name,
                department = department,
                year = year,
                college = college,
                bio = bio,
                selectedSkills = selectedSkills,
                selectedTime = selectedTime,
                showValidationErrors = showValidationErrors,
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

            Spacer(Modifier.height(32.dp))

            // Premium Save Button
            SaveButton(
                scaleAnim = scaleAnim,
                enabled = name.isNotBlank() && department.isNotBlank(),
                onClick = {
                    if (name.isBlank() || department.isBlank()) {
                        showValidationErrors = true
                    } else {
                        showValidationErrors = false
                        scope.launch {
                            sessionManager.setLoginState(true)
                            navController.navigate("dashboard") {
                                popUpTo("profileSetup") { inclusive = true }
                            }
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Setup",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF66CCFF)
        )

        Text(
            "Complete Your Profile",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Tell us more about yourself to personalize your learning experience",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
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
    showValidationErrors: Boolean,
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0x1AFFFFFF)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Personal Information Section
            PersonalInfoSection(
                name = name,
                department = department,
                year = year,
                college = college,
                bio = bio,
                showValidationErrors = showValidationErrors,
                onNameChange = onNameChange,
                onDepartmentChange = onDepartmentChange,
                onYearChange = onYearChange,
                onCollegeChange = onCollegeChange,
                onBioChange = onBioChange
            )

            Divider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            // Skills Section
            SkillsSection(
                selectedSkills = selectedSkills,
                onSkillToggle = onSkillToggle
            )

            Divider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

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
    showValidationErrors: Boolean,
    onNameChange: (String) -> Unit,
    onDepartmentChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onCollegeChange: (String) -> Unit,
    onBioChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Personal Info",
                tint = Color(0xFF66CCFF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Personal Information",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        ProfileTextField(
            label = "Full Name",
            value = name,
            onChange = onNameChange,
            isRequired = true,
            showError = showValidationErrors && name.isBlank(),
            leadingIcon = Icons.Default.Person
        )

        ProfileTextField(
            label = "Department",
            value = department,
            onChange = onDepartmentChange,
            isRequired = true,
            showError = showValidationErrors && department.isBlank(),
            leadingIcon = Icons.Default.School
        )

        ProfileTextField(
            label = "Academic Year",
            value = year,
            onChange = onYearChange,
            leadingIcon = Icons.Default.CalendarToday
        )

        ProfileTextField(
            label = "College/University",
            value = college,
            onChange = onCollegeChange,
            leadingIcon = Icons.Default.LocationOn
        )

        ProfileTextField(
            label = "Bio",
            value = bio,
            onChange = onBioChange,
            singleLine = false,
            leadingIcon = Icons.Default.Description
        )
    }
}

@Composable
fun SkillsSection(
    selectedSkills: Set<String>,
    onSkillToggle: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Skills",
                tint = Color(0xFF66CCFF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Technical Skills",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        Text(
            "Select skills that match your interests",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        val skills = listOf(
            "Data Structures", "Machine Learning", "Artificial Intelligence",
            "Web Development", "Android Development", "Cloud Computing",
            "IoT", "Embedded Systems", "Database Management"
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.forEach { skill ->
                FilterChip(
                    selected = selectedSkills.contains(skill),
                    onClick = { onSkillToggle(skill) },
                    label = {
                        Text(
                            skill,
                            color = if (selectedSkills.contains(skill)) Color.White else Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF66CCFF),
                        containerColor = Color.White.copy(alpha = 0.15f),
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
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
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Study Time",
                tint = Color(0xFF66CCFF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Preferred Study Time",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        Text(
            "When are you most productive?",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Mor", "Afte", "Eve", "Nig").forEach { time ->
                FilterChip(
                    selected = selectedTime == time,
                    onClick = { onTimeSelect(time) },
                    label = {
                        Text(
                            time,
                            color = if (selectedTime == time) Color.White else Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF66CCFF),
                        containerColor = Color.White.copy(alpha = 0.15f),
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
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
    showError: Boolean = false,
    singleLine: Boolean = true,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = {
                Text(
                    text = if (isRequired) "$label *" else label,
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.12f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                focusedIndicatorColor = if (showError) Color(0xFFFF6B6B) else Color(0xFF66CCFF),
                unfocusedIndicatorColor = if (showError) Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.4f),
                cursorColor = Color.White,
                focusedLabelColor = if (showError) Color(0xFFFF6B6B) else Color(0xFF66CCFF)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 3,
            isError = showError
        )

        if (showError) {
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
            .height(58.dp)
            .scale(scaleAnim),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF66CCFF),
            disabledContainerColor = Color(0xFF66CCFF).copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        ),
        enabled = enabled
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "Complete Profile & Continue",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}