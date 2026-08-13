package com.example.data.model

data class AssistantChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val imageUri: String? = null,
    val isSpeechPlaying: Boolean = false,
    val oshaReference: String? = null,
    val confidenceScore: Int? = null,
    val actionItems: List<String> = emptyList()
)

data class SiteContextMemory(
    val location: String = "Level 18 Deck • Grid B-4",
    val activeCrew: String = "Concrete Pour Crew #3 (14 workers)",
    val weatherRisk: String = "High Wind Warning (28 kt)",
    val activeHazards: String = "2 Open Risk Infractions",
    val activeBimModel: String = "BIM Struct S-204 Rev C",
    val activeProject: String = "Metro Tower Construction • Site #04"
)

data class AssistantState(
    val messages: List<AssistantChatMessage> = emptyList(),
    val isListening: Boolean = false,
    val isThinking: Boolean = false,
    val isSpeaking: Boolean = false,
    val contextMemory: SiteContextMemory = SiteContextMemory(),
    val attachedImageUri: String? = null,
    val attachedImageName: String? = null,
    val activeSpeechPlayingId: String? = null,
    val isHandsFreeEnabled: Boolean = true
)
