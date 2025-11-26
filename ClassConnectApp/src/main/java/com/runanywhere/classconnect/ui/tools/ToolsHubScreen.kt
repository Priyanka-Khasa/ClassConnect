package com.runanywhere.classconnect.ui.tools

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class ToolItem(
    val name: String,
    val description: String,
    val url: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val gradient: List<Color>,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsHubScreen(navController: NavController) {
    val context = LocalContext.current
    var showAddToolDialog by remember { mutableStateOf(false) }

    // Custom tools stored in state
    val customTools = remember { mutableStateListOf<ToolItem>() }

    val toolsByCategory = remember {
        listOf(
            // 🤖 AI & Chat Tools (20+ tools)
            listOf(
                ToolItem("ChatGPT", "Advanced AI conversations", "https://chat.openai.com", Icons.Filled.SmartToy, listOf(Color(0xFF74AA9C), Color(0xFF3B82F6)), "AI & Chat"),
                ToolItem("Gemini", "Google's AI assistant", "https://gemini.google.com", Icons.Filled.AutoAwesome, listOf(Color(0xFF667EEA), Color(0xFF764BA2)), "AI & Chat"),
                ToolItem("Claude AI", "Anthropic's AI assistant", "https://claude.ai", Icons.Filled.Psychology, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "AI & Chat"),
                ToolItem("HuggingFace", "AI models & datasets", "https://huggingface.co", Icons.Filled.ModelTraining, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "AI & Chat"),
                ToolItem("RunAnywhere SDK", "Local AI deployment", "https://runanywhere.ai", Icons.Filled.DeveloperBoard, listOf(Color(0xFF14B8A6), Color(0xFF0D9488)), "AI & Chat"),
                ToolItem("Midjourney", "AI image generation", "https://midjourney.com", Icons.Filled.Palette, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "AI & Chat"),
                ToolItem("DALL-E", "AI image creation", "https://openai.com/dall-e", Icons.Filled.Brush, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "AI & Chat"),
                ToolItem("Stable Diffusion", "Open source AI art", "https://stability.ai", Icons.Filled.ColorLens, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "AI & Chat"),
                ToolItem("Perplexity AI", "AI search engine", "https://perplexity.ai", Icons.Filled.Search, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "AI & Chat"),
                ToolItem("Character AI", "AI character chats", "https://character.ai", Icons.Filled.Person, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "AI & Chat"),
                ToolItem("Jasper AI", "AI writing assistant", "https://jasper.ai", Icons.Filled.Edit, listOf(Color(0xFF10B981), Color(0xFF059669)), "AI & Chat"),
                ToolItem("Copy.ai", "AI content creation", "https://copy.ai", Icons.Filled.ContentCopy, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "AI & Chat"),
                ToolItem("ElevenLabs", "AI voice generation", "https://elevenlabs.io", Icons.Filled.RecordVoiceOver, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "AI & Chat"),
                ToolItem("Synthesia", "AI video creation", "https://synthesia.io", Icons.Filled.Videocam, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "AI & Chat"),
                ToolItem("Runway ML", "AI video editing", "https://runwayml.com", Icons.Filled.Movie, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "AI & Chat"),
                ToolItem("Leonardo AI", "AI art generation", "https://leonardo.ai", Icons.Filled.Palette, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "AI & Chat"),
                ToolItem("Playground AI", "AI image creation", "https://playground.ai", Icons.Filled.Games, listOf(Color(0xFF10B981), Color(0xFF059669)), "AI & Chat"),
                ToolItem("DeepAI", "AI image & text tools", "https://deepai.org", Icons.Filled.DeveloperBoard, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "AI & Chat"),
                ToolItem("QuickDraw", "AI drawing game", "https://quickdraw.withgoogle.com", Icons.Filled.Draw, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "AI & Chat"),
                ToolItem("This Person Doesn't Exist", "AI generated faces", "https://thispersondoesnotexist.com", Icons.Filled.Person, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "AI & Chat")
            ),

            // 💻 Coding & Development (25+ tools)
            listOf(
                ToolItem("LeetCode", "Coding interviews prep", "https://leetcode.com", Icons.Filled.Code, listOf(Color(0xFF10B981), Color(0xFF059669)), "Coding & Dev"),
                ToolItem("GitHub", "Code collaboration", "https://github.com", Icons.Filled.Storage, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Coding & Dev"),
                ToolItem("Stack Overflow", "Developer community", "https://stackoverflow.com", Icons.Filled.Forum, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "Coding & Dev"),
                ToolItem("GitLab", "DevOps platform", "https://gitlab.com", Icons.Filled.IntegrationInstructions, listOf(Color(0xFFE24329), Color(0xFFFC6D26)), "Coding & Dev"),
                ToolItem("VS Code Online", "Cloud code editor", "https://vscode.dev", Icons.Filled.Computer, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Coding & Dev"),
                ToolItem("Replit", "Online IDE", "https://replit.com", Icons.Filled.Terminal, listOf(Color(0xFF667EEA), Color(0xFF764BA2)), "Coding & Dev"),
                ToolItem("CodePen", "Frontend playground", "https://codepen.io", Icons.Filled.Web, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Coding & Dev"),
                ToolItem("JSFiddle", "JavaScript testing", "https://jsfiddle.net", Icons.Filled.Code, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "Coding & Dev"),
                ToolItem("CodeSandbox", "Online code editor", "https://codesandbox.io", Icons.Filled.DeveloperBoard, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Coding & Dev"),
                ToolItem("Glitch", "Web app prototyping", "https://glitch.com", Icons.Filled.Bolt, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "Coding & Dev"),
                ToolItem("HackerRank", "Coding challenges", "https://hackerrank.com", Icons.Filled.EmojiEvents, listOf(Color(0xFF10B981), Color(0xFF059669)), "Coding & Dev"),
                ToolItem("Codecademy", "Learn to code", "https://codecademy.com", Icons.Filled.School, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Coding & Dev"),
                ToolItem("FreeCodeCamp", "Free coding courses", "https://freecodecamp.org", Icons.AutoMirrored.Filled.MenuBook, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Coding & Dev"),
                ToolItem("W3Schools", "Web development tutorials", "https://w3schools.com", Icons.Filled.Html, listOf(Color(0xFF10B981), Color(0xFF059669)), "Coding & Dev"),
                ToolItem("MDN Web Docs", "Web documentation", "https://developer.mozilla.org", Icons.Filled.Description, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Coding & Dev"),
                ToolItem("Can I Use", "Browser compatibility", "https://caniuse.com", Icons.Filled.Devices, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Coding & Dev"),
                ToolItem("CSS Tricks", "CSS resources", "https://css-tricks.com", Icons.Filled.Palette, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "Coding & Dev"),
                ToolItem("DevDocs", "API documentation", "https://devdocs.io", Icons.AutoMirrored.Filled.MenuBook, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Coding & Dev"),
                ToolItem("GitHub Pages", "Free web hosting", "https://pages.github.com", Icons.Filled.Public, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Coding & Dev"),
                ToolItem("Netlify", "Web deployment", "https://netlify.com", Icons.Filled.CloudUpload, listOf(Color(0xFF14B8A6), Color(0xFF0D9488)), "Coding & Dev"),
                ToolItem("Vercel", "Frontend deployment", "https://vercel.com", Icons.Filled.RocketLaunch, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Coding & Dev"),
                ToolItem("Codecov", "Code coverage", "https://codecov.io", Icons.Filled.Analytics, listOf(Color(0xFF10B981), Color(0xFF059669)), "Coding & Dev"),
                ToolItem("Travis CI", "Continuous integration", "https://travis-ci.org", Icons.Filled.Build, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Coding & Dev"),
                ToolItem("CircleCI", "CI/CD platform", "https://circleci.com", Icons.Filled.Autorenew, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Coding & Dev"),
                ToolItem("GitHub Actions", "Automation platform", "https://github.com/features/actions", Icons.Filled.PlayArrow, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Coding & Dev")
            ),

            // 🔬 ECE & Engineering (20+ tools)
            listOf(
                ToolItem("LTspice", "Circuit simulation", "https://www.analog.com/en/design-center/design-tools-and-calculators/ltspice-simulator.html", Icons.Filled.ElectricalServices, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), "ECE & Engineering"),
                ToolItem("MATLAB Online", "Numerical computing", "https://matlab.mathworks.com", Icons.Filled.Functions, listOf(Color(0xFFDC2626), Color(0xFFB91C1C)), "ECE & Engineering"),
                ToolItem("CircuitVerse", "Digital circuit design", "https://circuitverse.org", Icons.Filled.Memory, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "ECE & Engineering"),
                ToolItem("Falstad Sim", "Circuit simulator", "https://falstad.com/circuit", Icons.Filled.SettingsInputComponent, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "ECE & Engineering"),
                ToolItem("Tinkercad", "3D circuit design", "https://tinkercad.com", Icons.Filled.DesignServices, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "ECE & Engineering"),
                ToolItem("Wokwi", "Arduino simulator", "https://wokwi.com", Icons.Filled.Engineering, listOf(Color(0xFF10B981), Color(0xFF059669)), "ECE & Engineering"),
                ToolItem("EveryCircuit", "Circuit simulator", "https://everycircuit.com", Icons.Filled.ElectricalServices, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "ECE & Engineering"),
                ToolItem("PartSim", "Circuit simulation", "https://partsim.com", Icons.Filled.Memory, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "ECE & Engineering"),
                ToolItem("CircuitLab", "Online circuit designer", "https://circuitlab.com", Icons.Filled.Build, listOf(Color(0xFF10B981), Color(0xFF059669)), "ECE & Engineering"),
                ToolItem("EasyEDA", "PCB design tool", "https://easyeda.com", Icons.Filled.Dashboard, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "ECE & Engineering"),
                ToolItem("KiCad", "PCB design", "https://kicad.org", Icons.Filled.Memory, listOf(Color(0xFF14B8A6), Color(0xFF0D9488)), "ECE & Engineering"),
                ToolItem("Fritzing", "PCB prototyping", "https://fritzing.org", Icons.Filled.Build, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "ECE & Engineering"),
                ToolItem("Octave Online", "MATLAB alternative", "https://octave-online.net", Icons.Filled.Calculate, listOf(Color(0xFF1F2937), Color(0xFF111827)), "ECE & Engineering"),
                ToolItem("Desmos", "Graphing calculator", "https://desmos.com", Icons.Filled.TrendingUp, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "ECE & Engineering"),
                ToolItem("GeoGebra", "Math visualization", "https://geogebra.org", Icons.Filled.Functions, listOf(Color(0xFF10B981), Color(0xFF059669)), "ECE & Engineering"),
                ToolItem("Wolfram Cloud", "Computational intelligence", "https://wolframcloud.com", Icons.Filled.Cloud, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "ECE & Engineering"),
                ToolItem("Simulink Online", "Model-based design", "https://matlab.mathworks.com", Icons.Filled.Timeline, listOf(Color(0xFFDC2626), Color(0xFFB91C1C)), "ECE & Engineering"),
                ToolItem("National Instruments", "Engineering software", "https://ni.com", Icons.Filled.PrecisionManufacturing, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "ECE & Engineering"),
                ToolItem("Arduino IDE Web", "Arduino programming", "https://create.arduino.cc/editor", Icons.Filled.Code, listOf(Color(0xFF10B981), Color(0xFF059669)), "ECE & Engineering"),
                ToolItem("Raspberry Pi", "Single-board computers", "https://raspberrypi.org", Icons.Filled.Computer, listOf(Color(0xFFDC2626), Color(0xFFB91C1C)), "ECE & Engineering")
            ),

            // 📚 Study & Productivity (20+ tools)
            listOf(
                ToolItem("Notion", "All-in-one workspace", "https://notion.so", Icons.Filled.Dashboard, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Study & Productivity"),
                ToolItem("Google Drive", "Cloud storage", "https://drive.google.com", Icons.Filled.Cloud, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Study & Productivity"),
                ToolItem("Miro", "Online whiteboard", "https://miro.com", Icons.Filled.Draw, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "Study & Productivity"),
                ToolItem("Trello", "Project management", "https://trello.com", Icons.Filled.ViewKanban, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Study & Productivity"),
                ToolItem("Wolfram Alpha", "Computational intelligence", "https://wolframalpha.com", Icons.Filled.Calculate, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "Study & Productivity"),
                ToolItem("Khan Academy", "Free online courses", "https://khanacademy.org", Icons.Filled.School, listOf(Color(0xFF14B8A6), Color(0xFF0D9488)), "Study & Productivity"),
                ToolItem("Coursera", "Online courses", "https://coursera.org", Icons.AutoMirrored.Filled.MenuBook, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Study & Productivity"),
                ToolItem("edX", "University courses", "https://edx.org", Icons.Filled.School, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Study & Productivity"),
                ToolItem("Udemy", "Online learning", "https://udemy.com", Icons.Filled.PlayCircle, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "Study & Productivity"),
                ToolItem("Quizlet", "Flashcards & learning", "https://quizlet.com", Icons.Filled.FlashOn, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Study & Productivity"),
                ToolItem("Anki", "Spaced repetition", "https://apps.ankiweb.net", Icons.Filled.Schedule, listOf(Color(0xFF1F2937), Color(0xFF111827)), "Study & Productivity"),
                ToolItem("Google Scholar", "Academic research", "https://scholar.google.com", Icons.Filled.School, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Study & Productivity"),
                ToolItem("Zotero", "Reference manager", "https://zotero.org", Icons.Filled.LibraryBooks, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "Study & Productivity"),
                ToolItem("Mendeley", "Research management", "https://mendeley.com", Icons.AutoMirrored.Filled.MenuBook, listOf(Color(0xFF10B981), Color(0xFF059669)), "Study & Productivity"),
                ToolItem("Grammarly", "Writing assistant", "https://grammarly.com", Icons.Filled.Edit, listOf(Color(0xFF14B8A6), Color(0xFF0D9488)), "Study & Productivity"),
                ToolItem("Hemingway Editor", "Writing improvement", "https://hemingwayapp.com", Icons.Filled.FormatQuote, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "Study & Productivity"),
                ToolItem("Google Keep", "Note taking", "https://keep.google.com", Icons.Filled.Note, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "Study & Productivity"),
                ToolItem("Evernote", "Note organization", "https://evernote.com", Icons.Filled.NoteAdd, listOf(Color(0xFF10B981), Color(0xFF059669)), "Study & Productivity"),
                ToolItem("OneNote Online", "Digital notebook", "https://onenote.com", Icons.AutoMirrored.Filled.MenuBook, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "Study & Productivity"),
                ToolItem("Focus@Will", "Productivity music", "https://focusatwill.com", Icons.Filled.MusicNote, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "Study & Productivity")
            ),

            // 🛠️ DevOps & APIs (15+ tools)
            listOf(
                ToolItem("Postman", "API development", "https://postman.com", Icons.Filled.Api, listOf(Color(0xFFFF6B35), Color(0xFFF7931E)), "DevOps & APIs"),
                ToolItem("Docker Hub", "Container registry", "https://hub.docker.com", Icons.Filled.Storage, listOf(Color(0xFF0EA5E9), Color(0xFF0369A1)), "DevOps & APIs"),
                ToolItem("Kubernetes Docs", "Container orchestration", "https://kubernetes.io", Icons.Filled.CloudQueue, listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)), "DevOps & APIs"),
                ToolItem("Swagger UI", "API documentation", "https://swagger.io/tools/swagger-ui", Icons.Filled.Description, listOf(Color(0xFF10B981), Color(0xFF059669)), "DevOps & APIs"),
                ToolItem("JSON Formatter", "JSON validator & formatter", "https://jsonformatter.org", Icons.Filled.DataObject, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "DevOps & APIs"),
                ToolItem("JSONLint", "JSON validator", "https://jsonlint.com", Icons.Filled.CheckCircle, listOf(Color(0xFF10B981), Color(0xFF059669)), "DevOps & APIs"),
                ToolItem("Base64 Encode", "Base64 encoding", "https://base64encode.org", Icons.Filled.Code, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "DevOps & APIs"),
                ToolItem("RegExr", "Regular expression tool", "https://regexr.com", Icons.Filled.FilterAlt, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "DevOps & APIs"),
                ToolItem("JSON to Dart", "JSON conversion", "https://javiercbk.github.io/json_to_dart", Icons.Filled.SwapHoriz, listOf(Color(0xFF14B8A6), Color(0xFF0D9488)), "DevOps & APIs"),
                ToolItem("QuickType", "JSON to code", "https://quicktype.io", Icons.Filled.AutoFixHigh, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "DevOps & APIs"),
                ToolItem("Mockaroo", "Mock data generator", "https://mockaroo.com", Icons.Filled.DataArray, listOf(Color(0xFF10B981), Color(0xFF059669)), "DevOps & APIs"),
                ToolItem("JSONPlaceholder", "Fake API", "https://jsonplaceholder.typicode.com", Icons.Filled.Cloud, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "DevOps & APIs"),
                ToolItem("HTTPie", "API testing", "https://httpie.io", Icons.Filled.Http, listOf(Color(0xFFEC4899), Color(0xFFBE185D)), "DevOps & APIs"),
                ToolItem("Insomnia", "API client", "https://insomnia.rest", Icons.Filled.Nightlight, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), "DevOps & APIs"),
                ToolItem("Pingdom", "Website monitoring", "https://pingdom.com", Icons.Filled.Speed, listOf(Color(0xFF10B981), Color(0xFF059669)), "DevOps & APIs")
            )
        ).flatten() + customTools
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Study Tools Hub",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
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
                    // Add Custom Tool Button
                    IconButton(
                        onClick = { showAddToolDialog = true }
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add Custom Tool",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF020617)
                )
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF020617),
                            Color(0xFF0F172A),
                            Color(0xFF1E293B)
                        )
                    )
                )
                .padding(padding)
        ) {
            // Header
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    "${toolsByCategory.size}+ Tools Available",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Essential tools for students & developers • Tap + to add your own",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Tools Grid
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                toolsByCategory.groupBy { it.category }.forEach { (category, tools) ->
                    item {
                        Text(
                            "$category • ${tools.size} tools",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(tools.chunked(2)) { rowTools ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowTools.forEach { tool ->
                                ToolCard(
                                    tool = tool,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tool.url))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                            // Add empty space if row has only one item
                            if (rowTools.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // Add Custom Tool Dialog
    if (showAddToolDialog) {
        AddToolDialog(
            onDismiss = { showAddToolDialog = false },
            onAddTool = { newTool ->
                customTools.add(newTool)
                showAddToolDialog = false
            }
        )
    }
}

@Composable
fun ToolCard(
    tool: ToolItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = tool.gradient
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.name,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        text = tool.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                    Text(
                        text = tool.description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Shine effect overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToolDialog(
    onDismiss: () -> Unit,
    onAddTool: (ToolItem) -> Unit
) {
    var toolName by remember { mutableStateOf("") }
    var toolDescription by remember { mutableStateOf("") }
    var toolUrl by remember { mutableStateOf("https://") }
    var toolCategory by remember { mutableStateOf("Custom") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Tool", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = toolName,
                    onValueChange = { toolName = it },
                    label = { Text("Tool Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF38BDF8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF38BDF8),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = toolDescription,
                    onValueChange = { toolDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF38BDF8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF38BDF8),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = toolUrl,
                    onValueChange = { toolUrl = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF38BDF8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF38BDF8),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = toolCategory,
                    onValueChange = { toolCategory = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF38BDF8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF38BDF8),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (toolName.isNotBlank() && toolUrl.isNotBlank()) {
                        val newTool = ToolItem(
                            name = toolName,
                            description = toolDescription,
                            url = toolUrl,
                            icon = Icons.Filled.Link,
                            gradient = listOf(Color(0xFF6B7280), Color(0xFF374151)),
                            category = toolCategory
                        )
                        onAddTool(newTool)
                    }
                }
            ) {
                Text("Add Tool", color = Color(0xFF38BDF8))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = Color(0xFF1E293B)
    )
}