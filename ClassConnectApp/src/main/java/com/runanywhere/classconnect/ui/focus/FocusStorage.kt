package com.runanywhere.classconnect.ui.focus

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.focusDataStore by preferencesDataStore("focus_sessions")

object FocusStorage {
    private val KEY_SESSIONS = stringPreferencesKey("sessions")
    private val gson = Gson()

    fun saveSession(context: Context, session: FocusSession) = runBlocking {
        val current = loadSessions(context).toMutableList()
        current.add(session)
        val json = gson.toJson(current)
        context.focusDataStore.edit { prefs ->
            prefs[KEY_SESSIONS] = json
        }
    }

    fun loadSessions(context: Context): List<FocusSession> = runBlocking {
        val json = context.focusDataStore.data
            .map { prefs -> prefs[KEY_SESSIONS] ?: "[]" }
            .first()

        val type = object : TypeToken<List<FocusSession>>() {}.type
        gson.fromJson(json, type)
    }
}
