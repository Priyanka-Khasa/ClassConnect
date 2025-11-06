package com.runanywhere.classconnect.data

data class AssignmentTask(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val description: String = "",
    val dueAtMillis: Long,
    val isCompleted: Boolean = false
)