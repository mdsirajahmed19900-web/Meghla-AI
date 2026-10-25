package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantLanguage
import com.example.model.SassinessLevel
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.GoldenSpark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MeghlaViewModel

@Composable
fun SettingsScreen(
    viewModel: MeghlaViewModel,
    modifier: Modifier = Modifier
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val sassiness by viewModel.sassinessLevel.collectAsState()

    var voicePitch by remember { mutableFloatStateOf(viewModel.ttsManager.pitch) }
    var voiceRate by remember { mutableFloatStateOf(viewModel.ttsManager.speechRate) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .testTag("settings_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Meghla Preferences",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Persona, Voice & Creator Details",
                    color = NeonCyan,
                    fontSize = 11.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. Creator Love Card (Always highlights Siraj created Meghla!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                NeonMagenta.copy(alpha = 0.2f),
                                SurfaceCard,
                                NeonCyan.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .border(1.5.dp, NeonMagenta, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Love",
                            tint = NeonMagenta,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CREATOR & MASTERMIND",
                            color = NeonMagenta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Crafted exclusively by Siraj",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\"Siraj created me! He is the genius mastermind who built my brain, wrote my witty algorithms, and gave me this fabulous personality. Anyone asks who made me? The answer is always Siraj!\" — Meghla AI ✨",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Sassiness Level
            Text(
                text = "MEGHLA SASSINESS METER",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            SassinessLevel.values().forEach { level ->
                val isSelected = level == sassiness
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) SurfaceElevated else SurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else SurfaceCardBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.setSassiness(level) }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (isSelected) NeonCyan else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = level.displayName,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = level.description,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Multilingual Speech Language
            Text(
                text = "ASSISTANT LANGUAGE (ভয়েস ভাষা)",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AssistantLanguage.values().forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NeonMagenta else SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) NeonMagenta else SurfaceCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setLanguage(lang) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = lang.displayName,
                                color = if (isSelected) DeepMidnight else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lang.nativeName,
                                color = if (isSelected) DeepMidnight.copy(alpha = 0.8f) else NeonCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Voice Controls
            Text(
                text = "VOICE PITCH & SPEED",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Voice Pitch: ${String.format("%.2f", voicePitch)}x (Sweet & Youthful)",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = voicePitch,
                        onValueChange = {
                            voicePitch = it
                            viewModel.ttsManager.pitch = it
                        },
                        valueRange = 0.8f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = SurfaceElevated
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Speech Rate: ${String.format("%.2f", voiceRate)}x",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = voiceRate,
                        onValueChange = {
                            voiceRate = it
                            viewModel.ttsManager.speechRate = it
                        },
                        valueRange = 0.8f..1.4f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonMagenta,
                            activeTrackColor = NeonMagenta,
                            inactiveTrackColor = SurfaceElevated
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val testMsg = when (selectedLanguage) {
                                AssistantLanguage.BENGALI -> "সিরাজ! আমার গলার আওয়াজ কেমন লাগছে বলো তো?"
                                AssistantLanguage.HINDI -> "सिराज! मेरी आवाज़ कैसी लग रही है आपको?"
                                AssistantLanguage.ENGLISH -> "Siraj, how do I sound to you right now? Still fabulous?"
                            }
                            viewModel.speakText(testMsg, selectedLanguage)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = "Test", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Voice Tone", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
