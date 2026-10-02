package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KiemMongApp()
            }
        }
    }
}

@Composable
fun KiemMongApp(
    viewModel: GameViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentChapter by viewModel.currentChapter.collectAsStateWithLifecycle()
    val activeQuest by viewModel.activeQuest.collectAsStateWithLifecycle()
    val activeDialogue by viewModel.activeDialogue.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()
    val showTutorial by viewModel.showTutorial.collectAsStateWithLifecycle()
    val showLore by viewModel.showLore.collectAsStateWithLifecycle()

    // Handle back button behavior
    BackHandler(enabled = currentScreen != AppScreen.MAIN_MENU) {
        when (currentScreen) {
            AppScreen.MAIN_MENU -> {}
            AppScreen.COMBAT_EXPLORATION -> viewModel.setScreen(AppScreen.MAIN_MENU)
            else -> viewModel.setScreen(AppScreen.COMBAT_EXPLORATION)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp)
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                AppScreen.MAIN_MENU -> {
                    MainMenuScreen(
                        hasSaveData = viewModel.hasSaveData(),
                        onStartNewGame = { viewModel.startNewGame() },
                        onContinueGame = { viewModel.continueGame() },
                        onShowTutorial = { viewModel.setShowTutorial(true) },
                        onShowLore = { viewModel.setShowLore(true) }
                    )
                }

                AppScreen.COMBAT_EXPLORATION -> {
                    // 2D Game Canvas
                    GameCanvas(
                        engine = viewModel.engine,
                        onChestClick = { chestId -> viewModel.openChest(chestId) },
                        onNpcClick = { npcId -> viewModel.talkToNpc(npcId) }
                    )

                    // Combat & Exploration HUD
                    CombatHud(
                        engine = viewModel.engine,
                        currentChapter = currentChapter,
                        activeQuest = activeQuest,
                        onOpenCharacter = { viewModel.setScreen(AppScreen.CHARACTER) },
                        onOpenInventory = { viewModel.setScreen(AppScreen.INVENTORY) },
                        onOpenBonds = { viewModel.setScreen(AppScreen.BOND) },
                        onOpenWorldMap = { viewModel.setScreen(AppScreen.WORLD_MAP) },
                        onOpenSettings = { viewModel.setScreen(AppScreen.SETTINGS) },
                        onTriggerPurify = { viewModel.triggerPurifyBoss() },
                        onLinkSkillMessage = { msg -> viewModel.showBanner("🤝 $msg") }
                    )
                }

                AppScreen.CHARACTER -> {
                    CharacterScreen(
                        engine = viewModel.engine,
                        onClose = { viewModel.setScreen(AppScreen.COMBAT_EXPLORATION) }
                    )
                }

                AppScreen.INVENTORY -> {
                    InventoryScreen(
                        engine = viewModel.engine,
                        inventory = viewModel.inventory,
                        onClose = { viewModel.setScreen(AppScreen.COMBAT_EXPLORATION) }
                    )
                }

                AppScreen.BOND -> {
                    BondScreen(
                        engine = viewModel.engine,
                        inventory = viewModel.inventory,
                        onClose = { viewModel.setScreen(AppScreen.COMBAT_EXPLORATION) }
                    )
                }

                AppScreen.WORLD_MAP -> {
                    WorldMapScreen(
                        engine = viewModel.engine,
                        currentChapter = currentChapter,
                        onClose = { viewModel.setScreen(AppScreen.COMBAT_EXPLORATION) }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        onSaveGame = { viewModel.saveGame() },
                        onLoadGame = { viewModel.loadGame() },
                        onShowTutorial = { viewModel.setShowTutorial(true) },
                        onClose = { viewModel.setScreen(AppScreen.COMBAT_EXPLORATION) }
                    )
                }
            }

            // Dialogue Overlay
            if (activeDialogue != null) {
                DialogueOverlay(
                    node = activeDialogue!!,
                    onFinish = { viewModel.dismissDialogue() },
                    onChoiceSelected = { choice -> viewModel.selectDialogueChoice(choice) }
                )
            }

            // Notification Banner (Top Center)
            AnimatedVisibility(
                visible = bannerMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            ) {
                bannerMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xF01A233A))
                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }

            // Tutorial Modal
            if (showTutorial) {
                TutorialModal(onClose = { viewModel.setShowTutorial(false) })
            }

            // Lore Modal
            if (showLore) {
                LoreModal(onClose = { viewModel.setShowLore(false) })
            }
        }
    }
}
