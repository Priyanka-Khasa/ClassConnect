package com.runanywhere.classconnect.util

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ✅ Create DataStore instance (scoped to Context)
private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {

    companion object {
        // Preference Keys
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val EMAIL = stringPreferencesKey("email")
        private val LOGIN_TIME = longPreferencesKey("login_time")

        // Session expiry (3 days)
        private const val SESSION_DURATION = 3 * 24 * 60 * 60 * 1000L
    }

    /** 🔹 Observe whether the user is logged in (reactive Flow) */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        val loggedIn = prefs[IS_LOGGED_IN] ?: false
        val savedTime = prefs[LOGIN_TIME] ?: 0L
        loggedIn && (System.currentTimeMillis() - savedTime) < SESSION_DURATION
    }

    /** 🔹 Save login state and user email */
    suspend fun saveLoginSession(email: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[EMAIL] = email
            prefs[LOGIN_TIME] = System.currentTimeMillis()
        }
    }

    /** 🔹 Simple login state setter (for demo purposes) */
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

    /** 🔹 Clear all saved session data (logout) */
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    /** 🔹 Get current user email (nullable Flow) */
    val userEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[EMAIL]
    }
}
