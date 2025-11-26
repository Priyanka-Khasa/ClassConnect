 ClassConnect – AI-Enhanced Student Collaboration Platform (2025 Edition)

ClassConnect is an intelligent, modular, real-time Android application that redefines how students collaborate, study, and stay organized.
Designed using Jetpack Compose + MVVM + Kotlin Coroutines + RunAnywhere SDK, it powers deep focus sessions, AI assistance, group productivity, and smart academic tools in one seamless platform.

 Project Demo

https://drive.google.com/file/d/1M3x0XT4WMTNnC1SaD1DtTrfV4OiB3vVF/view?usp=sharing

 Key Highlights (Updated)
 1. Tools Hub (NEW – 2025)

A beautiful grid of 30+ latest tools for:

AI (ChatGPT, Gemini, Claude, RunAnywhere AI)

Coding (LeetCode, GFG, HackerRank)

ECE (Proteus, MATLAB, LTspice, KiCad)

DevOps (Docker, Kubernetes)
One tap → opens official website.

 2. Real-Time Group Collaboration

Create and manage study groups with a responsive and modern UI.

3. Smart Task Dashboard

Track deadlines, assignments, submissions, and class schedules.

4. Deep Focus Mode

Distraction-free study sessions with:

Focus timer

Leaderboard

Productivity score

 5. Intelligent Reminders

Notification system for tasks, classes, meetings, and study streaks.

 6. Real-Time Chat

Secure messaging between peers, groups, and study partners.

7. Personalized Profiles

Profile setup, preferences, dark mode, streak tracking.

 8. Resume Builder + Review Portal

A modern tool for students to:

Create resumes

Edit sections

Review via AI

Export PDF

 9. Courses Explorer

Display trending courses, enroll buttons, fake search bar (demo), course cards.

 10. Job Portal

Latest internships, off-campus opportunities, tech roles.

Architecture Overview (Clean & Modern)
ClassConnectApp (App initialization)
     ↓
SessionManager (User identity & preferences)
     ↓
ViewModels (Dashboard, Groups, Chat, Focus, Resume, Tools)
     ↓
Composable Screens (UI Layer)

✔ MVVM Architecture

✔ Jetpack Compose UI
✔ RunAnywhere SDK for on-device AI
✔ Navigation Component
✔ Kotlin Coroutines + Flows
✔ Room / Firebase (optional)

Technical Stack
 Languages & Frameworks

Kotlin

Jetpack Compose

Material 3

Kotlin Coroutines

Retrofit / Ktor Client

RunAnywhere On-Device AI SDK

 Architecture

MVVM

Repository Pattern

Navigation Graph

 Storage

Room Database (local)

Firebase Auth (optional)

DataStore Preferences

 Build Tools

Gradle

Kotlinx Serialization

Coil (Images)

 Installation Guide
 Clone the Repository
git clone https://github.com/Priyanka-Khasa/ClassConnect.git

 Open in Android Studio

Android Studio 2023.1+

Let Gradle sync fully

 Build & Run
./gradlew assembleDebug


Or simply click Run ▶

 Project Structure
Folder / File	Purpose
app/	Main Android module
ui/	Jetpack Compose screens
ui/tools/	🔥 Tools Hub
viewmodels/	All ViewModels
data/	Local/remote data
SessionManager.kt	Login state
DashboardScreen.kt	Main dashboard
ToolsHubScreen.kt	Tools launcher grid
ResumeBuilderScreen.kt	Resume Builder
MainActivity.kt	Navigation Host
build.gradle	Dependencies
 Troubleshooting
 Build Errors

✔ Ensure correct Compose compiler version
✔ Use jvmTarget = 17
✔ Clean & rebuild:

./gradlew clean build

❗ Gradle Sync Issues

Delete .gradle and build/ folders

Re-import project

❗ Login / Data Issues

Check Firebase credentials (if enabled)

Internet connectivity

Rebuild the project

 Future Enhancements (Roadmap 2025)

AI-powered focus analytics

Auto-generated study plans

Smart reminders using ML

ERP Integration for colleges

Cloud sync across devices

Leaderboard with XP & Levels

Advanced resume scoring model

 Author

Priyanka Khasa
B.Tech – Electronics & Communication Engineering
Android Developer | AI Innovator | Open Source Contributor
Haryana, India

🔗 LinkedIn: add link here
🔗 GitHub: Priyanka-Khasa

 License

This project is licensed under the MIT License.
Compatible with RunAnywhere SDK licensing.
