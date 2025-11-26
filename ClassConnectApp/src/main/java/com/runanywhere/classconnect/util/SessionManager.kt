package com.runanywhere.classconnect.util

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.runanywhere.classconnect.model.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// DataStore Instance
private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {

    companion object {
        // Session keys
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val EMAIL = stringPreferencesKey("email")
        private val LOGIN_TIME = longPreferencesKey("login_time")

        // Profile keys
        private val NAME = stringPreferencesKey("user_name")
        private val DEPARTMENT = stringPreferencesKey("user_department")
        private val YEAR = stringPreferencesKey("user_year")
        private val COLLEGE = stringPreferencesKey("user_college")
        private val BIO = stringPreferencesKey("user_bio")
        private val SKILLS = stringPreferencesKey("user_skills")
        private val TIME = stringPreferencesKey("user_time")
        private val IMAGE_URI = stringPreferencesKey("user_image_uri")

        private const val SESSION_DURATION = 3 * 24 * 60 * 60 * 1000L
    }

    // Observe Login State
    val isLoggedIn = context.dataStore.data.map { prefs ->
        val loggedIn = prefs[IS_LOGGED_IN] ?: false
        val savedTime = prefs[LOGIN_TIME] ?: 0L
        loggedIn && (System.currentTimeMillis() - savedTime) < SESSION_DURATION
    }

    // Save Login
    suspend fun saveLoginSession(email: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[EMAIL] = email
            prefs[LOGIN_TIME] = System.currentTimeMillis()
        }
    }

    suspend fun setLoginState(loggedIn: Boolean, email: String = "user@example.com") {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = loggedIn
            if (loggedIn) {
                prefs[EMAIL] = email
                prefs[LOGIN_TIME] = System.currentTimeMillis()
            } else {
                prefs.remove(EMAIL)
                prefs.remove(LOGIN_TIME)
            }
        }
    }

    // Logout
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    // Email flow
    val userEmail = context.dataStore.data.map { prefs -> prefs[EMAIL] }

    // -------------------- USER PROFILE --------------------

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[NAME] = profile.name
            prefs[DEPARTMENT] = profile.department
            prefs[YEAR] = profile.year
            prefs[COLLEGE] = profile.college
            prefs[BIO] = profile.bio
            prefs[SKILLS] = profile.skills.joinToString(",")
            prefs[TIME] = profile.time
            prefs[IMAGE_URI] = profile.imageUri
        }
    }

    val userProfile = context.dataStore.data.map { prefs ->
        val name = prefs[NAME] ?: ""
        val department = prefs[DEPARTMENT] ?: ""

        if (name.isEmpty() && department.isEmpty()) return@map null

        UserProfile(
            name = name,
            department = department,
            year = prefs[YEAR] ?: "",
            college = prefs[COLLEGE] ?: "",
            bio = prefs[BIO] ?: "",
            skills = (prefs[SKILLS] ?: "").split(",").filter { it.isNotBlank() },
            time = prefs[TIME] ?: "",
            imageUri = prefs[IMAGE_URI] ?: ""
        )
    }

    // ⭐ FIX: THIS MUST BE INSIDE THE CLASS
    suspend fun isProfileCompleted(): Boolean {
        return context.dataStore.data.map { prefs ->
            val name = prefs[NAME] ?: ""
            val department = prefs[DEPARTMENT] ?: ""
            name.isNotEmpty() && department.isNotEmpty()
        }.first()
    }
}

