package com.runanywhere.classconnect.ui.focus

data class FocusSession(
    val startTime: Long,
    var endTime: Long? = null,
    var durationSec: Int = 0,
    var distractions: MutableList<String> = mutableListOf()
)