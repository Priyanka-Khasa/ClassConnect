package com.runanywhere.classconnect.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.classconnect.model.UserProfile
import com.runanywhere.classconnect.util.SessionManager
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    navController: NavController,
    sessionManager: SessionManager
) {
    // ------------------ FORM STATE ------------------
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var selectedTime by remember { mutableStateOf("Evening") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Validation + loading
    var showValidationErrors by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Pending profile for safe side-effect
    var pendingProfile by remember { mutableStateOf<UserProfile?>(null) }

    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri = it }
    }

    // Background Gradient (premium but static)
    val gradientColors = listOf(
        Color(0xFF0C0C1C),
        Color(0xFF1A1A2E),
        Color(0xFF16213E),
        Color(0xFF0F3460)
    )

    // ✅ Stable side-effect for saving + navigation (no animation)
    LaunchedEffect(pendingProfile) {
        val profileToSave = pendingProfile ?: return@LaunchedEffect
        isLoading = true
        try {
            sessionManager.saveUserProfile(profileToSave)
            navController.navigate("dashboard") {
                popUpTo("profileSetup") { inclusive = true }
            }
        } catch (e: Exception) {
            // Could show a snackbar / error text later
            isLoading = false
        } finally {
            pendingProfile = null
        }
    }

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
            HeaderSection()

            Spacer(Modifier.height(24.dp))

            ProfileFormCard(
                name = name,
                department = department,
                year = year,
                college = college,
                bio = bio,
                selectedSkills = selectedSkills,
                selectedTime = selectedTime,
                imageUri = imageUri,
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
                onTimeSelect = { selectedTime = it },
                onImagePick = { imagePickerLauncher.launch("image/*") }
            )

            Spacer(Modifier.height(32.dp))

            // Save Button – sets pendingProfile, actual work in LaunchedEffect
            SaveButton(
                enabled = name.isNotBlank() && department.isNotBlank() && !isLoading,
                onClick = {
                    if (name.isBlank() || department.isBlank()) {
                        showValidationErrors = true
                    } else if (!isLoading) {
                        showValidationErrors = false
                        pendingProfile = UserProfile(
                            name = name,
                            department = department,
                            year = year,
                            college = college,
                            bio = bio,
                            skills = selectedSkills.toList(),
                            time = selectedTime,
                            imageUri = imageUri?.toString() ?: ""
                        )
                    }
                }
            )

            Spacer(Modifier.height(20.dp))
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(Modifier.height(16.dp))
                    Text("Saving Profile...", color = Color.White)
                }
            }
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
            textAlign = TextAlign.Center
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
    imageUri: Uri?,
    showValidationErrors: Boolean,
    onNameChange: (String) -> Unit,
    onDepartmentChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onCollegeChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSkillToggle: (String) -> Unit,
    onTimeSelect: (String) -> Unit,
    onImagePick: () -> Unit
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
            ProfileImagePicker(
                imageUri = imageUri,
                onImagePicked = onImagePick
            )

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

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            SkillsSection(
                selectedSkills = selectedSkills,
                onSkillToggle = onSkillToggle
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            StudyTimeSection(
                selectedTime = selectedTime,
                onTimeSelect = onTimeSelect
            )
        }
    }
}

@Composable
fun ProfileImagePicker(
    imageUri: Uri?,
    onImagePicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    if (imageUri != null) Color(0xFF66CCFF).copy(alpha = 0.3f)
                    else Color(0xFF66CCFF).copy(alpha = 0.1f),
                    CircleShape
                )
                .border(2.dp, Color(0xFF66CCFF), CircleShape)
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Placeholder",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }

        }

        Button(
            onClick = onImagePicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF66CCFF)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.AddAPhoto,
                contentDescription = "Add Photo",
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Add Profile Picture")
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

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.chunked(3).forEach { rowSkills ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowSkills.forEach { skill ->
                        FilterChip(
                            selected = selectedSkills.contains(skill),
                            onClick = { onSkillToggle(skill) },
                            label = {
                                Text(
                                    skill,
                                    color = if (selectedSkills.contains(skill))
                                        Color.White
                                    else
                                        Color.White.copy(alpha = 0.8f),
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
            listOf("Morning", "Afternoon", "Evening", "Night").forEach { time ->
                FilterChip(
                    selected = selectedTime == time,
                    onClick = { onTimeSelect(time) },
                    label = {
                        Text(
                            time,
                            color = if (selectedTime == time)
                                Color.White
                            else
                                Color.White.copy(alpha = 0.8f),
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
    leadingIcon: ImageVector? = null
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
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (showError) Color(0xFFFF6B6B) else Color(0xFF66CCFF),
                unfocusedBorderColor = if (showError) Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                cursorColor = Color.White,
                focusedLabelColor = if (showError) Color(0xFFFF6B6B) else Color(0xFF66CCFF),
                unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.08f)
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
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
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF66CCFF),
            disabledContainerColor = Color(0xFF66CCFF).copy(alpha = 0.5f)
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

