package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.SoundSystem

@Composable
fun MainMenuScreen(
    hasSaveData: Boolean,
    onStartNewGame: () -> Unit,
    onContinueGame: () -> Unit,
    onShowTutorial: () -> Unit,
    onShowLore: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        // Hero visual banner background
        Image(
            painter = painterResource(id = R.drawable.img_hero_banner),
            contentDescription = "Thanh Hoa Cốc Banner",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay darkening bottom half
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x990B0F19),
                            Color(0xFF0B0F19)
                        ),
                        startY = 100f
                    )
                )
        )

        // Title and Menu Buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Emblem & Title
            Text(
                text = "KIẾM MỘNG",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD54F),
                letterSpacing = 3.sp
            )
            Text(
                text = "HÀNH TRÌNH HUYNH ĐỆ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "“Một thanh kiếm có thể chém đứt bóng tối, nhưng chỉ tình huynh đệ mới giúp con người vượt qua nó.”",
                fontSize = 12.sp,
                color = Color(0xFFE2E8F0),
                textAlign = TextAlign.Center,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Column(
                modifier = Modifier.widthIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (hasSaveData) {
                    Button(
                        onClick = {
                            SoundSystem.playButtonClick()
                            onContinueGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("continue_game_btn")
                    ) {
                        Text(
                            text = "TIẾP TỤC HÀNH TRÌNH 🗡️",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Button(
                    onClick = {
                        SoundSystem.playButtonClick()
                        onStartNewGame()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasSaveData) Color(0xFF1E293B) else Color(0xFF00E5FF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_new_game_btn")
                ) {
                    Text(
                        text = if (hasSaveData) "BẮT ĐẦU MỚI" else "BẮT ĐẦU HÀNH TRÌNH 🗡️",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasSaveData) Color.White else Color.Black
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            SoundSystem.playButtonClick()
                            onShowTutorial()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("menu_tutorial_btn")
                    ) {
                        Text(text = "Hướng Dẫn", fontSize = 12.sp, color = Color(0xFFFFD54F))
                    }

                    OutlinedButton(
                        onClick = {
                            SoundSystem.playButtonClick()
                            onShowLore()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("menu_lore_btn")
                    ) {
                        Text(text = "Cốt Truyện", fontSize = 12.sp, color = Color(0xFF80D8FF))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
