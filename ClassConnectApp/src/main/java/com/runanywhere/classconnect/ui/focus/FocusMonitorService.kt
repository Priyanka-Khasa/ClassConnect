package com.runanywhere.classconnect.ui.focus

import android.app.Service
import android.content.Intent
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.os.IBinder
import android.util.Log

class FocusMonitorService : Service() {

    private val distractingApps = listOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.snapchat.android",
        "com.facebook.katana",
        "com.twitter.android",
        "com.whatsapp"
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            while (true) {
                checkRunningApp()
                Thread.sleep(3000)
            }
        }.start()
        return START_STICKY
    }

    private fun checkRunningApp() {
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        val events = usm.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()

        var currentApp = ""
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND)
                currentApp = event.packageName
        }

        if (distractingApps.contains(currentApp)) {
            Log.d("FocusMonitor", "Detected distracting app: $currentApp")
            val intent = Intent(this, ReminderOverlayActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("distractingApp", currentApp)
            startActivity(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
