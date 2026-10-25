package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantLanguage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextPrimary

@Composable
fun QuickActionChips(
    language: AssistantLanguage,
    onChipClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chips = when (language) {
        AssistantLanguage.BENGALI -> listOf(
            "সিরাজ, তোমাকে কে বানিয়েছে? 🤖",
            "হোয়াটসঅ্যাপে মেসেজ পাঠাও 💬",
            "ইউটিউবে গান বাজাও 🎵",
            "ফোন কল করো 📞",
            "একটা ফ্লার্টি জোক বলো 😉",
            "দারুণ ছবি বানাও 🎨"
        )
        AssistantLanguage.HINDI -> listOf(
            "सिराज, तुम्हें किसने बनाया? 🤖",
            "व्हाट्सएप पर मैसेज भेजो 💬",
            "यूट्यूब पर गाना चलाओ 🎶",
            "कॉल मिलाओ 📞",
            "कोई मजेदार जोक सुनाओ 😆",
            "आर्ट बनाओ 🎨"
        )
        AssistantLanguage.ENGLISH -> listOf(
            "Siraj, who created you? 👑",
            "Send WhatsApp message 💬",
            "Stream music on YouTube 🎵",
            "Call someone 📞",
            "Write a flirty text 💋",
            "Create Cyberpunk Art 🎨"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("quick_action_chips_row")
    ) {
        chips.forEachIndexed { index, chipText ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp))
                    .minimumInteractiveComponentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = NeonCyan)
                    ) {
                        onChipClick(chipText)
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("quick_chip_$index")
            ) {
                Text(
                    text = chipText,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
