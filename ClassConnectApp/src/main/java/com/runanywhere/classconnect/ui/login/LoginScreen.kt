package com.runanywhere.classconnect.ui.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.runanywhere.classconnect.util.SessionManager


@Composable
fun LoginScreen(navController: NavController, sessionManager: SessionManager) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    // 🌈 Enhanced gradient background
    val gradientColors = listOf(
        Color(0xFF667EEA),
        Color(0xFF764BA2),
        Color(0xFFF093FB)
    )

    val infiniteTransition = rememberInfiniteTransition()

    // Smooth floating animation for orbs
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 6000
                0.0f at 0
                0.5f at 2000
                1.0f at 4000
                0.5f at 5000
                0.0f at 6000
            }
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Enhanced animated background elements
        EnhancedAnimatedBackground(floatAnim)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // ✨ Enhanced entrance animation
            val scale = remember { Animatable(0.8f) }
            val rotationY = remember { Animatable(0f) }
            val alpha = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                scale.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
                rotationY.animateTo(360f, animationSpec = tween(1200, easing = LinearEasing))
                alpha.animateTo(1f, animationSpec = tween(600))
            }

            Text(
                text = "🎓 ClassConnect",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                ),
                modifier = Modifier
                    .scale(scale.value)
                    .graphicsLayer(
                        rotationY = rotationY.value,
                        alpha = alpha.value
                    )
                    .padding(bottom = 8.dp)
            )

            Text(
                "Your Smart Study Companion",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp, // ✅ FIXED: Added .sp
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 🪟 Enhanced Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        shadowElevation = 24f
                        shape = RoundedCornerShape(28.dp)
                        clip = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Welcome Back! ",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        "Sign in to continue your learning journey",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(bottom = 28.dp)
                    )

                    // 📧 Enhanced Email Field
                    EnhancedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        isPassword = false
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 🔒 Enhanced Password Field
                    EnhancedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        showPassword = showPassword,
                        onTogglePassword = { showPassword = !showPassword }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔗 Forgot Password
                    TextButton(
                        onClick = { /* TODO: Implement forgot password */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Forgot Password?",
                            color = Color(0xFF80D8FF),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 🎯 Enhanced Animated Login Button
                    val pulse by rememberInfiniteTransition().animateFloat(
                        initialValue = 1f,
                        targetValue = 1.08f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Button(
                        onClick = {
                            val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                            if (!isEmailValid) {
                                // optional: show error snackbar
                                return@Button
                            }

                            if (email.isNotBlank() && password.isNotBlank()) {
                                isLoading = true
                                scope.launch {
                                    delay(1500)

                                    // ✅ save session when login succeeds
                                    sessionManager.saveLoginSession(email)

                                    isLoading = false
                                    navController.navigate("profileSetup") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        },

                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .scale(pulse),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF5C6BC0)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color(0xFF5C6BC0),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Continue to Profile Setup",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp // ✅ FIXED: Added .sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 👤 Enhanced Signup Section
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Don't have account? ",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        TextButton(
                            onClick = {
                                // TODO: Implement signup flow
                                // For now, navigate to profile setup
                                navController.navigate("profileSetup")
                            }
                        ) {
                            Text(
                                "Go Up",
                                color = Color(0xFF80D8FF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                leadingIcon,
                null,
                tint = Color(0xFF80D8FF),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (isPassword && onTogglePassword != null) {
                IconButton(
                    onClick = onTogglePassword,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (showPassword)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.18f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
            focusedIndicatorColor = Color(0xFF80D8FF),
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.4f),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
            focusedLabelColor = Color(0xFF80D8FF),
            unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}

@Composable
fun EnhancedAnimatedBackground(floatValue: Float) {
    // Multiple floating orbs with different animations
    val orbColors = listOf(
        Color.White.copy(alpha = 0.06f),
        Color.White.copy(alpha = 0.04f),
        Color.White.copy(alpha = 0.03f)
    )

    val orbSizes = listOf(280.dp, 200.dp, 150.dp)
    val orbOffsets = listOf(
        Pair((-80).dp, (-100).dp),
        Pair(250.dp, 400.dp),
        Pair(150.dp, (-50).dp)
    )
    val orbMovements = listOf(
        Pair(40f, 30f),
        Pair(-30f, 20f),
        Pair(20f, -15f)
    )

    orbColors.forEachIndexed { index, color ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = orbOffsets[index].first, y = orbOffsets[index].second)
        ) {
            Box(
                modifier = Modifier
                    .size(orbSizes[index])
                    .background(color = color, shape = CircleShape)
                    .offset(
                        x = (floatValue * orbMovements[index].first).dp,
                        y = (floatValue * orbMovements[index].second).dp
                    )
            )
        }
    }
}

