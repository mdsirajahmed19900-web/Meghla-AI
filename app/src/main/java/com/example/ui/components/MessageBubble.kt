package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AssistantIntent
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
    message: ChatMessage,
    onSpeak: (String) -> Unit,
    onExecuteAction: ((AssistantIntent) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUser = message.sender == MessageSender.USER
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Row(
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(if (isUser) "user_message_row" else "meghla_message_row")
    ) {
        if (!isUser) {
            // Meghla avatar small
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, NeonCyan, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.meghla_portrait),
                    contentDescription = "Meghla",
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            val backgroundBrush = if (isUser) {
                Brush.linearGradient(listOf(Color(0xFF2C2454), Color(0xFF38296B)))
            } else {
                Brush.linearGradient(listOf(SurfaceElevated, SurfaceCard))
            }

            val borderBrush = if (isUser) {
                Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.4f), NeonMagenta.copy(alpha = 0.4f)))
            } else {
                Brush.linearGradient(listOf(SurfaceCardBorder, NeonCyan.copy(alpha = 0.3f)))
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(backgroundBrush)
                    .border(
                        1.dp,
                        borderBrush,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(14.dp)
            ) {
                Column {
                    if (!isUser) {
                        Text(
                            text = "Meghla AI",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = message.text,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    // Embedded action trigger if intent detected
                    if (!isUser && message.intent !is AssistantIntent.None && message.intent !is AssistantIntent.OriginQuestion) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AssistantActionChip(intent = message.intent, onClick = {
                            onExecuteAction?.invoke(message.intent)
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action row (Time, Speak, Copy)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = timeFormatted,
                    color = TextMuted,
                    fontSize = 10.sp
                )

                if (!isUser) {
                    IconButton(
                        onClick = { onSpeak(message.text) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Aloud",
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Meghla Message", message.text)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy text",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AssistantActionChip(intent: AssistantIntent, onClick: () -> Unit) {
    val (label, icon, color) = when (intent) {
        is AssistantIntent.SendWhatsApp -> Triple("Launch WhatsApp", Icons.Default.Send, MintGreen)
        is AssistantIntent.SendMessenger -> Triple("Open Messenger", Icons.Default.Send, NeonCyan)
        is AssistantIntent.SendImo -> Triple("Open Imo", Icons.Default.Send, NeonMagenta)
        is AssistantIntent.MakeCall -> Triple("Make Call", Icons.Default.Call, MintGreen)
        is AssistantIntent.StreamYouTube -> Triple("Stream on YouTube", Icons.Default.PlayArrow, Color(0xFFFF4444))
        is AssistantIntent.GenerateImage -> Triple("View in Art Studio", Icons.Default.PlayArrow, Color(0xFFFFD166))
        is AssistantIntent.WriteContent -> Triple("Open in Writer", Icons.Default.Send, NeonCyan)
        else -> Triple("Open", Icons.Default.PlayArrow, NeonCyan)
    }

    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(34.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
