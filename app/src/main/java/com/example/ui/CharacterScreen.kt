package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.engine.GameEngine
import com.example.engine.SoundSystem
import com.example.model.Hero

@Composable
fun CharacterScreen(
    engine: GameEngine,
    onClose: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(engine.activeHeroIndex) }
    val hero = engine.heroes.getOrElse(selectedIndex) { engine.activeHero }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF00B0F19))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚔️ NHÂN VẬT & TU LUYỆN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                IconButton(onClick = onClose, modifier = Modifier.testTag("close_character_screen")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hero Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                engine.heroes.forEachIndexed { idx, h ->
                    val isSelected = idx == selectedIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(h.element.colorHex) else Color(0xFF1E2638))
                            .clickable {
                                SoundSystem.playButtonClick()
                                selectedIndex = idx
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = h.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hero Overview Card
                item {
                    HeroOverviewCard(hero)
                }

                // Skill Tree & Upgrades
                item {
                    SkillTreeSection(hero = hero, onUpgradeSkill = { skillIdx ->
                        if (hero.skillPoints > 0) {
                            hero.skillPoints--
                            when (skillIdx) {
                                0 -> hero.upgradedSkill1++
                                1 -> hero.upgradedSkill2++
                                2 -> hero.upgradedUlt++
                            }
                            SoundSystem.playLevelUp()
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun HeroOverviewCard(hero: Hero) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(hero.element.colorHex))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(hero.avatarColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = hero.element.symbol, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${hero.name} - ${hero.title}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Hệ: ${hero.element.displayName} | Tuổi: ${hero.age} | ${hero.role}",
                        fontSize = 11.sp,
                        color = Color(0xFFA0AEC0)
                    )
                    Text(
                        text = "Vũ khí: ${hero.weaponType}",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "\"${hero.personality}\"",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFF2D3748))
            Spacer(modifier = Modifier.height(10.dp))

            // Stat Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatBadge("Cấp độ", "Lv.${hero.level}")
                StatBadge("Sinh Lực", "${hero.currentHp.toInt()}/${hero.totalMaxHp.toInt()}")
                StatBadge("Tấn Công", "${hero.atk.toInt()}")
                StatBadge("Phòng Ngự", "${hero.def.toInt()}")
                StatBadge("Bạo Kích", "${(hero.critRate * 100).toInt()}%")
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFFA0AEC0))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
    }
}

@Composable
private fun SkillTreeSection(hero: Hero, onUpgradeSkill: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3954))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌱 CÂY KỸ NĂNG & ĐỘT PHÁ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFFFD54F)
                )
                Text(
                    text = "Điểm kỹ năng: ${hero.skillPoints}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF00E676)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            hero.skills.forEachIndexed { idx, skill ->
                val upgradeCount = when (idx) {
                    0 -> hero.upgradedSkill1
                    1 -> hero.upgradedSkill2
                    else -> hero.upgradedUlt
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1F293D))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = skill.icon, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${skill.name} (+${upgradeCount})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = skill.description,
                                fontSize = 10.sp,
                                color = Color(0xFFA0AEC0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onUpgradeSkill(idx) },
                        enabled = hero.skillPoints > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Nâng cấp", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Tăng", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
