# ClassConnect – AI-Enhanced Student Collaboration Platform (2025 Edition)

ClassConnect is a modern Android application designed to enhance student productivity, collaboration, and learning efficiency.  
Built using Jetpack Compose, MVVM, Kotlin Coroutines, and the RunAnywhere on-device AI SDK, it brings together intelligent tools for focused study, real-time communication, and academic management in a single platform.

---

## Project Demo  
https://drive.google.com/file/d/1M3x0XT4WMTNnC1SaD1DtTrfV4OiB3vVF/view?usp=sharing

---

# Key Features

## 1. Tools Hub (New – 2025)
A dedicated hub featuring 30+ essential tools across multiple domains.

### Categories Include
- **Artificial Intelligence:** ChatGPT, Gemini, Claude, RunAnywhere AI  
- **Coding:** LeetCode, GeeksForGeeks, HackerRank  
- **ECE Tools:** MATLAB, LTspice, KiCad, Proteus  
- **DevOps/Cloud:** Docker, Kubernetes, GitHub, AWS  

Each tool opens in the browser with a single tap.

---

## 2. Real-Time Group Collaboration  
Create, join, and manage responsive study groups.

## 3. Smart Task Dashboard  
Track deadlines, assignments, progress, and submissions.

## 4. Deep Focus Mode  
A distraction-free study system with:
- Focus Timer  
- Productivity Score  
- Leaderboard  

## 5. Intelligent Reminders  
Automated notifications for all important events.

## 6. Real-Time Chat  
Fast and secure messaging for groups and peers.

## 7. Personalized Profiles  
Profile setup, preferences, theme selection, and streak tracking.

## 8. Resume Builder & Review Portal
- Create resume  
- Edit sections  
- AI-assisted review  
- Export to PDF  

## 9. Courses Explorer  
Modern course cards, enroll actions, and a mock search bar.

## 10. Job & Internship Portal  
Latest internships, jobs, and off-campus opportunities.

---

# Architecture Overview



ClassConnectApp (Initialization)
↓
SessionManager (Preferences & Login State)
↓
ViewModels (Dashboard, Chat, Focus, Resume, Tools)
↓
Composable Screens (UI Layer)


### Core Architecture Principles
- MVVM  
- Repository Pattern  
- Navigation Graph  
- Kotlin Coroutines + Flows  
- RunAnywhere On-Device AI  

---

# Technical Stack

### Languages & Frameworks
- Kotlin  
- Jetpack Compose  
- Material 3  
- Coroutines  
- Retrofit / Ktor  
- RunAnywhere AI SDK  

### Storage
- Room Database (local)  
- Firebase Auth (optional)  
- DataStore Preferences  

### Build Tools
- Gradle  
- Kotlinx Serialization  
- Coil for image loading  

---

# Installation

## 1. Clone the Repository
```bash
git clone https://github.com/Priyanka-Khasa/ClassConnect.git

2. Open in Android Studio

(Android Studio 2023.1 or newer)

3. Build & Run
./gradlew assembleDebug


Or simply click Run.

Project Structure
Folder / File	Purpose
app/	Main Android module
ui/	Jetpack Compose screens
ui/tools/	Tools Hub UI
viewmodels/	MVVM ViewModels
data/	Local/Remote data
SessionManager.kt	Auth + Preferences
DashboardScreen.kt	Main dashboard
ToolsHubScreen.kt	Tool grid
ResumeBuilderScreen.kt	Resume builder
MainActivity.kt	Navigation host
build.gradle	Dependencies
Troubleshooting
Build Errors

Ensure:

Correct Compose compiler version

JVM target = 17

Then run:

./gradlew clean build

Gradle Sync Issues

Delete: .gradle and build/
Reopen project.

Login/Data Issues

Check Firebase config (if used) and network connectivity.

Future Enhancements (Roadmap 2025)

AI-driven Focus Analytics

Auto Study Plan Generation

ML-based Reminder System

College ERP Integration

Cloud Sync

XP/Badges Leaderboard

Resume Scoring AI

Author

Priyanka Khasa
B.Tech – Electronics & Communication Engineering
Android Developer | AI Innovator | Open Source Contributor
Haryana, India

GitHub: Priyanka-Khasa
LinkedIn: add your link here

License

This project is licensed under the MIT License and follows RunAnywhere SDK compatibility.

