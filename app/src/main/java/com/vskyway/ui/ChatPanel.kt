package com.vskyway.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vskyway.ui.state.ChatMessage
import com.vskyway.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPanel(
    messages: List<ChatMessage>,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = modifier.fillMaxSize()) {
        // Chat List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
        }

        // Input Area with Strict Manual Upload
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment Button (Strict Manual Input)
            IconButton(onClick = { /* TODO: Trigger File/Image Picker */ }) {
                Icon(Icons.Default.Add, contentDescription = "Attach File", tint = TextSecondary)
            }
            
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Command your agent...", fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = SurfaceWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PrimaryBlue
                )
            )
            
            IconButton(
                onClick = { 
                    if (inputText.text.isNotBlank()) {
                        onSend(inputText.text)
                        inputText = TextFieldValue("")
                    }
                },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(PrimaryBlue, CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val backgroundColor = if (isUser) PrimaryBlue else SurfaceWhite
    val textColor = if (isUser) Color.White else TextPrimary
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Surface(
            color = backgroundColor,
            shape = shape,
            shadowElevation = if (isUser) 0.dp else 1.dp,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Text(
                text = message.content,
                color = textColor,
                modifier = Modifier.padding(12.dp),
                fontSize = 13.sp
            )
        }
    }
}