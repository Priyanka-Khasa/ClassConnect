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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
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
import androidx.compose.foundation.Canvas

@Composable
fun LoginScreen(navController: NavController, sessionManager: SessionManager) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    // 🌌 Premium Dark Gradient Background
    val gradientColors = listOf(
        Color(0xFF0C0C1C),
        Color(0xFF1A1A2E),
        Color(0xFF16213E),
        Color(0xFF0F3460)
    )

    val infiniteTransition = rememberInfiniteTransition()

    // Smooth floating animation
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 8000
                0.0f at 0
                0.5f at 3000
                1.0f at 6000
                0.5f at 7000
                0.0f at 8000
            }
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = Offset(0.3f, 0.3f),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Premium animated background
        PremiumAnimatedBackground(floatAnim)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // ✨ Premium entrance animation
            val scale = remember { Animatable(0.7f) }
            val rotationY = remember { Animatable(-15f) }
            val alpha = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                scale.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
                rotationY.animateTo(0f, animationSpec = tween(1200, easing = LinearOutSlowInEasing))
                alpha.animateTo(1f, animationSpec = tween(800))
            }

            // Premium Logo/Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(scale.value)
                    .graphicsLayer(
                        rotationY = rotationY.value,
                        alpha = alpha.value
                    )
                    .padding(bottom = 16.dp)
            ) {
                // Animated gradient text
                Text(
                    text = "ClassConnect",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White,
                    modifier = Modifier
                )

                Text(
                    "Elevate Your Learning Experience",
                    color = Color(0xFF8892B0),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 💎 Premium Glassmorphism Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        shadowElevation = 32f
                        shape = RoundedCornerShape(32.dp)
                        clip = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x1AFFFFFF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x26FFFFFF),
                                    Color(0x0DFFFFFF)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Welcome Back",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            "Continue your journey to excellence",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF8892B0)
                            ),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // 📧 Premium Email Field
                        PremiumTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            leadingIcon = Icons.Default.Email,
                            isPassword = false
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 🔒 Premium Password Field
                        PremiumTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            leadingIcon = Icons.Default.Lock,
                            isPassword = true,
                            showPassword = showPassword,
                            onTogglePassword = { showPassword = !showPassword }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔗 Premium Forgot Password
                        TextButton(
                            onClick = { /* TODO */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                "Forgot Password?",
                                color = Color(0xFF64FFDA),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 🚀 Premium Animated Login Button
                        val buttonScale by rememberInfiniteTransition().animateFloat(
                            initialValue = 1f,
                            targetValue = 1.02f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        val buttonGlow by rememberInfiniteTransition().animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(3000, easing = LinearEasing)
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .scale(buttonScale)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF667EEA),
                                            Color(0xFF764BA2),
                                            Color(0xFF667EEA)
                                        ),
                                        start = Offset(buttonGlow * 200f, 0f),
                                        end = Offset(200f + buttonGlow * 200f, 60f),
                                        tileMode = TileMode.Repeated
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Button(
                                onClick = {
                                    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                                    if (!isEmailValid) {
                                        return@Button
                                    }

                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        isLoading = true
                                        scope.launch {
                                            delay(1500)
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
                                    .fillMaxSize(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        "Continue to Profile",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 👤 Premium Signup Section
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "New to here? ",
                                color = Color(0xFF8892B0),
                                fontWeight = FontWeight.Medium
                            )
                            TextButton(
                                onClick = {
                                    navController.navigate("profileSetup")
                                }
                            ) {
                                Text(
                                    "set it",
                                    color = Color(0xFF64FFDA),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumTextField(
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
                tint = Color(0xFF64FFDA),
                modifier = Modifier.size(22.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0x15FFFFFF),
                RoundedCornerShape(16.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0x1AFFFFFF),
            unfocusedContainerColor = Color(0x15FFFFFF),
            focusedIndicatorColor = Color(0xFF64FFDA),
            unfocusedIndicatorColor = Color(0x66FFFFFF),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
            focusedLabelColor = Color(0xFF64FFDA),
            unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Composable
fun PremiumAnimatedBackground(floatValue: Float) {
    // Floating particles effect
    val particleColors = listOf(
        Color(0x1564FFDA), // Teal
        Color(0x15667EEA), // Purple
        Color(0x15FF6B6B)  // Coral
    )

    val particleData = listOf(
        Triple(0.1f, 0.2f, 120.dp),
        Triple(0.7f, 0.8f, 80.dp),
        Triple(0.4f, 0.3f, 150.dp),
        Triple(0.9f, 0.1f, 100.dp),
        Triple(0.2f, 0.9f, 60.dp)
    )

    // Background grid/lines effect
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw subtle grid
        for (i in 0..10) {
            val x = width * i / 10
            val y = height * i / 10

            drawLine(
                color = Color(0x0AFFFFFF),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            drawLine(
                color = Color(0x0AFFFFFF),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }
    }

    // Floating particles
    particleData.forEachIndexed { index, (startX, startY, size) ->
        val movement = (floatValue * 50f).dp
        val color = particleColors[index % particleColors.size]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = (startX * 400 - 200).dp + movement,
                    y = (startY * 400 - 200).dp + movement * (if (index % 2 == 0) 1f else -1f)
                )
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(color, Color.Transparent),
                            center = Offset(0.5f, 0.5f),
                            radius = 0.8f
                        ),
                        shape = CircleShape
                    )
                    .blur(8.dp)
            )
        }
    }

    // Animated gradient orbs
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = (-100).dp, y = (-100).dp)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x15667EEA), Color.Transparent),
                        center = Offset(0.3f, 0.3f),
                        radius = 300f
                    ),
                    shape = CircleShape
                )
                .offset(
                    x = (floatValue * 40).dp,
                    y = (floatValue * 30).dp
                )
                .blur(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = 300.dp, y = 500.dp)
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1564FFDA), Color.Transparent),
                        center = Offset(0.7f, 0.7f),
                        radius = 250f
                    ),
                    shape = CircleShape
                )
                .offset(
                    x = (floatValue * -20).dp,
                    y = (floatValue * 40).dp
                )
                .blur(16.dp)
        )
    }
}