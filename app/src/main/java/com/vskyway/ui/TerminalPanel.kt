package com.vskyway.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // <-- Fixed Import
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vskyway.ui.theme.*

@Composable
fun TerminalPanel(
    output: String,
    onInterrupt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = output,
            color = TerminalGreenText,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp) 
        )

        Button(
            onClick = onInterrupt,
            colors = ButtonDefaults.buttonColors(containerColor = AccentWarning),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .height(32.dp)
        ) {
            Text("Ctrl+C (Stop)", fontSize = 10.sp, color = Color.White)
        }
    }
}