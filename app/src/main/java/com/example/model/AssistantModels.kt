package com.example.model

import java.util.UUID

enum class AssistantLanguage(val code: String, val displayName: String, val nativeName: String, val speechLocale: String) {
    ENGLISH("en", "English", "English", "en-US"),
    BENGALI("bn", "Bengali", "বাংলা", "bn-BD"),
    HINDI("hi", "Hindi", "हिंदी", "hi-IN")
}

enum class SassinessLevel(val displayName: String, val description: String) {
    PLAYFUL("Playful Sweetheart", "Friendly, warm teasing, cute compliments"),
    SASSY("Witty & Sassy", "Bold one-liners, clever retorts, high energy"),
    SPICY("Spicy Diva", "Maximum sass, fiercely protective of Siraj, sarcastic wit")
}

sealed interface AssistantIntent {
    data object None : AssistantIntent
    data object OriginQuestion : AssistantIntent
    data class SendWhatsApp(val phoneNumber: String = "", val message: String = "") : AssistantIntent
    data class SendMessenger(val message: String = "") : AssistantIntent
    data class SendImo(val message: String = "") : AssistantIntent
    data class MakeCall(val phoneNumber: String = "") : AssistantIntent
    data class StreamYouTube(val query: String = "") : AssistantIntent
    data class GenerateImage(val prompt: String = "") : AssistantIntent
    data class WriteContent(val category: String = "", val topic: String = "") : AssistantIntent
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val intent: AssistantIntent = AssistantIntent.None,
    val language: AssistantLanguage = AssistantLanguage.ENGLISH,
    val isStreaming: Boolean = false
)

enum class MessageSender {
    USER, MEGHLA
}

enum class AssistantTab(val title: String, val iconLabel: String) {
    VOICE("Voice", "Voice"),
    CHAT("Chat", "Chat"),
    CREATE_IMAGE("Create Image", "Art"),
    WRITE("Write", "Writer"),
    INTEGRATIONS("Apps", "Apps"),
    SETTINGS("Settings", "Settings")
}

data class GeneratedArtPiece(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val style: String,
    val drawableRes: Int? = null,
    val date: String
)

data class WritingCategory(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val samplePrompts: List<String>
)
