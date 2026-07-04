package com.vskyway.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WhatsAppPanel(modifier: Modifier = Modifier) {
    val recentChats = listOf(
        Pair("Aman Verma", "Bhai, project ka demo dekh liya..."),
        Pair("Neha Singh", "Theek hai, main docs update kar..."),
        Pair("Dev Team", "Rohit: Build successful \u2705"),
        Pair("Skyway AI Group", "You: New update pushed \uD83D\uDD25")
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(recentChats) { chat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture Placeholder
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5E81AC))
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(text = chat.first, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = chat.second, color = Color.Gray, fontSize = 10.sp, maxLines = 1)
                }
            }
        }
    }
}