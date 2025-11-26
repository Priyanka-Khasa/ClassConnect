package com.runanywhere.classconnect.ui.focus

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun FocusModeActivityWrapper(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.startActivity(Intent(context, FocusModeActivity::class.java))
    }
}
