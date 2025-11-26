package com.runanywhere.classconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.runanywhere.classconnect.ui.chat.ChatScreen
import com.runanywhere.classconnect.ui.dashboard.DashboardScreen
import com.runanywhere.classconnect.ui.groups.GroupDashboard
import com.runanywhere.classconnect.ui.login.LoginScreen
import com.runanywhere.classconnect.ui.matchmaking.MatchmakingScreen
import com.runanywhere.classconnect.ui.profile.ProfileScreen
import com.runanywhere.classconnect.ui.profile.ProfileSetupScreen
import com.runanywhere.classconnect.ui.theme.Startup_hackathon20Theme
import com.runanywhere.classconnect.ui.timeline.AssignmentTimelineScreen
import com.runanywhere.classconnect.ui.workspace.TeamWorkspace
import com.runanywhere.classconnect.util.SessionManager
import com.runanywhere.classconnect.viewmodels.ChatViewModel
import com.runanywhere.classconnect.ui.focus.FocusModeActivityWrapper
import com.runanywhere.classconnect.ui.tools.ToolsHubScreen
import com.runanywhere.classconnect.ui.focus.FocusLeaderboardScreen
import com.runanywhere.classconnect.ui.courses.CoursesScreen
import com.runanywhere.classconnect.ui.resume.ResumeBuilderScreen
import com.runanywhere.classconnect.ui.resume.ResumeReviewScreen
import com.runanywhere.classconnect.ui.jobs.JobPortalScreen



import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚡ Just edge-to-edge + UI content, no experimental Foundation flags
        enableEdgeToEdge()

        setContent {
            Startup_hackathon20Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val chatViewModel: ChatViewModel = viewModel()

    // Determine correct start destination
    val isCompleted = try {
        runBlocking { sessionManager.isProfileCompleted() } ?: false
    } catch (e: Exception) {
        false
    }

    val startDestination = if (isCompleted) "dashboard" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(navController, sessionManager)
        }

        composable("profileSetup") {
            ProfileSetupScreen(navController, sessionManager)
        }

        composable("profile") {
            ProfileScreen(navController, sessionManager)
        }

        composable("focusLeaderboard") {
            FocusLeaderboardScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(
                navController = navController,
                viewModel = chatViewModel,
                sessionManager = sessionManager
            )
        }
        composable("courses") {
            CoursesScreen(navController)
        }
        composable("resumeBuilder") {
            ResumeBuilderScreen(navController)
        }

        composable("resumeReview") {
            ResumeReviewScreen(navController)
        }

        composable("jobPortal") {
            JobPortalScreen(navController)
        }

        composable("chat") {
            ChatScreen(navController, chatViewModel)
        }

        composable("focus") {
            FocusModeActivityWrapper(navController)
        }

        composable("timeline") {
            AssignmentTimelineScreen(navController, chatViewModel)
        }

        composable("matchmaking") {
            MatchmakingScreen(navController)
        }

        composable("toolsHub") {
            ToolsHubScreen(navController)
        }

        composable("groups") {
            GroupDashboard(navController)
        }

        composable("workspace") {
            TeamWorkspace(navController)
        }
    }
}
