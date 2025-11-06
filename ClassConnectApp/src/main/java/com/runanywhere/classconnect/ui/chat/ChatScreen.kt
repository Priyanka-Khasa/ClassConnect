package com.runanywhere.classconnect.ui.chat

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.runanywhere.classconnect.util.SpeechToTextHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT, CODE, SYSTEM, SUGGESTION
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(navController: NavController, studentName: String = "StudyBot") {
    var userInput by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var isTyping by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    // Speech Recognizer
    val speechHelper = remember {
        SpeechToTextHelper(
            activity = activity,
            onResult = { spokenText ->
                userInput = spokenText
                isListening = false
            },
            onError = {
                isListening = false
            }
        )
    }

    // Permission handling
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isListening = true
            speechHelper.startListening()
        } else {
            isListening = false
        }
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            speechHelper.destroy()
        }
    }

    // Auto-scroll when new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(100)
            lazyListState.animateScrollToItem(0)
        }
    }

    // Welcome message
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            delay(500)
            messages.add(
                ChatMessage(
                    "👋 Hello! I'm your AI study assistant.\n\n" +
                            "📚 I can help you with:\n" +
                            "• Course concepts & explanations\n" +
                            "• Study strategies & schedules\n" +
                            "• Project ideas & guidance\n" +
                            "• Assignment help & debugging\n" +
                            "• Code review & optimization\n\n" +
                            "What would you like to learn today?",
                    false
                )
            )
        }
    }

    // 🔮 Animated background
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
                    start = androidx.compose.ui.geometry.Offset(shimmerOffset, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, shimmerOffset)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 🌟 Enhanced Header
            EnhancedChatHeader(navController = navController, studentName = studentName)

            // 💬 Messages Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    state = lazyListState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages.reversed(), key = { it.timestamp }) { message ->
                        EnhancedChatBubble(message = message)
                    }

                    if (isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                }

                // Scroll to bottom button
                if (lazyListState.firstVisibleItemIndex > 5) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(0)
                                }
                            },
                            containerColor = Color(0xFF66CCFF),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.ArrowDownward, contentDescription = "Scroll to bottom")
                        }
                    }
                }
            }

            // 🎙 Input Area
            EnhancedInputArea(
                userInput = userInput,
                onInputChange = { userInput = it },
                messages = messages,
                isListening = isListening,
                onToggleListening = {
                    if (isListening) {
                        isListening = false
                        speechHelper.stopListening()
                    } else {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            isListening = true
                            speechHelper.startListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                onSendMessage = { text ->
                    if (text.isNotBlank()) {
                        messages.add(ChatMessage(text, true))
                        userInput = ""
                        isTyping = true

                        coroutineScope.launch {
                            delay(300)
                            simulateAIResponse(messages, text) {
                                isTyping = false
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun EnhancedChatHeader(navController: NavController, studentName: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        color = Color.White.copy(alpha = 0.12f),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                            .scale(pulse)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "💬 $studentName",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "AI Study Assistant • Online",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Row {
                IconButton(
                    onClick = { /* Clear chat */ },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Clear chat",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    val bubbleColor = if (isUser)
        Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))
    else
        Brush.linearGradient(listOf(Color(0xFF4CA1AF), Color(0xFF2C3E50)))

    val align = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = if (isUser)
        RoundedCornerShape(20.dp, 8.dp, 20.dp, 20.dp)
    else
        RoundedCornerShape(8.dp, 20.dp, 20.dp, 20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = align
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                shape = bubbleShape,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .clip(bubbleShape)
                        .background(bubbleColor)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        if (message.messageType == MessageType.CODE) {
                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            )
                        } else {
                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = formatTime(message.timestamp),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "AI is typing",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                TypingDots()
            }
        }
    }
}

@Composable
fun TypingDots() {
    val infiniteTransition = rememberInfiniteTransition()

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                this.durationMillis = 1200
                0.3f at 0
                1f at 400
                0.3f at 800
            }
        )
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                this.durationMillis = 1200
                0.3f at 200
                1f at 600
                0.3f at 1000
            }
        )
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                this.durationMillis = 1200
                0.3f at 400
                1f at 800
                0.3f at 1200
            }
        )
    )

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color.White.copy(alpha = dot1Alpha), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color.White.copy(alpha = dot2Alpha), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color.White.copy(alpha = dot3Alpha), CircleShape)
        )
    }
}

@Composable
fun EnhancedInputArea(
    userInput: String,
    onInputChange: (String) -> Unit,
    messages: List<ChatMessage>,
    isListening: Boolean,
    onToggleListening: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        color = Color.White.copy(alpha = 0.08f),
        tonalElevation = 12.dp,
        shadowElevation = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val hasUserMessages = messages.any { it.isUser }
            if (!hasUserMessages && userInput.isEmpty()) {
                EnhancedQuickSuggestions(onSuggestionClick = onInputChange)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleListening,
                    modifier = Modifier
                        .size(52.dp)
                        .scale(if (isListening) pulse else 1f)
                        .background(
                            if (isListening) Color.Red.copy(alpha = 0.2f)
                            else Color(0xFF66CCFF).copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop listening" else "Start voice input",
                        tint = if (isListening) Color.Red else Color(0xFF66CCFF),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent),
                    value = userInput,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            if (isListening) "Listening... Speak now" else "Ask anything about your studies...",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.12f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF66CCFF),
                        focusedIndicatorColor = Color(0xFF66CCFF),
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(25.dp),
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(12.dp))

                FloatingActionButton(
                    onClick = { onSendMessage(userInput) },
                    containerColor = if (userInput.isNotBlank()) Color(0xFF66CCFF) else Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send, // Fixed deprecated icon
                        contentDescription = "Send",
                        tint = if (userInput.isNotBlank()) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedQuickSuggestions(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf(
        "Explain machine learning basics in simple terms",
        "Help me create a study schedule for finals",
        "What are good project ideas for AI course?",
        "How to improve my coding skills effectively?"
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "💡 Try asking:",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(suggestions) { suggestion ->
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { onSuggestionClick(suggestion) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = suggestion,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

suspend fun simulateAIResponse(messages: MutableList<ChatMessage>, input: String, onComplete: () -> Unit) {
    val thinking = ChatMessage("⏳ Thinking...", false)
    messages.add(thinking)
    delay(1200 + (input.length * 8L))
    messages.remove(thinking)
    val response = generateEnhancedAIResponse(input)
    messages.add(ChatMessage(response, false))
    onComplete()
}

fun generateEnhancedAIResponse(input: String): String {
    val lower = input.lowercase()
    return when {
        "machine learning" in lower || "ml" in lower ->
            "🤖 **Machine Learning Basics**\n\n" +
                    "ML enables computers to learn from data without explicit programming. Key concepts:\n\n" +
                    "• **Supervised Learning**: Labeled data (classification, regression)\n" +
                    "• **Unsupervised Learning**: Unlabeled data (clustering, dimensionality reduction)\n" +
                    "• **Reinforcement Learning**: Learn through rewards/punishments\n\n" +
                    "Popular libraries: TensorFlow, PyTorch, Scikit-learn"

        "dbms" in lower || "database" in lower ->
            "🗄️ **Database Management Systems**\n\n" +
                    "DBMS manages data storage, retrieval, and manipulation efficiently:\n\n" +
                    "• **SQL**: Structured Query Language for database operations\n" +
                    "• **Normalization**: Reduces data redundancy (1NF to 5NF)\n" +
                    "• **Indexing**: Improves query performance\n" +
                    "• **ACID Properties**: Atomicity, Consistency, Isolation, Durability\n\n" +
                    "Need help with a specific DBMS concept?"

        "schedule" in lower || "study plan" in lower ->
            "📅 **Effective Study Scheduling**\n\n" +
                    "**Pomodoro Technique**:\n" +
                    "• 25min focused study + 5min break\n" +
                    "• After 4 sessions, take 15-30min break\n\n" +
                    "**Spaced Repetition**:\n" +
                    "• Review material at increasing intervals\n" +
                    "• Use apps like Anki for flashcards\n\n" +
                    "**Weekly Planning**:\n" +
                    "• Dedicate specific times for each subject\n" +
                    "• Include buffer time for revisions"

        "project" in lower || "project idea" in lower ->
            "💡 **AI Project Ideas**\n\n" +
                    "**Beginner**:\n" +
                    "• Sentiment Analysis on social media\n" +
                    "• Chatbot for FAQs\n" +
                    "• Image classification\n\n" +
                    "**Intermediate**:\n" +
                    "• Recommendation system\n" +
                    "• Stock price prediction\n" +
                    "• Face recognition system\n\n" +
                    "**Advanced**:\n" +
                    "• Autonomous vehicle simulation\n" +
                    "• Medical diagnosis assistant\n" +
                    "• Real-time object detection"

        else ->
            "That's an interesting question! 🌟\n\n" +
                    "I'd be happy to help you with that. Could you provide more details about what specific aspect you'd like me to explain?\n\n" +
                    "For example:\n" +
                    "• Are you looking for basic concepts or advanced details?\n" +
                    "• Do you need practical examples or theoretical explanations?\n" +
                    "• Is this for a specific project or assignment?"
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}