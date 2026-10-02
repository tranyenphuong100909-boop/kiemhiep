package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SoundSystem

@Composable
fun SettingsScreen(
    onSaveGame: () -> Unit,
    onLoadGame: () -> Unit,
    onShowTutorial: () -> Unit,
    onClose: () -> Unit
) {
    var isMuted by remember { mutableStateOf(SoundSystem.isAudioMuted()) }
    var graphicsQuality by remember { mutableStateOf("Cao") }
    var saveStatusMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF00B0F19))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚙️ CÀI ĐẶT HỆ THỐNG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                IconButton(onClick = onClose, modifier = Modifier.testTag("close_settings_screen")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                }
            }

            if (saveStatusMsg != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = saveStatusMsg!!, fontSize = 12.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sound Settings
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3954)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "🔊 ÂM THANH & NHẠC NỀN", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Âm lượng hiệu ứng & BGM", fontSize = 13.sp, color = Color(0xFFE2E8F0))
                        Switch(
                            checked = !isMuted,
                            onCheckedChange = { checked ->
                                isMuted = !checked
                                SoundSystem.setMuted(!checked)
                                if (checked) SoundSystem.startBgm()
                            },
                            modifier = Modifier.testTag("sound_toggle")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Graphics Quality
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3954)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "🎨 CHẤT LƯỢNG ĐỒ HỌA", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Thấp", "Trung", "Cao").forEach { q ->
                            val isSelected = graphicsQuality == q
                            Button(
                                onClick = {
                                    graphicsQuality = q
                                    SoundSystem.playButtonClick()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF00E5FF) else Color(0xFF1E283D)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = q,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Save & Load
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3954)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "💾 LƯU TRỮ TIẾN TRÌNH", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                onSaveGame()
                                SoundSystem.playLevelUp()
                                saveStatusMsg = "Đã lưu trò chơi thành công vào thiết bị!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("save_game_btn")
                        ) {
                            Text(text = "Lưu Game", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onLoadGame()
                                SoundSystem.playButtonClick()
                                saveStatusMsg = "Đã tải lại dữ liệu lưu thành công!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("load_game_btn")
                        ) {
                            Text(text = "Tải Game", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tutorial button
            OutlinedButton(
                onClick = onShowTutorial,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("show_tutorial_btn")
            ) {
                Text(text = "📖 Xem Lại Hướng Dẫn Tân Thủ", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
            }
        }
    }
}
