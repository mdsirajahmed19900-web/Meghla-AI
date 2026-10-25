package com.example.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.AssistantLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {
    private val tag = "TtsManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    var pitch: Float = 1.15f // Youthful, bright voice
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    var speechRate: Float = 1.0f
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setPitch(pitch)
            tts?.setSpeechRate(speechRate)
            setLanguage(AssistantLanguage.ENGLISH)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            Log.d(tag, "TTS initialized successfully")
        } else {
            Log.e(tag, "TTS initialization failed with status $status")
            isInitialized = false
        }
    }

    fun setLanguage(language: AssistantLanguage) {
        if (!isInitialized) return
        val targetLocale = when (language) {
            AssistantLanguage.ENGLISH -> Locale.US
            AssistantLanguage.BENGALI -> Locale.forLanguageTag("bn-BD")
            AssistantLanguage.HINDI -> Locale.forLanguageTag("hi-IN")
        }

        val result = tts?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(tag, "Language $targetLocale not fully supported by device TTS engine; fallback to US")
            tts?.setLanguage(Locale.US)
        }
    }

    fun speak(text: String, language: AssistantLanguage? = null) {
        if (_isMuted.value) return
        if (!isInitialized) {
            Log.w(tag, "TTS not initialized yet")
            return
        }

        if (language != null) {
            setLanguage(language)
        }

        // Clean text of emojis and asterisks for smooth speaking
        val cleanText = text
            .replace("\\*+".toRegex(), "")
            .replace("[\\p{So}\\p{Cn}]".toRegex(), "")
            .trim()

        if (cleanText.isBlank()) return

        stop()
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "MeghlaUtterance_${System.currentTimeMillis()}")
        _isSpeaking.value = true
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun toggleMute(): Boolean {
        val newMute = !_isMuted.value
        _isMuted.value = newMute
        if (newMute) {
            stop()
        }
        return newMute
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        _isSpeaking.value = false
    }
}
