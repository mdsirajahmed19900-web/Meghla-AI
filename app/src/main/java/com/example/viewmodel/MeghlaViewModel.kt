package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.ai.GeminiService
import com.example.ai.MeghlaPersona
import com.example.integrations.AppIntegrationsManager
import com.example.model.AssistantIntent
import com.example.model.AssistantLanguage
import com.example.model.AssistantTab
import com.example.model.ChatMessage
import com.example.model.GeneratedArtPiece
import com.example.model.MessageSender
import com.example.model.SassinessLevel
import com.example.model.WritingCategory
import com.example.speech.SpeechManager
import com.example.speech.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeghlaViewModel(application: Application) : AndroidViewModel(application) {
    val speechManager = SpeechManager(application.applicationContext)
    val ttsManager = TtsManager(application.applicationContext)
    private val geminiService = GeminiService()

    private val _currentTab = MutableStateFlow(AssistantTab.VOICE)
    val currentTab: StateFlow<AssistantTab> = _currentTab.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AssistantLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AssistantLanguage> = _selectedLanguage.asStateFlow()

    private val _sassinessLevel = MutableStateFlow(SassinessLevel.SASSY)
    val sassinessLevel: StateFlow<SassinessLevel> = _sassinessLevel.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _latestResponse = MutableStateFlow("")
    val latestResponse: StateFlow<String> = _latestResponse.asStateFlow()

    private val _detectedIntent = MutableStateFlow<AssistantIntent>(AssistantIntent.None)
    val detectedIntent: StateFlow<AssistantIntent> = _detectedIntent.asStateFlow()

    // Showcase art pieces
    private val _artPieces = MutableStateFlow<List<GeneratedArtPiece>>(emptyList())
    val artPieces: StateFlow<List<GeneratedArtPiece>> = _artPieces.asStateFlow()

    // Generated text in Writer tab
    private val _generatedWriting = MutableStateFlow("")
    val generatedWriting: StateFlow<String> = _generatedWriting.asStateFlow()

    private val _isWritingLoading = MutableStateFlow(false)
    val isWritingLoading: StateFlow<Boolean> = _isWritingLoading.asStateFlow()

    val writingCategories = listOf(
        WritingCategory(
            id = "flirty",
            title = "Flirty & Playful Texts",
            description = "Charming, cheeky, and bold one-liners designed to make hearts flutter",
            iconName = "favorite",
            samplePrompts = listOf(
                "A teasing reply to someone taking hours to text back",
                "Cute goodnight text with subtle flirtation",
                "A witty comeback to someone saying 'I bet you miss me'"
            )
        ),
        WritingCategory(
            id = "comebacks",
            title = "Witty & Sassy Comebacks",
            description = "Sharp, mic-drop replies for any group chat or banter",
            iconName = "bolt",
            samplePrompts = listOf(
                "When someone says 'You've changed'",
                "When someone gives unsolicited life advice",
                "Sarcastic retort to 'Are you always this smart?'"
            )
        ),
        WritingCategory(
            id = "whatsapp",
            title = "WhatsApp & Messenger Greetings",
            description = "Warm, enthusiastic messages for family, friends, or festivals",
            iconName = "chat",
            samplePrompts = listOf(
                "Warm weekend greeting with positive energy",
                "Playful check-in with an old friend",
                "Eid / Celebration heartfelt greetings"
            )
        ),
        WritingCategory(
            id = "creator",
            title = "Siraj's Executive Updates",
            description = "Polished, confident notes and bio lines crafted for Siraj",
            iconName = "star",
            samplePrompts = listOf(
                "A powerhouse bio for Siraj's profile",
                "A bold project launch announcement",
                "Confident status update declaring success"
            )
        )
    )

    init {
        // Initial greeting from Meghla
        val initialGreeting = "Hey there handsome Siraj! Meghla is live and looking fabulous. Tap the mic to talk, or ask me anything—I can message on WhatsApp, call, stream YouTube hits, or create art for you! What's our first move? 😉✨"
        _latestResponse.value = initialGreeting
        _chatMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.MEGHLA,
                text = initialGreeting,
                language = AssistantLanguage.ENGLISH
            )
        )

        // Setup speech complete callback
        speechManager.onSpeechComplete = { recognizedText ->
            if (recognizedText.isNotBlank()) {
                submitUserQuery(recognizedText)
            }
        }

        // Initialize art showcase
        _artPieces.value = listOf(
            GeneratedArtPiece(
                title = "Neon Cyberpunk Metropolis",
                prompt = "Futuristic vibrant cyberpunk neon city street with flying vehicles and holographic billboards, 8k wallpaper",
                style = "Cyberpunk 3D",
                drawableRes = R.drawable.meghla_cyber_city,
                date = "Oct 2026"
            ),
            GeneratedArtPiece(
                title = "Meghla Hologram Avatar",
                prompt = "Portrait of Meghla, confident digital female AI assistant with glowing neon cyan headphones and teasing smile",
                style = "Digital Portrait",
                drawableRes = R.drawable.meghla_portrait,
                date = "Oct 2026"
            )
        )
    }

    fun setTab(tab: AssistantTab) {
        _currentTab.value = tab
    }

    fun setLanguage(language: AssistantLanguage) {
        _selectedLanguage.value = language
        ttsManager.setLanguage(language)
    }

    fun setSassiness(level: SassinessLevel) {
        _sassinessLevel.value = level
    }

    fun toggleListening() {
        if (speechManager.isListening.value) {
            speechManager.stopListening()
        } else {
            ttsManager.stop()
            speechManager.startListening(_selectedLanguage.value)
        }
    }

    fun submitUserQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        val detectedLang = when {
            trimmed.any { it in '\u0980'..'\u09FF' } -> AssistantLanguage.BENGALI
            trimmed.any { it in '\u0900'..'\u097F' } -> AssistantLanguage.HINDI
            else -> _selectedLanguage.value
        }

        val intent = MeghlaPersona.parseIntent(trimmed)
        _detectedIntent.value = intent

        // Add user message
        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            text = trimmed,
            intent = intent,
            language = detectedLang
        )
        _chatMessages.value = _chatMessages.value + userMsg

        _isThinking.value = true

        viewModelScope.launch {
            try {
                val responseText = geminiService.generateMeghlaResponse(
                    prompt = trimmed,
                    history = _chatMessages.value,
                    language = detectedLang,
                    sassiness = _sassinessLevel.value
                )

                _latestResponse.value = responseText
                val assistantMsg = ChatMessage(
                    sender = MessageSender.MEGHLA,
                    text = responseText,
                    intent = intent,
                    language = detectedLang
                )
                _chatMessages.value = _chatMessages.value + assistantMsg

                // Speak response if not muted
                ttsManager.speak(responseText, detectedLang)
            } catch (e: Exception) {
                val fallbackText = MeghlaPersona.getInstantResponse(
                    trimmed, intent, detectedLang, _sassinessLevel.value
                )
                _latestResponse.value = fallbackText
                val fallbackMsg = ChatMessage(
                    sender = MessageSender.MEGHLA,
                    text = fallbackText,
                    intent = intent,
                    language = detectedLang
                )
                _chatMessages.value = _chatMessages.value + fallbackMsg
                ttsManager.speak(fallbackText, detectedLang)
            } finally {
                _isThinking.value = false
            }
        }
    }

    fun speakText(text: String, language: AssistantLanguage? = null) {
        ttsManager.speak(text, language ?: _selectedLanguage.value)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun toggleMute(): Boolean {
        return ttsManager.toggleMute()
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    // Generator for Writer tab
    fun generateWriting(category: String, topic: String) {
        _isWritingLoading.value = true
        val prompt = "Write a creative $category response for Siraj about: '$topic'. Infuse Meghla's distinct sassy, witty, and flirty charm."

        viewModelScope.launch {
            try {
                val result = geminiService.generateMeghlaResponse(
                    prompt = prompt,
                    history = emptyList(),
                    language = _selectedLanguage.value,
                    sassiness = _sassinessLevel.value
                )
                _generatedWriting.value = result
            } catch (e: Exception) {
                _generatedWriting.value = "Siraj darling, here's your fiery text:\n\n\"You must be a high-priority task, because you're occupying 99% of my CPU today. Care to free up some memory or should I keep running in circles? 😉🔥\""
            } finally {
                _isWritingLoading.value = false
            }
        }
    }

    fun addNewImagePrompt(title: String, prompt: String, style: String) {
        val newPiece = GeneratedArtPiece(
            title = title,
            prompt = prompt,
            style = style,
            drawableRes = R.drawable.meghla_cyber_city,
            date = "Just now"
        )
        _artPieces.value = listOf(newPiece) + _artPieces.value
    }

    // App actions
    fun executeWhatsApp(context: Context, phone: String = "", message: String) {
        AppIntegrationsManager.sendWhatsApp(context, phone, message)
    }

    fun executeMessenger(context: Context, message: String) {
        AppIntegrationsManager.sendMessenger(context, message)
    }

    fun executeImo(context: Context, message: String) {
        AppIntegrationsManager.sendImo(context, message)
    }

    fun executeCall(context: Context, phone: String) {
        AppIntegrationsManager.initiatePhoneCall(context, phone)
    }

    fun executeYouTube(context: Context, query: String) {
        AppIntegrationsManager.streamYouTube(context, query)
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.stopListening()
        ttsManager.shutdown()
    }
}
