package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun WriterScreen(
    viewModel: MeghlaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories = viewModel.writingCategories
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var topicInput by remember { mutableStateOf("") }
    val generatedText by viewModel.generatedWriting.collectAsState()
    val isLoading by viewModel.isWritingLoading.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .testTag("writer_screen")
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
                imageVector = Icons.Default.EditNote,
                contentDescription = "Writer",
                tint = NeonCyan,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Meghla Sassy Writer",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Flirty texts, comebacks & WhatsApp replies for Siraj",
                    color = NeonMagenta,
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
            // Category horizontal tabs
            Text(
                text = "SELECT WRITING VIBE",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                categories.forEach { cat ->
                    val isSelected = cat.id == selectedCategory.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) NeonCyan else SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan else SurfaceCardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = cat.title,
                            color = if (isSelected) DeepMidnight else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category card details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = selectedCategory.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Quick Inspiration:",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    selectedCategory.samplePrompts.forEach { sample ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceElevated)
                                .clickable { topicInput = sample }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "👉 $sample", color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Topic Input Box
            Text(
                text = "WHAT SHOULD MEGHLA WRITE ABOUT?",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = topicInput,
                onValueChange = { topicInput = it },
                placeholder = {
                    Text("e.g. Reply to a cute girl saying I'm busy right now...", color = TextMuted, fontSize = 13.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = SurfaceCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                shape = RoundedCornerShape(14.dp),
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("writer_topic_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Generate Button
            Button(
                onClick = {
                    if (topicInput.isNotBlank()) {
                        viewModel.generateWriting(selectedCategory.title, topicInput)
                    } else {
                        viewModel.generateWriting(selectedCategory.title, selectedCategory.samplePrompts.first())
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = DeepMidnight
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_writing_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = DeepMidnight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Meghla is Crafting Words...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Craft", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate with Meghla Sass", fontWeight = FontWeight.Bold)
                }
            }

            // Output Card
            if (generatedText.isNotBlank()) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "CRAFTED FOR SIRAJ",
                    color = NeonMagenta,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(SurfaceElevated, SurfaceCard)
                            )
                        )
                        .border(1.dp, NeonMagenta.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                        .testTag("generated_writing_output_card")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✨ Meghla's Masterpiece",
                                color = NeonMagenta,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row {
                                IconButton(
                                    onClick = { viewModel.speakText(generatedText) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Read Aloud",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Crafted Text", generatedText))
                                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Text",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = generatedText,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Share dispatch buttons to WhatsApp, Messenger, Imo
                        Text(
                            text = "Direct Send via:",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // WhatsApp
                            Button(
                                onClick = {
                                    viewModel.executeWhatsApp(context, "", generatedText)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MintGreen.copy(alpha = 0.2f),
                                    contentColor = MintGreen
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Messenger
                            Button(
                                onClick = {
                                    viewModel.executeMessenger(context, generatedText)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan.copy(alpha = 0.2f),
                                    contentColor = NeonCyan
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Messenger", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Imo
                            Button(
                                onClick = {
                                    viewModel.executeImo(context, generatedText)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonMagenta.copy(alpha = 0.2f),
                                    contentColor = NeonMagenta
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Imo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
