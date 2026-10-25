package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary

@Composable
fun MeghlaAvatarView(
    isListening: Boolean,
    isSpeaking: Boolean,
    isThinking: Boolean,
    soundLevel: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val activeGlowBrush = when {
        isSpeaking -> Brush.sweepGradient(listOf(NeonMagenta, NeonCyan, Color(0xFF8A2BE2), NeonMagenta))
        isListening -> Brush.sweepGradient(listOf(NeonCyan, MintGreen, NeonCyan))
        isThinking -> Brush.sweepGradient(listOf(Color(0xFFFFD166), NeonMagenta, Color(0xFFFFD166)))
        else -> Brush.sweepGradient(listOf(NeonCyan.copy(alpha = 0.5f), NeonMagenta.copy(alpha = 0.5f)))
    }

    val statusText = when {
        isSpeaking -> "Meghla is Speaking..."
        isListening -> "Listening to Siraj..."
        isThinking -> "Thinking witty thoughts..."
        else -> "Meghla AI • Siraj's Assistant"
    }

    val statusColor = when {
        isSpeaking -> NeonMagenta
        isListening -> MintGreen
        isThinking -> Color(0xFFFFD166)
        else -> NeonCyan
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.testTag("meghla_avatar_view")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(140.dp)
                .scale(if (isListening) 1f + (soundLevel * 0.15f) else if (isSpeaking) pulseScale else 1f)
        ) {
            // Outer glowing aura ring
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .border(3.dp, activeGlowBrush, CircleShape)
            )

            // Inner avatar image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, DeepMidnight, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.meghla_portrait),
                    contentDescription = "Meghla AI Portrait",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Soundwave or status badge
        if (isListening || isSpeaking) {
            AudioWaveformBar(soundLevel = if (isListening) soundLevel else 0.5f)
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Status pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(SurfaceCard, RoundedCornerShape(16.dp))
                .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AudioWaveformBar(soundLevel: Float) {
    val barCount = 7
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(18.dp)
    ) {
        for (i in 0 until barCount) {
            val factor = ((i - barCount / 2).toDouble() / barCount).toFloat()
            val heightPercent = (0.25f + (soundLevel * (1f - kotlin.math.abs(factor)) * 0.75f)).coerceIn(0.2f, 1f)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((18 * heightPercent).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(NeonCyan, NeonMagenta)
                        )
                    )
            )
        }
    }
}
