package com.vskyway.shell

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

class TermuxBridge(private val context: Context) {

    // Internal workspace directory jahan AI ka saara execution hoga
    private val workspaceDir: File = File(context.filesDir, "skyway_workspace").apply {
        if (!exists()) mkdirs()
    }

    private var activeProcess: Process? = null

    // Live terminal output flow jise UI (TerminalPanel) observe karega
    private val _liveOutput = MutableStateFlow("")
    val liveOutput: StateFlow<String> = _liveOutput.asStateFlow()

    // ==========================================
    // 1. SHELL EXECUTION (The AI Backend)
    // ==========================================

    /**
     * AI dwara diye gaye shell commands ko execute karta hai.
     */
    suspend fun executeCommand(command: String): String = withContext(Dispatchers.IO) {
        try {
            _liveOutput.value += "\n$ $command"
            
            // Setting up the process to run in the isolated workspace
            val processBuilder = ProcessBuilder("sh", "-c", command)
            processBuilder.directory(workspaceDir)
            processBuilder.redirectErrorStream(true) // Combine stdout and stderr

            activeProcess = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(activeProcess?.inputStream))
            val outputBuilder = java.lang.StringBuilder()
            var line: String?

            // Reading output line by line for real-time UI updates
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: ""
                outputBuilder.append(currentLine).append("\n")
                _liveOutput.value += "\n$currentLine"
            }

            activeProcess?.waitFor()
            activeProcess = null

            return@withContext outputBuilder.toString().trim()
        } catch (e: Exception) {
            Log.e("SkywayLayerB", "Command execution failed: ${e.message}")
            _liveOutput.value += "\n[ERROR] ${e.message}"
            return@withContext "Error: ${e.message}"
        }
    }

    /**
     * Human-in-the-loop: Interrupt button (Ctrl+C) logic.
     * Agar script infinite loop mein fas jaye toh process destroy kar dega.
     */
    fun interruptProcess() {
        activeProcess?.let {
            it.destroy()
            _liveOutput.value += "\n[SYSTEM] Process interrupted by User (SIGINT)."
            activeProcess = null
        }
    }

    // ==========================================
    // 2. STRICT MANUAL UPLOAD (The Gatekeeper)
    // ==========================================

    /**
     * Jab Boss UI mein paperclip icon daba kar file select karenge, 
     * sirf tab yeh function us file ko safe workspace mein copy karega.
     */
    suspend fun uploadFileToWorkspace(fileUri: Uri, fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val destinationFile = File(workspaceDir, fileName)
            
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            _liveOutput.value += "\n[SYSTEM] File '$fileName' manually uploaded to workspace."
            return@withContext true
        } catch (e: Exception) {
            Log.e("SkywayLayerB", "File upload failed: ${e.message}")
            return@withContext false
        }
    }
    
    /**
     * Workspace clear karne ka command, taki naya session clean start le sake.
     */
    fun clearWorkspace() {
        workspaceDir.listFiles()?.forEach { it.deleteRecursively() }
        _liveOutput.value += "\n[SYSTEM] Workspace wiped clean."
    }
}