package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GeneratedArtPiece
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
fun CreateImageScreen(
    viewModel: MeghlaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val artPieces by viewModel.artPieces.collectAsState()

    var promptInput by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk 3D") }

    val styles = listOf("Cyberpunk 3D", "Neon Synthwave", "Hologram Anime", "Photorealistic 8K", "Dark Fantasy", "Watercolor")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .testTag("create_image_screen")
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
                imageVector = Icons.Default.Palette,
                contentDescription = "Art Studio",
                tint = NeonMagenta,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Meghla Art Studio",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AI Visuals & Prompt Crafting for Siraj",
                    color = NeonCyan,
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))

                // Prompt creation box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "DESCRIBE YOUR VISION, SIRAJ",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = {
                                Text("e.g. Siraj standing on a neon rooftop overlooking a cyberpunk sky city...", color = TextMuted, fontSize = 13.sp)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonMagenta,
                                unfocusedBorderColor = SurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = SurfaceElevated,
                                unfocusedContainerColor = SurfaceElevated
                            ),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("image_prompt_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Style selector chips
                        Text(
                            text = "Aesthetic Style:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            styles.forEach { style ->
                                val isSelected = style == selectedStyle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) NeonMagenta else SurfaceElevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonMagenta else SurfaceCardBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedStyle = style }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = style,
                                        color = if (isSelected) DeepMidnight else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Spice Up + Generate
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    val spicier = if (promptInput.isNotBlank()) {
                                        "Masterpiece of $promptInput, $selectedStyle style, cinematic volumetric lighting, hyper-detailed reflections, 8k resolution, octane render"
                                    } else {
                                        "A majestic cyberpunk anime goddess named Meghla floating in holographic neon lights, digital art masterpiece for Siraj"
                                    }
                                    promptInput = spicier
                                    Toast.makeText(context, "Prompt spiced up with Meghla's flair!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SurfaceElevated,
                                    contentColor = GoldenSpark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Spice up", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Spice Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (promptInput.isNotBlank()) {
                                        val title = promptInput.take(28) + "..."
                                        viewModel.addNewImagePrompt(title, promptInput, selectedStyle)
                                        Toast.makeText(context, "Added to Meghla Showcase!", Toast.LENGTH_SHORT).show()
                                        promptInput = ""
                                    } else {
                                        Toast.makeText(context, "Please enter a vision first, Siraj!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonMagenta,
                                    contentColor = DeepMidnight
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("create_art_button")
                            ) {
                                Text("Craft Art", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "GALLERY & RECENT SHOWCASE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            items(artPieces, key = { it.id }) { piece ->
                ArtPieceCard(piece = piece, onCopyPrompt = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Art Prompt", piece.prompt))
                    Toast.makeText(context, "Prompt copied!", Toast.LENGTH_SHORT).show()
                })
                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ArtPieceCard(piece: GeneratedArtPiece, onCopyPrompt: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
    ) {
        Column {
            // Artwork Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFF0F0B20))
            ) {
                if (piece.drawableRes != null) {
                    Image(
                        painter = painterResource(id = piece.drawableRes),
                        contentDescription = piece.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.3f), NeonMagenta.copy(alpha = 0.3f)))
                            )
                    ) {
                        Text("🎨 Generated Concept", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // Style badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepMidnight.copy(alpha = 0.8f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = piece.style, color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Info & prompt
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = piece.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = piece.date,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = piece.prompt,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCopyPrompt() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Prompt", tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Prompt", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
