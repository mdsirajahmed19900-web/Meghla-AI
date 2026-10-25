package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.GoldenSpark
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
fun IntegrationsHubScreen(
    viewModel: MeghlaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var whatsAppPhone by remember { mutableStateOf("") }
    var whatsAppMsg by remember { mutableStateOf("Hey! How are you doing?") }

    var messengerMsg by remember { mutableStateOf("Hello from Siraj! ✨") }
    var imoMsg by remember { mutableStateOf("Hey there, Siraj here!") }

    var callPhone by remember { mutableStateOf("") }
    var youtubeQuery by remember { mutableStateOf("Best Trending Hits 2026") }

    val popularMusic = listOf(
        "Trending Bangla Songs",
        "Top Bollywood Hits",
        "Cyberpunk Synthwave Beats",
        "Arijit Singh Best",
        "Relaxing Lo-Fi Chill"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .testTag("integrations_hub_screen")
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
                imageVector = Icons.Default.Devices,
                contentDescription = "Apps",
                tint = MintGreen,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "App Control Center",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "WhatsApp • Messenger • Imo • Phone • YouTube",
                    color = MintGreen,
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
            // 1. WhatsApp Dispatch Card
            IntegrationCard(
                title = "WhatsApp Controller",
                subtitle = "Send direct messages or open chat threads",
                accentColor = MintGreen,
                icon = Icons.Default.Chat
            ) {
                OutlinedTextField(
                    value = whatsAppPhone,
                    onValueChange = { whatsAppPhone = it },
                    label = { Text("Phone Number (with country code, optional)") },
                    placeholder = { Text("+1234567890") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = whatsAppMsg,
                    onValueChange = { whatsAppMsg = it },
                    label = { Text("Message Text") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.executeWhatsApp(context, whatsAppPhone, whatsAppMsg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen, contentColor = DeepMidnight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send via WhatsApp", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Facebook Messenger Card
            IntegrationCard(
                title = "Facebook Messenger",
                subtitle = "Share texts and chat instantly",
                accentColor = NeonCyan,
                icon = Icons.Default.Chat
            ) {
                OutlinedTextField(
                    value = messengerMsg,
                    onValueChange = { messengerMsg = it },
                    label = { Text("Message Text") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.executeMessenger(context, messengerMsg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepMidnight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Launch Facebook Messenger", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Imo Messenger Card
            IntegrationCard(
                title = "Imo Messenger",
                subtitle = "Send text messages via Imo",
                accentColor = NeonMagenta,
                icon = Icons.Default.Chat
            ) {
                OutlinedTextField(
                    value = imoMsg,
                    onValueChange = { imoMsg = it },
                    label = { Text("Message Text") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.executeImo(context, imoMsg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta, contentColor = DeepMidnight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Launch Imo Messenger", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Phone Caller Card
            IntegrationCard(
                title = "Phone Call Initiator",
                subtitle = "Direct dial or phone call for Siraj",
                accentColor = GoldenSpark,
                icon = Icons.Default.Call
            ) {
                OutlinedTextField(
                    value = callPhone,
                    onValueChange = { callPhone = it },
                    label = { Text("Contact Phone Number") },
                    placeholder = { Text("e.g. +18001234567") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.executeCall(context, callPhone)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenSpark, contentColor = DeepMidnight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Initiate Phone Call", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. YouTube Music Streamer Card
            IntegrationCard(
                title = "YouTube Music Streamer",
                subtitle = "Search and stream music instantly",
                accentColor = Color(0xFFFF4444),
                icon = Icons.Default.PlayArrow
            ) {
                OutlinedTextField(
                    value = youtubeQuery,
                    onValueChange = { youtubeQuery = it },
                    label = { Text("Artist, Song, or Playlist") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Music quick suggestions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    popularMusic.forEach { song ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceElevated)
                                .clickable { youtubeQuery = song }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "🎵 $song", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.executeYouTube(context, youtubeQuery)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Stream", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stream on YouTube", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun IntegrationCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun appTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NeonCyan,
    unfocusedBorderColor = SurfaceCardBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedContainerColor = SurfaceElevated,
    unfocusedContainerColor = SurfaceElevated,
    focusedLabelColor = NeonCyan,
    unfocusedLabelColor = TextMuted
)
