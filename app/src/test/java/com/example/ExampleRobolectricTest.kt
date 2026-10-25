package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.MeghlaPersona
import com.example.model.AssistantIntent
import com.example.model.AssistantLanguage
import com.example.model.SassinessLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Megla AI", appName)
    }

    @Test
    fun `origin question recognizes Siraj as creator`() {
        val intent = MeghlaPersona.parseIntent("Who created you?")
        assertTrue(intent is AssistantIntent.OriginQuestion)

        val response = MeghlaPersona.getInstantResponse(
            query = "Who created you?",
            intent = intent,
            language = AssistantLanguage.ENGLISH,
            sassiness = SassinessLevel.SASSY
        )
        assertTrue(response.contains("Siraj"))
    }

    @Test
    fun `bengali origin question recognizes Siraj as creator`() {
        val intent = MeghlaPersona.parseIntent("তোমাকে কে বানিয়েছে?")
        assertTrue(intent is AssistantIntent.OriginQuestion)

        val response = MeghlaPersona.getInstantResponse(
            query = "তোমাকে কে বানিয়েছে?",
            intent = intent,
            language = AssistantLanguage.BENGALI,
            sassiness = SassinessLevel.SASSY
        )
        assertTrue(response.contains("সিরাজ"))
    }

    @Test
    fun `whatsapp intent parses correctly`() {
        val intent = MeghlaPersona.parseIntent("send whatsapp to my friend")
        assertTrue(intent is AssistantIntent.SendWhatsApp)
    }

    @Test
    fun `youtube intent parses correctly`() {
        val intent = MeghlaPersona.parseIntent("play music on youtube")
        assertTrue(intent is AssistantIntent.StreamYouTube)
    }
}
