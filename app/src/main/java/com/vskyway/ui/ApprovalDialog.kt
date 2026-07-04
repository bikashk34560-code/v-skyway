package com.vskyway.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vskyway.ui.theme.*

// Data class to hold the blocked action's details
data class PendingAction(
    val targetApp: String, // e.g., "WhatsApp" or "Termux"
    val actionType: String, // e.g., "SEND_MESSAGE" or "DELETE_FILE"
    val description: String // e.g., "Sending 'Hello' to Aman Verma"
)

@Composable
fun ApprovalDialog(
    action: PendingAction,
    onAllowOnce: () -> Unit,
    onAlwaysAllow: () -> Unit,
    onDeny: () -> Unit,
    onDismiss: () -> Unit // If user wants to minimize this and chat for clarification
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Action Approval Required",
                color = AccentWarning, // Orange warning color
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text("AI is attempting to perform an action:", color = TextPrimary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                // Action Details
                Surface(
                    color = BackgroundWhite,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Target: ${action.targetApp}", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 14.sp)
                        Text("Action: ${action.actionType}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Details: ${action.description}", color = TextSecondary, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "You can deny this, or ask AI for clarification in the chat before deciding.", 
                    color = TextPrimary, 
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Allow Once Button
                Button(
                    onClick = onAllowOnce,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Allow Once", color = Color.White)
                }
                
                // Always Allow (Saves to Room DB Rule)
                Button(
                    onClick = onAlwaysAllow,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Green
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Always Allow (Save Rule)", color = Color.White)
                }
            }
        },
        dismissButton = {
            // Deny Button
            OutlinedButton(
                onClick = onDeny,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deny Action", color = AccentWarning)
            }
        }
    )
}