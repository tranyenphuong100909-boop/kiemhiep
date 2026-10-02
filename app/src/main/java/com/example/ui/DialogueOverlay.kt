package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SoundSystem
import com.example.model.DialogueChoice
import com.example.model.DialogueNode

@Composable
fun DialogueOverlay(
    node: DialogueNode,
    onFinish: () -> Unit,
    onChoiceSelected: (DialogueChoice) -> Unit
) {
    var lineIndex by remember(node.id) { mutableIntStateOf(0) }
    val currentLine = node.lines.getOrNull(lineIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x77000000))
            .clickable {
                if (lineIndex < node.lines.size - 1) {
                    SoundSystem.playButtonClick()
                    lineIndex++
                } else if (node.choices.isEmpty()) {
                    SoundSystem.playButtonClick()
                    onFinish()
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        if (currentLine != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xF0121927))
                    .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                // Header: Avatar & Speaker Name & Emotion
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(currentLine.avatarColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentLine.speaker.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = currentLine.speaker,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "[${currentLine.emotion}]",
                                fontSize = 11.sp,
                                color = Color(0xFF80D8FF)
                            )
                        }
                    }

                    // Skip button
                    TextButton(
                        onClick = onFinish,
                        modifier = Modifier.testTag("dialogue_skip_btn")
                    ) {
                        Text(text = "Bỏ qua >>", fontSize = 12.sp, color = Color(0xFFA0AEC0))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dialogue Content
                Text(
                    text = currentLine.text,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // If on last line and there are choices, show choice buttons
                if (lineIndex == node.lines.size - 1 && node.choices.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        node.choices.forEachIndexed { idx, choice ->
                            Button(
                                onClick = {
                                    SoundSystem.playButtonClick()
                                    onChoiceSelected(choice)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dialogue_choice_$idx"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = choice.text,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                    if (choice.bondBonus > 0) {
                                        Text(
                                            text = "+${choice.bondBonus} ❤️ Huynh Đệ",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFF69B4)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Chạm để tiếp tục ▾",
                            fontSize = 11.sp,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }
            }
        }
    }
}
