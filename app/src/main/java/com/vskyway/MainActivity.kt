package com.vskyway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vskyway.security.SystemPrivilegeHelper
import com.vskyway.ui.DashboardScreen
import com.vskyway.ui.state.WorkspaceViewModel
import com.vskyway.ui.theme.VSkywayTheme

class MainActivity : ComponentActivity() {

    // ViewModels() delegate automatically lifecycle handle karta hai
    private val workspaceViewModel: WorkspaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide ActionBar for full screen Compose experience
        actionBar?.hide()

        // Silent accessibility enable — kaam karega sirf tab jab
        // WRITE_SECURE_SETTINGS pehle adb se grant kiya ho, warna no-op
        SystemPrivilegeHelper.trySilentEnableAccessibility(this)

        setContent {
            VSkywayTheme {
                // UI aur ViewModel ki wiring yahan ho rahi hai
                DashboardScreen(viewModel = workspaceViewModel)
            }
        }
    }
}
