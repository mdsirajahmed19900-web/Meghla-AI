package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.model.AssistantLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpeechManager(private val context: Context) {
    private val tag = "SpeechManager"
    private var recognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _soundLevel = MutableStateFlow(0f)
    val soundLevel: StateFlow<Float> = _soundLevel.asStateFlow()

    private val _speechText = MutableStateFlow("")
    val speechText: StateFlow<String> = _speechText.asStateFlow()

    var onSpeechComplete: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(language: AssistantLanguage) {
        if (!isRecognitionAvailable()) {
            onError?.invoke("Speech recognition not available on this device.")
            return
        }

        stopListening()

        try {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _speechText.value = ""
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize dB (-2 to 10) to 0.0 - 1.0
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        _soundLevel.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _soundLevel.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _soundLevel.value = 0f
                        val msg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "I didn't catch that, Siraj. Tap the mic to try again!"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out, Siraj."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network issue for voice recognition."
                            else -> "Could not hear clearly."
                        }
                        Log.d(tag, "Speech error code $error: $msg")
                        if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            onError?.invoke(msg)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _soundLevel.value = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim().orEmpty()
                        if (text.isNotBlank()) {
                            _speechText.value = text
                            onSpeechComplete?.invoke(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull().orEmpty()
                        if (text.isNotBlank()) {
                            _speechText.value = text
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.speechLocale)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language.speechLocale)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            recognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e(tag, "Failed to start speech recognition", e)
            _isListening.value = false
            onError?.invoke(e.localizedMessage ?: "Failed to start listening")
        }
    }

    fun stopListening() {
        try {
            recognizer?.stopListening()
            recognizer?.destroy()
        } catch (e: Exception) {
            Log.e(tag, "Error stopping recognizer", e)
        } finally {
            recognizer = null
            _isListening.value = false
            _soundLevel.value = 0f
        }
    }
}
