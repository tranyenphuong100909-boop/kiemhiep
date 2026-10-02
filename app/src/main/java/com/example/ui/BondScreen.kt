package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
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
import com.example.engine.GameEngine
import com.example.engine.SoundSystem
import com.example.model.BondLevel
import com.example.model.Inventory

@Composable
fun BondScreen(
    engine: GameEngine,
    inventory: Inventory,
    onClose: () -> Unit
) {
    val activeHero = engine.activeHero
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF00B0F19))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🤝 TÌNH ANH EM & HUYNH ĐỆ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Text(
                        text = "Gắn kết cùng ${activeHero.name} - Mở khóa Kỹ Năng Huynh Đệ",
                        fontSize = 11.sp,
                        color = Color(0xFFA0AEC0)
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.testTag("close_bond_screen")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                }
            }

            if (feedbackMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = feedbackMessage!!,
                    fontSize = 12.sp,
                    color = Color(0xFFFF69B4),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Link skill preview banner
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E283D)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "✨ KỸ NĂNG HUYNH ĐỆ ĐẶC BIỆT",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFFFD54F)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Lâm Vân + Xích Long: \"HUYNH ĐỆ ĐỒNG TÂM\" (Hỏa Phong Song Trảm)\n• Mộc Linh + Bạch Nguyệt: \"BĂNG HOA VĨNH HẰNG\" (Băng Trận Hồi Máu)\n• Khi ra trận cùng nhau, mọi chỉ số công thủ đều tăng theo cấp bậc Huynh Đệ!",
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // List of brothers/sisters in party
                items(engine.heroes.size) { idx ->
                    val otherHero = engine.heroes[idx]
                    if (otherHero.id != activeHero.id) {
                        val bond = activeHero.bonds.getOrPut(otherHero.id) {
                            com.example.model.CharacterBond(otherHero.id, 120)
                        }

                        BondPartnerCard(
                            hero = activeHero,
                            partner = otherHero,
                            points = bond.points,
                            onSendGift = {
                                if (inventory.gold >= 50) {
                                    inventory.gold -= 50
                                    bond.points += 60
                                    otherHero.bonds[activeHero.id]?.let { it.points += 60 }
                                    SoundSystem.playHealChime()
                                    feedbackMessage = "Đã tặng Huynh Đệ Linh Trà cho ${otherHero.name}! (+60 Điểm Huynh Đệ)"
                                } else {
                                    feedbackMessage = "Cần 50 Vàng để mua Linh Trà tặng huynh đệ!"
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BondPartnerCard(
    hero: com.example.model.Hero,
    partner: com.example.model.Hero,
    points: Int,
    onSendGift: () -> Unit
) {
    val bondLevel = BondLevel.fromPoints(points)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(partner.element.colorHex))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(partner.avatarColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = partner.element.symbol, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${partner.name} (${partner.role})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFFF4081),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Cấp ${bondLevel.level}: ${bondLevel.title} (+${bondLevel.statBonusPct}% Công Thủ)",
                                fontSize = 11.sp,
                                color = Color(0xFFFF80AB),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Gift Button
                Button(
                    onClick = onSendGift,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("gift_button_${partner.id}")
                ) {
                    Text(text = "🎁 Tặng Trà (50G)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bond Progress Bar
            val targetPoints = when (bondLevel) {
                BondLevel.STRANGER -> 100
                BondLevel.TEAMMATE -> 250
                BondLevel.FRIEND -> 500
                BondLevel.BROTHER -> 1000
                BondLevel.SWORN_BROTHER -> 1000
            }
            val progress = (points.toFloat() / targetPoints.toFloat()).coerceIn(0f, 1f)

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Độ Thân Thiết", fontSize = 10.sp, color = Color(0xFFA0AEC0))
                    Text(text = "$points / $targetPoints", fontSize = 10.sp, color = Color(0xFFFFD54F))
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFFF4081),
                    trackColor = Color(0xFF263238)
                )
            }
        }
    }
}
