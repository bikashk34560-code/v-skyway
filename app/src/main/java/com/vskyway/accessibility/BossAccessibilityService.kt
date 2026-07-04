package com.vskyway.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BossAccessibilityService : AccessibilityService() {

    companion object {
        // Singleton instance taaki ViewModel (WorkspaceViewModel) ise direct command de sake
        var instance: BossAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("SkywayLayerA", "Layer A (Accessibility) Connected & Ready")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Passive monitoring. Hum mostly active polling use karenge jab AI demand karega.
    }

    override fun onInterrupt() {
        Log.e("SkywayLayerA", "Layer A Interrupted")
        instance = null
    }

    // ==========================================
    // 1. SCREEN PERCEPTION (Reading the UI Tree)
    // ==========================================
    
    /**
     * AI ke padhne ke liye active window ko parse karta hai.
     */
    fun extractScreenForAI(): String {
        val rootNode = rootInActiveWindow ?: return "ERROR: No active window found. Screen might be locked or secure."
        val uiElements = mutableListOf<String>()
        traverseNodeTree(rootNode, uiElements)
        return uiElements.joinToString("\n")
    }

    private fun traverseNodeTree(node: AccessibilityNodeInfo?, list: MutableList<String>) {
        if (node == null) return

        // Sirf visible aur useful nodes filter karna taaki AI tokens waste na hon
        if (node.isVisibleToUser && (node.text != null || node.contentDescription != null || node.isClickable || node.isEditable)) {
            val rect = Rect()
            node.getBoundsInScreen(rect)
            
            val text = node.text?.toString()?.replace("\n", " ") ?: "null"
            val desc = node.contentDescription?.toString()?.replace("\n", " ") ?: "null"
            
            // Format: [NodeHash] Type:Button Text:'...' Desc:'...' Clickable:true Bounds:[x1,y1,x2,y2]
            val elementInfo = "[ID:${node.hashCode()}] Class:${node.className?.substringAfterLast(".")} | Text:'$text' | Desc:'$desc' | Clickable:${node.isClickable} | Editable:${node.isEditable} | Bounds:[${rect.left},${rect.top},${rect.right},${rect.bottom}]"
            
            list.add(elementInfo)
        }

        for (i in 0 until node.childCount) {
            traverseNodeTree(node.getChild(i), list)
        }
    }

    // ==========================================
    // 2. ACTION EXECUTION (Controlling the UI)
    // ==========================================

    /**
     * AI dwara diye gaye Node Hash ID par click karna.
     */
    fun clickByNodeId(targetHash: Int): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val targetNode = findNodeByHash(rootNode, targetHash)
        
        return if (targetNode?.isClickable == true) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            // Agar element khud clickable nahi hai, toh parent mein click dhoondho (e.g., WhatsApp message bubble)
            var parent = targetNode?.parent
            var clicked = false
            while (parent != null) {
                if (parent.isClickable) {
                    clicked = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    break
                }
                parent = parent.parent
            }
            clicked
        }
    }

    /**
     * Kisi input field mein AI ka text type karna.
     */
    fun typeTextInNode(targetHash: Int, textToType: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val targetNode = findNodeByHash(rootNode, targetHash)
        
        if (targetNode != null && (targetNode.isEditable || targetNode.className?.contains("EditText") == true)) {
            val arguments = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, textToType)
            }
            return targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }
        return false
    }

    /**
     * Agar UI automation fail ho jaye, toh X,Y coordinate par exact physical tap simulate karna.
     */
    fun clickByCoordinates(x: Float, y: Float): Boolean {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100)) // 100ms tap duration
            .build()
        return dispatchGesture(gesture, null, null)
    }

    // Helper function: Node tree mein ID dhoondhna
    private fun findNodeByHash(node: AccessibilityNodeInfo?, hash: Int): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.hashCode() == hash) return node
        
        for (i in 0 until node.childCount) {
            val found = findNodeByHash(node.getChild(i), hash)
            if (found != null) return found
        }
        return null
    }
}