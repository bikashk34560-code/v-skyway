package com.vskyway.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vskyway.ui.state.WorkspaceViewModel
import com.vskyway.ui.theme.*

@Composable
fun DashboardScreen(viewModel: WorkspaceViewModel = viewModel()) {
    // Observing states concurrently from ViewModel
    val chatMessages by viewModel.chatMessages.collectAsState()
    val terminalText by viewModel.terminalOutput.collectAsState()
    val webState by viewModel.webAgentState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite) // Bright White background
            .padding(16.dp)
    ) {
        // App Header
        TopHeader()
        
        Spacer(modifier = Modifier.height(16.dp))

        // Top Row (2 Panels)
        Row(modifier = Modifier.weight(1f)) {
            // Top Left: Web Agent Panel
            ModuleCard(title = "WEB AGENT", modifier = Modifier.weight(1f)) {
                WebAgentPanel(state = webState)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Top Right: Ubuntu Sandbox Panel (Dark)
            ModuleCard(title = "UBUNTU SANDBOX", isDark = true, modifier = Modifier.weight(1f)) {
                TerminalPanel(
                    output = terminalText,
                    onInterrupt = { viewModel.appendTerminalOutput("\n[SYSTEM] Interrupt signal sent.") }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bottom Row (2 Panels)
        Row(modifier = Modifier.weight(1f)) {
            // Bottom Left: AI Chat Panel
            ModuleCard(title = "AI CHAT", modifier = Modifier.weight(1f)) {
                ChatPanel(
                    messages = chatMessages,
                    onSend = { prompt -> viewModel.addUserMessage(prompt) }
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Bottom Right: WhatsApp / UI Control Target
            ModuleCard(title = "WHATSAPP (LAYER A)", modifier = Modifier.weight(1f)) {
                WhatsAppPanel()
            }
        }
    }
}

@Composable
fun TopHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SKYWAY AI", 
            color = TextPrimary, 
            fontSize = 24.sp, 
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Autonomous AI Operating Assistant", 
            color = TextSecondary, 
            fontSize = 14.sp
        )
    }
}

@Composable
fun ModuleCard(
    title: String, 
    modifier: Modifier = Modifier, 
    isDark: Boolean = false,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) TerminalBackground else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title, 
                color = if (isDark) TerminalGreenText else PrimaryBlue, 
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Inject the specific panel content
            content()
        }
    }
}

