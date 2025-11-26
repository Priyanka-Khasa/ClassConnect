package com.runanywhere.classconnect.ui.focus

import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FocusMonitorService : Service() {

    private val distractingApps = listOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.snapchat.android",
        "com.facebook.katana",
        "com.twitter.android",
        "com.whatsapp"
    )

    // Coroutine scope for background monitoring
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // To avoid spamming overlay
    private var lastDetectedApp: String? = null
    private var lastOverlayTime: Long = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startInForeground()

        // Already running? Don't start multiple loops
        serviceScope.launch {
            while (isActive) {
                if (hasUsageAccess()) {
                    try {
                        checkRunningApp()
                    } catch (e: Exception) {
                        Log.e("FocusMonitorService", "Error checking running app", e)
                    }
                } else {
                    Log.w("FocusMonitorService", "Usage access not granted")
                    // Optional: open settings screen once
                    openUsageSettingsOnce()
                }
                delay(3000L) // check every 3 seconds
            }
        }

        return START_STICKY
    }

    private fun startInForeground() {
        val notification = buildNotification()
        // On Android O+ we must call startForeground quickly
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus mode monitoring")
            .setContentText("Tracking distracting apps to help you stay focused.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for focus monitoring foreground service"
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun hasUsageAccess(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private var openedUsageSettingsOnce = false

    private fun openUsageSettingsOnce() {
        if (openedUsageSettingsOnce) return
        openedUsageSettingsOnce = true
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("FocusMonitorService", "Failed to open usage access settings", e)
        }
    }

    private fun checkRunningApp() {
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10_000L

        val events = usm.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()

        var currentApp: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(event)

            // Prefer ACTIVITY_RESUMED on newer Android, but also keep MOVE_TO_FOREGROUND
            val isForegroundEvent =
                event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                        event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND

            if (isForegroundEvent) {
                currentApp = event.packageName
            }
        }

        if (currentApp != null && distractingApps.contains(currentApp)) {
            maybeShowOverlay(currentApp)
        }
    }

    private fun maybeShowOverlay(currentApp: String) {
        val now = System.currentTimeMillis()

        // Debounce: don't show overlay again for same app within 20 seconds
        if (currentApp == lastDetectedApp && now - lastOverlayTime < 20_000L) {
            return
        }

        lastDetectedApp = currentApp
        lastOverlayTime = now

        Log.d("FocusMonitorService", "Detected distracting app: $currentApp")

        val intent = Intent(this, ReminderOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("distractingApp", currentApp)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("FocusMonitorService", "Failed to start overlay activity", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "focus_monitor_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
