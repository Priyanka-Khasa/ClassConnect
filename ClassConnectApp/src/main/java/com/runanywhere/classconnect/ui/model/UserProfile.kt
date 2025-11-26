package com.runanywhere.classconnect.model

data class UserProfile(
    val name: String = "",
    val department: String = "",
    val year: String = "",
    val college: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val time: String = "",
    val imageUri: String = ""
)
