package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantIntent
import com.example.model.AssistantLanguage
import com.example.model.AssistantTab
import com.example.ui.components.AssistantActionChip
import com.example.ui.components.MeghlaAvatarView
import com.example.ui.components.QuickActionChips
import com.example.ui.components.VoicePulseOrb
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MintGreen
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
fun VoiceHomeScreen(
    viewModel: MeghlaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isListening by viewModel.speechManager.isListening.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val isMuted by viewModel.ttsManager.isMuted.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val soundLevel by viewModel.speechManager.soundLevel.collectAsState()
    val speechText by viewModel.speechManager.speechText.collectAsState()
    val latestResponse by viewModel.latestResponse.collectAsState()
    val detectedIntent by viewModel.detectedIntent.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var textInput by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .testTag("voice_home_screen")
    ) {
        // Top App Bar Header
        HomeTopBar(
            selectedLanguage = selectedLanguage,
            isMuted = isMuted,
            onLanguageChange = { viewModel.setLanguage(it) },
            onToggleMute = { viewModel.toggleMute() }
        )

        // Scrollable content area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Meghla Hero Avatar
            MeghlaAvatarView(
                isListening = isListening,
                isSpeaking = isSpeaking,
                isThinking = isThinking,
                soundLevel = soundLevel
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live recognized voice speech display
            AnimatedVisibility(
                visible = isListening && speechText.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, MintGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Hearing: \"$speechText\"",
                        color = MintGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Latest Response Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(SurfaceElevated, SurfaceCard)
                        )
                    )
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
                    .testTag("latest_response_card")
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✨ Meghla AI",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• for Siraj",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (isThinking) {
                            CircularProgressIndicator(
                                color = NeonMagenta,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            IconButton(
                                onClick = { viewModel.speakText(latestResponse) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Response",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isThinking) "Cooking up something brilliant and sassy for Siraj..." else latestResponse,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )

                    // Embedded action trigger if intent detected
                    if (detectedIntent !is AssistantIntent.None && detectedIntent !is AssistantIntent.OriginQuestion) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AssistantActionChip(intent = detectedIntent, onClick = {
                            handleIntentExecution(context, viewModel, detectedIntent)
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Action suggestions chips
            Text(
                text = "SUGGESTED FOR SIRAJ",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            QuickActionChips(
                language = selectedLanguage,
                onChipClick = { chipPrompt ->
                    viewModel.submitUserQuery(chipPrompt)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // "Tap to Talk" pulsating microphone button
            VoicePulseOrb(
                isListening = isListening,
                soundLevel = soundLevel,
                onClick = {
                    viewModel.toggleListening()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom text query bar
        BottomQueryBar(
            query = textInput,
            onQueryChange = { textInput = it },
            onSend = {
                if (textInput.isNotBlank()) {
                    viewModel.submitUserQuery(textInput)
                    textInput = ""
                }
            },
            onMicClick = {
                viewModel.toggleListening()
            }
        )
    }
}

@Composable
fun HomeTopBar(
    selectedLanguage: AssistantLanguage,
    isMuted: Boolean,
    onLanguageChange: (AssistantLanguage) -> Unit,
    onToggleMute: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = "MEGHLA AI",
                color = NeonCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Personal Assistant for Siraj",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Language selector chips
            AssistantLanguage.values().forEach { lang ->
                val isSelected = lang == selectedLanguage
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonMagenta else SurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) NeonMagenta else SurfaceCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onLanguageChange(lang) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = when (lang) {
                            AssistantLanguage.ENGLISH -> "EN"
                            AssistantLanguage.BENGALI -> "বাং"
                            AssistantLanguage.HINDI -> "हिं"
                        },
                        color = if (isSelected) DeepMidnight else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Voice Sound Mute/Unmute toggle
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, CircleShape)
                    .clickable { onToggleMute() }
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "Unmute Voice" else "Mute Voice",
                    tint = if (isMuted) TextMuted else NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BottomQueryBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(1.dp, SurfaceCardBorder)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text("Ask Meghla or give a command...", color = TextMuted, fontSize = 13.sp)
                },
                trailingIcon = {
                    IconButton(onClick = onMicClick) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = NeonCyan
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = SurfaceCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 2,
                modifier = Modifier
                    .weight(1f)
                    .testTag("query_text_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(NeonCyan, NeonMagenta))
                    )
                    .clickable { onSend() }
                    .minimumInteractiveComponentSize()
                    .testTag("send_query_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = DeepMidnight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun handleIntentExecution(context: Context, viewModel: MeghlaViewModel, intent: AssistantIntent) {
    when (intent) {
        is AssistantIntent.SendWhatsApp -> viewModel.executeWhatsApp(context, intent.phoneNumber, intent.message)
        is AssistantIntent.SendMessenger -> viewModel.executeMessenger(context, intent.message)
        is AssistantIntent.SendImo -> viewModel.executeImo(context, intent.message)
        is AssistantIntent.MakeCall -> viewModel.executeCall(context, intent.phoneNumber)
        is AssistantIntent.StreamYouTube -> viewModel.executeYouTube(context, intent.query)
        is AssistantIntent.GenerateImage -> viewModel.setTab(AssistantTab.CREATE_IMAGE)
        is AssistantIntent.WriteContent -> viewModel.setTab(AssistantTab.WRITE)
        else -> {}
    }
}
