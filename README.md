# ClassConnect - Intelligent Student Collaboration Platform

**ClassConnect** is a modern Android application designed to enhance classroom productivity, collaboration, and focus.  
It provides students and teachers a unified space for managing study groups, tracking progress, and improving learning efficiency through intelligent insights.

---

## Overview

This project demonstrates the complete implementation of a real-time, AI-enhanced collaboration platform using Jetpack Compose and modern Android architecture components.  
It focuses on simplifying academic communication, automating reminders, and promoting focused learning sessions.

---

## Features

- **Group Management:** Create and manage study groups efficiently.  
- **Task Dashboard:** Track assignments, progress, and deadlines in real time.  
- **Focus Mode:** Reduce distractions and monitor study sessions.  
- **Reminder System:** Intelligent notifications for tasks and meetings.  
- **Real-time Communication:** Seamless message exchange among peers.  
- **Data Security:** Local storage and secure authentication for user data.  
- **Modern UI:** Clean Material 3 design with adaptive layout for all screen sizes.

---

## Architecture Overview

MyApplication (App initialization)
↓
SessionManager (Authentication & preferences)
↓
DashboardViewModel / GroupViewModel (state management)
↓
Composable Screens (UI)

markdown
Copy code

- **MVVM Architecture:** Ensures separation of UI and logic.  
- **Jetpack Compose:** Declarative UI components for responsive design.  
- **Kotlin Coroutines:** Handles asynchronous background tasks.  
- **Navigation Component:** Simplifies screen transitions.  
- **Firebase / Local DB (optional):** For user authentication and data storage.

---

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/Priyanka-Khasa/ClassConnect.git
2. Open in Android Studio
Open the project in Android Studio (version 2023.1 or above).

Allow Gradle to sync automatically.

3. Build and Run
bash
Copy code
./gradlew assembleDebug
# or directly click "Run" in Android Studio
Project Structure
Directory / File	Purpose
app/	Main Android application module
ui/	Jetpack Compose UI components
viewmodel/	Application state management
data/	Local and remote data handling
SessionManager.kt	Handles authentication and user preferences
Dashboard.kt	Displays main group and task interface
build.gradle	Dependency and SDK configuration

Technical Stack
Language: Kotlin

Framework: Jetpack Compose, AndroidX

Architecture: MVVM (Model-View-ViewModel)

Database: Room / Firebase Realtime Database

Dependency Injection: Hilt (if configured)

Version Control: Git & GitHub

IDE: Android Studio

Requirements
Android 8.0 (API 26) or higher

At least 200 MB free storage

Internet connection for sync and collaboration features

Troubleshooting
Build Issues
Ensure correct Android SDK and Compose compiler versions.

Clean and rebuild the project using:

bash
Copy code
./gradlew clean build
Gradle Sync Errors
Verify Gradle and Kotlin versions match project configuration.

Delete .gradle and build folders and re-sync.

Login or Data Issues
Check Firebase/Database configuration.

Ensure network connectivity and valid credentials.

Future Enhancements
AI-based focus analytics dashboard

Smart task recommendations

Cloud sync across devices

Integration with college ERP systems

Author
Priyanka Khasa
B.Tech Electronics and Communication Engineering
Developer & Innovator | Haryana, India
LinkedIn | GitHub

License
This project is licensed under the MIT License.
You may freely use, modify, and distribute this software under the stated conditions.


This example app follows the license of the RunAnywhere SDK.
