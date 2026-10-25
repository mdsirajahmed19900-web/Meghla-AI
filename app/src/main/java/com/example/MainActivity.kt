package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AssistantTab
import com.example.ui.screens.ChatHistoryScreen
import com.example.ui.screens.CreateImageScreen
import com.example.ui.screens.IntegrationsHubScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceHomeScreen
import com.example.ui.screens.WriterScreen
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MeghlaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MeghlaAppRoot()
            }
        }
    }
}

@Composable
fun MeghlaAppRoot(
    viewModel: MeghlaViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()

    // Request RECORD_AUDIO and CALL_PHONE permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Result handled
    }

    LaunchedEffect(Unit) {
        val audioGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!audioGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE
                )
            )
        }
    }

    // Handle back button to return to Voice tab if in another tab
    if (currentTab != AssistantTab.VOICE) {
        BackHandler {
            viewModel.setTab(AssistantTab.VOICE)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MeghlaBottomNavBar(
                currentTab = currentTab,
                onTabSelect = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tabTransition") { tab ->
                when (tab) {
                    AssistantTab.VOICE -> VoiceHomeScreen(viewModel = viewModel)
                    AssistantTab.CHAT -> ChatHistoryScreen(viewModel = viewModel)
                    AssistantTab.CREATE_IMAGE -> CreateImageScreen(viewModel = viewModel)
                    AssistantTab.WRITE -> WriterScreen(viewModel = viewModel)
                    AssistantTab.INTEGRATIONS -> IntegrationsHubScreen(viewModel = viewModel)
                    AssistantTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MeghlaBottomNavBar(
    currentTab: AssistantTab,
    onTabSelect: (AssistantTab) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(width = 1.dp, color = SurfaceCardBorder)
            .testTag("meghla_bottom_nav_bar")
    ) {
        AssistantTab.values().forEach { tab ->
            val isSelected = tab == currentTab
            val (iconSelected, iconUnselected) = getTabIcons(tab)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelect(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) iconSelected else iconUnselected,
                        contentDescription = tab.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.iconLabel,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepMidnight,
                    selectedTextColor = NeonCyan,
                    indicatorColor = if (tab == AssistantTab.VOICE) NeonCyan else NeonMagenta,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}

fun getTabIcons(tab: AssistantTab): Pair<ImageVector, ImageVector> {
    return when (tab) {
        AssistantTab.VOICE -> Pair(Icons.Filled.Mic, Icons.Outlined.Mic)
        AssistantTab.CHAT -> Pair(Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat)
        AssistantTab.CREATE_IMAGE -> Pair(Icons.Filled.Palette, Icons.Outlined.Palette)
        AssistantTab.WRITE -> Pair(Icons.Filled.EditNote, Icons.Outlined.EditNote)
        AssistantTab.INTEGRATIONS -> Pair(Icons.Filled.Devices, Icons.Outlined.Devices)
        AssistantTab.SETTINGS -> Pair(Icons.Filled.Settings, Icons.Outlined.Settings)
    }
}
