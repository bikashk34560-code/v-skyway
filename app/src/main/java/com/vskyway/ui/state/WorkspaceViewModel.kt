package com.vskyway.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// Data Classes for States
data class ChatMessage(val id: String, val sender: String, val content: String, val isUser: Boolean)
data class WebAgentData(val url: String, val isLoading: Boolean, val lastAction: String = "Idle", val progress: Float = 0f)

class WorkspaceViewModel : ViewModel() {

    // 1. Session Isolation State
    private val _currentSessionId = MutableStateFlow(UUID.randomUUID().toString())
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    // 2. AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // 3. Terminal State (Layer B)
    private val _terminalOutput = MutableStateFlow("$ systemctl start vskyway-agent\nInitialization complete...")
    val terminalOutput: StateFlow<String> = _terminalOutput.asStateFlow()

    // 4. Web Agent State
    private val _webAgentState = MutableStateFlow(WebAgentData(url = "https://github.com", isLoading = false))
    val webAgentState: StateFlow<WebAgentData> = _webAgentState.asStateFlow()

    // --- State Update Functions ---

    fun loadNewSession() {
        _currentSessionId.value = UUID.randomUUID().toString()
        _chatMessages.value = emptyList() // Clear chat for new session
        _terminalOutput.value = "$ " // Reset terminal
    }

    fun loadExistingSession(sessionId: String, previousMessages: List<ChatMessage>) {
        _currentSessionId.value = sessionId
        _chatMessages.value = previousMessages
    }

    fun addUserMessage(content: String) {
        val newMessage = ChatMessage(UUID.randomUUID().toString(), "User", content, true)
        _chatMessages.value = _chatMessages.value + newMessage
        // TODO: Part 9 mein yahan se AI Provider ko API call jayegi
    }

    fun appendTerminalOutput(text: String) {
        _terminalOutput.value = _terminalOutput.value + "\n" + text
    }
}