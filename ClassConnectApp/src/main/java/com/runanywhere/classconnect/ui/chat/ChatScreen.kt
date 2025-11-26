package com.runanywhere.classconnect.ui.chat

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.classconnect.viewmodels.ChatViewModel
import com.runanywhere.classconnect.viewmodels.ChatMessage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val messages by viewModel.messages.collectAsState()
    var userInput by remember { mutableStateOf("") }
    var showVersionDialog by remember { mutableStateOf(false) }
    var selectedVersion by remember { mutableStateOf("Study Pro") }
    var isRecording by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "StudyBot",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "$selectedVersion • Your AI Study Buddy",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Version selector dropdown
                    IconButton(onClick = { showVersionDialog = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "AI Version",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF020617)
                )
            )
        },
        containerColor = Color(0xFF020617)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF020617),
                            Color(0xFF020617),
                            Color(0xFF0B1120)
                        )
                    )
                )
                .padding(padding)
        ) {

            // MESSAGES LIST
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }

                // Typing indicator when AI is responding
                if (messages.lastOrNull()?.isUser == true) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            Divider(color = Color.White.copy(alpha = 0.1f))

            // INPUT AREA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Voice Input Button
                IconButton(
                    onClick = {
                        isRecording = !isRecording
                        if (isRecording) {
                            // Simulate voice input
                            simulateVoiceInput(coroutineScope) { text ->
                                userInput = text
                                isRecording = false
                            }
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) Color(0xFFEF4444) else Color(0xFF1E293B)
                        ),
                ) {
                    Icon(
                        Icons.Default.KeyboardVoice,
                        contentDescription = "Voice Input",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Input Field
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Message StudyBot...",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF38BDF8),
                        containerColor = Color(0xFF1E293B)
                    ),
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    singleLine = false
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            viewModel.sendMessage(userInput.trim())
                            val textForAI = userInput.trim()
                            userInput = ""

                            // Simulate AI response after a delay
                            simulateAIResponse(viewModel, selectedVersion, coroutineScope, textForAI)
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (userInput.isNotBlank())
                                Color(0xFF2563EB)
                            else
                                Color(0xFF1E293B)
                        ),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // AI Version Selection Dialog
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("Select AI Version", color = Color.White) },
            text = {
                Column {
                    listOf(
                        "Study Basic" to "Fast responses for quick questions",
                        "Study Pro" to "Detailed explanations with examples",
                        "Study Expert" to "Advanced concepts & deep analysis",
                        "Creative Helper" to "Brainstorming & project ideas"
                    ).forEach { (version, description) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedVersion == version)
                                        Color(0xFF2563EB)
                                    else
                                        Color(0xFF1E293B)
                                )
                                .padding(12.dp)
                                .clickable {
                                    selectedVersion = version
                                    showVersionDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedVersion == version,
                                onClick = {
                                    selectedVersion = version
                                    showVersionDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White.copy(0.5f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(version, color = Color.White, fontWeight = FontWeight.Medium)
                                Text(
                                    description,
                                    color = Color.White.copy(0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) {
                    Text("Close", color = Color(0xFF38BDF8))
                }
            },
            containerColor = Color(0xFF020617)
        )
    }

    // Voice Recording Indicator (tap anywhere to cancel)
    if (isRecording) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .pointerInput(Unit) {
                    detectTapGestures {
                        // cancel recording on tap
                        isRecording = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Animated recording indicator
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "🎤",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Listening... Speak now",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tap anywhere to cancel",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    val bubbleColor = if (isUser) {
        // User – Blue bubble (iMessage style)
        Color(0xFF2563EB)
    } else {
        // Bot – Dark grey bubble
        Color(0xFF1F2933)
    }

    val bubbleShape = if (isUser) {
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 4.dp,
            bottomEnd = 18.dp,
            bottomStart = 18.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 18.dp,
            bottomEnd = 18.dp,
            bottomStart = 18.dp
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = formatTime(message.timestamp),
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(
                start = if (isUser) 0.dp else 4.dp,
                end = if (isUser) 4.dp else 0.dp
            )
        )
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1F2933))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Animated typing dots
        val dotAlpha = remember { Animatable(0.3f) }

        LaunchedEffect(Unit) {
            while (true) {
                dotAlpha.animateTo(1f, animationSpec = tween(500))
                dotAlpha.animateTo(0.3f, animationSpec = tween(500))
            }
        }

        Text(
            "StudyBot is typing",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = dotAlpha.value))
                        .padding(end = 2.dp)
                )
            }
        }
    }
}

// Simulate voice input (in real app, integrate with Speech-to-Text API)
private fun simulateVoiceInput(
    coroutineScope: CoroutineScope,
    onResult: (String) -> Unit
) {
    coroutineScope.launch {
        kotlinx.coroutines.delay(2000L) // 2 second delay to simulate listening

        // Sample responses based on common study queries
        val sampleResponses = listOf(
            "Can you explain quantum physics in simple terms?",
            "I need help with my calculus homework on derivatives",
            "What's the best way to study for finals?",
            "Can you create a study schedule for my exams?",
            "Explain the water cycle with a diagram"
        )

        onResult(sampleResponses.random())
    }
}

// Simulate AI response based on selected version
private fun simulateAIResponse(
    viewModel: ChatViewModel,
    version: String,
    coroutineScope: CoroutineScope,
    userMessage: String
) {
    coroutineScope.launch {
        // Simulate AI thinking time
        kotlinx.coroutines.delay(1500L)

        // Generate different responses based on AI version
        val response = when (version) {
            "Study Basic" -> generateBasicResponse()
            "Study Pro" -> generateProResponse()
            "Study Expert" -> generateExpertResponse()
            "Creative Helper" -> generateCreativeResponse()
            else -> generateProResponse()
        }

        // Add bot message to ViewModel
        viewModel.addMessage(
            ChatMessage(
                text = response,
                isUser = false,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}

private fun generateBasicResponse(): String {
    return "I can help with that! Here's a quick explanation:\n\n" +
            "• Key points summarized\n" +
            "• Easy to understand\n" +
            "• Perfect for quick revision"
}

private fun generateProResponse(): String {
    return "Great question! Let me break this down for you:\n\n" +
            "**Detailed Explanation:**\n" +
            "This concept involves multiple aspects that work together. First, we have the fundamental principles...\n\n" +
            "**Examples:**\n" +
            "1. Real-world application example\n" +
            "2. Step-by-step breakdown\n" +
            "3. Common mistakes to avoid\n\n" +
            "**Practice Tip:** Try applying this to different scenarios to reinforce your understanding!"
}

private fun generateExpertResponse(): String {
    return "Excellent query! Let's dive deep into this advanced topic:\n\n" +
            "**Advanced Analysis:**\n" +
            "From a theoretical perspective, this connects to several complex concepts including...\n\n" +
            "**Mathematical Foundation:**\n" +
            "• Advanced formulas and derivations\n" +
            "• Interdisciplinary connections\n" +
            "• Research-level insights\n\n" +
            "**Critical Thinking:** Consider how this applies to cutting-edge research and future developments."
}

private fun generateCreativeResponse(): String {
    return "Love the creative angle! Here are some innovative ideas:\n\n" +
            "💡 **Brainstorming Session:**\n" +
            "• Unconventional approaches\n" +
            "• Cross-disciplinary connections\n" +
            "• Out-of-the-box solutions\n\n" +
            "🎨 **Project Ideas:**\n" +
            "1. Interactive visualization\n" +
            "2. Creative presentation format\n" +
            "3. Real-world implementation plan\n\n" +
            "Let's build something amazing together!"
}

fun formatTime(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
