package com.vskyway.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vskyway.ui.state.WebAgentData
import com.vskyway.ui.theme.*

@Composable
fun WebAgentPanel(
    state: WebAgentData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        // URL Layer
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            Text("URL: ", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(state.url, color = PrimaryBlue, fontSize = 12.sp)
        }

        // Live Action Indicator
        Text("Current Action:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(
            text = state.lastAction,
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Loading/Progress State
        if (state.isLoading) {
            Text("Progress", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = PrimaryBlue,
                trackColor = BackgroundWhite
            )
        } else {
            Text("Status: Idle & Waiting", color = TextSecondary, fontSize = 11.sp)
        }
    }
}