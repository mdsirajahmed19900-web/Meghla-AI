package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.model.AssistantLanguage
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.SassinessLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {
    private val tag = "GeminiService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateMeghlaResponse(
        prompt: String,
        history: List<ChatMessage>,
        language: AssistantLanguage,
        sassiness: SassinessLevel
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val intent = MeghlaPersona.parseIntent(prompt)

        // For Origin questions, ALWAYS ensure Meghla declares Siraj created her!
        if (intent is com.example.model.AssistantIntent.OriginQuestion) {
            return@withContext MeghlaPersona.getInstantResponse(prompt, intent, language, sassiness)
        }

        // If API key is missing or is the placeholder, use intelligent local response
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(tag, "GEMINI_API_KEY is not set or placeholder; using Meghla persona engine")
            return@withContext MeghlaPersona.getInstantResponse(prompt, intent, language, sassiness)
        }

        try {
            val systemPrompt = MeghlaPersona.getSystemPrompt(language, sassiness)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // Include last 6 turns for conversational context
            val recentHistory = history.takeLast(6)
            for (msg in recentHistory) {
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                val partObj = JSONObject().put("text", msg.text)
                val msgObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(msgObj)
            }

            // Current prompt
            val currentMsg = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
            contentsArray.put(currentMsg)

            val rootJson = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.9)
                    put("maxOutputTokens", 500)
                })
            }

            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val respJson = JSONObject(responseBody)
                val candidates = respJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) {
                            // Ensure Siraj is mentioned if not already present
                            val finalAnswer = if (!text.contains(MeghlaPersona.USER_NAME, ignoreCase = true)) {
                                "Siraj, $text"
                            } else {
                                text
                            }
                            return@withContext finalAnswer.trim()
                        }
                    }
                }
            } else {
                Log.w(tag, "Gemini call non-successful: code=${response.code}, body=$responseBody")
            }
        } catch (e: Exception) {
            Log.e(tag, "Gemini API error, falling back to instant Meghla persona", e)
        }

        // Fallback to instant intelligent response
        MeghlaPersona.getInstantResponse(prompt, intent, language, sassiness)
    }
}
