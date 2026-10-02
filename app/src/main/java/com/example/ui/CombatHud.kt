package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.model.EnemyState
import com.example.model.Quest
import kotlin.math.*

@Composable
fun CombatHud(
    engine: GameEngine,
    currentChapter: Int,
    activeQuest: Quest?,
    onOpenCharacter: () -> Unit,
    onOpenInventory: () -> Unit,
    onOpenBonds: () -> Unit,
    onOpenWorldMap: () -> Unit,
    onOpenSettings: () -> Unit,
    onTriggerPurify: () -> Unit,
    onLinkSkillMessage: (String) -> Unit
) {
    val activeHero = engine.activeHero
    val boss = engine.enemies.find { it.isBoss && it.state != EnemyState.DEAD }

    Box(modifier = Modifier.fillMaxSize()) {
        // TOP HUD: Player Info & Menu buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Player Hero Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xCC101726))
                    .border(1.5.dp, Color(activeHero.element.colorHex), RoundedCornerShape(14.dp))
                    .padding(8.dp)
            ) {
                // Avatar with level badge
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(activeHero.avatarColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeHero.element.symbol,
                            fontSize = 20.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFFFD54F))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Lv.${activeHero.level}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Name & HP / Energy Bars
                Column(modifier = Modifier.width(130.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = activeHero.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${activeHero.currentHp.toInt()}/${activeHero.totalMaxHp.toInt()}",
                            fontSize = 10.sp,
                            color = Color(0xFFA0AEC0)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // HP Bar
                    val hpPct = (activeHero.currentHp / activeHero.totalMaxHp).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { hpPct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF00E676),
                        trackColor = Color(0xFF263238),
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Energy Bar
                    val energyPct = (activeHero.currentEnergy / activeHero.maxEnergy).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { energyPct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFFFFD54F),
                        trackColor = Color(0xFF263238),
                    )
                }
            }

            // Top Menu Icons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                HudMenuIconButton(icon = Icons.Default.Person, label = "Nhân Vật", onClick = onOpenCharacter, testTag = "menu_character_btn")
                HudMenuIconButton(icon = Icons.Default.ShoppingBag, label = "Túi Đồ", onClick = onOpenInventory, testTag = "menu_inventory_btn")
                HudMenuIconButton(icon = Icons.Default.Favorite, label = "Huynh Đệ", onClick = onOpenBonds, testTag = "menu_bond_btn")
                HudMenuIconButton(icon = Icons.Default.Map, label = "Bản Đồ", onClick = onOpenWorldMap, testTag = "menu_map_btn")
                HudMenuIconButton(icon = Icons.Default.Settings, label = "Cài Đặt", onClick = onOpenSettings, testTag = "menu_settings_btn")
            }
        }

        // BOSS HEALTH BAR (Top Center)
        if (boss != null && boss.state != EnemyState.PURIFIED) {
            val distToBoss = sqrt(
                (engine.playerX - boss.x) * (engine.playerX - boss.x) +
                (engine.playerY - boss.y) * (engine.playerY - boss.y)
            )
            AnimatedVisibility(
                visible = distToBoss < 800f,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 66.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xDD121824))
                        .border(1.5.dp, Color(0xFFFF5252), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "👑 ${boss.type.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "Giai Đoạn ${boss.bossPhase}",
                            fontSize = 11.sp,
                            color = Color(0xFFFF8A80)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val bossHpPct = (boss.currentHp / boss.maxHp).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { bossHpPct },
                        modifier = Modifier
                            .width(220.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFF1744),
                        trackColor = Color(0xFF37474F)
                    )

                    // Purification button appears when HP <= 35%
                    if (boss.currentHp <= boss.maxHp * 0.35f && !boss.isPurified) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onTriggerPurify,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("purify_boss_btn")
                        ) {
                            Text(
                                text = "✨ TỊNH HÓA THẦN LONG ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // QUEST TRACKER OVERLAY (Left under player card)
        if (activeQuest != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xAA101626))
                    .border(1.dp, Color(0x66FFD54F), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column {
                    Text(
                        text = "📜 Chương $currentChapter: ${activeQuest.title}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Text(
                        text = activeQuest.description,
                        fontSize = 10.sp,
                        color = Color(0xFFE2E8F0)
                    )
                    if (activeQuest.targetCount > 1) {
                        Text(
                            text = "Tiến độ: ${activeQuest.currentCount}/${activeQuest.targetCount}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }
            }
        }

        // CHARACTER SWITCH BAR (Right side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            engine.heroes.forEachIndexed { index, hero ->
                val isActive = index == engine.activeHeroIndex
                val canSwap = !isActive && engine.swapCooldown <= 0f

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) Color(hero.element.colorHex) else Color(0xCC1A2133)
                        )
                        .border(
                            width = if (isActive) 2.5.dp else 1.dp,
                            color = if (isActive) Color.White else Color(hero.element.colorHex),
                            shape = CircleShape
                        )
                        .clickable(enabled = canSwap) {
                            engine.switchHero(index)
                        }
                        .testTag("swap_hero_${hero.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = hero.name.take(2).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.Black else Color.White
                        )
                        Text(
                            text = hero.element.symbol,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // BOTTOM-LEFT: VIRTUAL JOYSTICK
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
        ) {
            VirtualJoystick(
                onMove = { vx, vy ->
                    engine.setPlayerMovement(vx, vy)
                }
            )
        }

        // BOTTOM-RIGHT: COMBAT ACTION BUTTONS
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
        ) {
            CombatActionsCluster(
                engine = engine,
                onLinkSkill = {
                    val msg = engine.triggerBrotherhoodSkill()
                    if (msg != null) onLinkSkillMessage(msg)
                }
            )
        }
    }
}

@Composable
private fun HudMenuIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xCC182030))
            .border(1.dp, Color(0xFF334155), CircleShape)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun VirtualJoystick(
    onMove: (Float, Float) -> Unit
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadius = 55f

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color(0x4400E5FF))
            .border(2.dp, Color(0x8800E5FF), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset: Offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dragX = offset.x - center.x
                        val dragY = offset.y - center.y
                        val dist = sqrt(dragX * dragX + dragY * dragY)
                        val clampedX = if (dist > maxRadius) dragX * (maxRadius / dist) else dragX
                        val clampedY = if (dist > maxRadius) dragY * (maxRadius / dist) else dragY
                        knobOffset = Offset(clampedX, clampedY)
                        onMove(clampedX / maxRadius, clampedY / maxRadius)
                    },
                    onDrag = { change, dragAmount: Offset ->
                        change.consume()
                        val newX = knobOffset.x + dragAmount.x
                        val newY = knobOffset.y + dragAmount.y
                        val dist = sqrt(newX * newX + newY * newY)
                        val clampedX = if (dist > maxRadius) newX * (maxRadius / dist) else newX
                        val clampedY = if (dist > maxRadius) newY * (maxRadius / dist) else newY
                        knobOffset = Offset(clampedX, clampedY)
                        onMove(clampedX / maxRadius, clampedY / maxRadius)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Inner knob
        Box(
            modifier = Modifier
                .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF0091EA))
                    )
                )
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

@Composable
private fun CombatActionsCluster(
    engine: GameEngine,
    onLinkSkill: () -> Unit
) {
    val hero = engine.activeHero
    val s1 = hero.skills.getOrNull(0)
    val s2 = hero.skills.getOrNull(1)
    val ult = hero.skills.getOrNull(2)

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Normal Attack (Big button in center-right)
        Box(
            modifier = Modifier
                .offset(x = 45.dp, y = 35.dp)
                .size(68.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(hero.element.colorHex), Color(0xFF1E293B))
                    )
                )
                .border(2.dp, Color.White, CircleShape)
                .clickable {
                    engine.triggerNormalAttack()
                }
                .testTag("attack_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⚔️",
                    fontSize = 24.sp
                )
                Text(
                    text = "Trảm",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Skill 1 (Top-Right)
        if (s1 != null) {
            Box(
                modifier = Modifier
                    .offset(x = 45.dp, y = (-42).dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xEE1E293B))
                    .border(1.5.dp, Color(s1.element.colorHex), CircleShape)
                    .clickable(enabled = s1.currentCooldown <= 0f) {
                        engine.castSkill(0)
                    }
                    .testTag("skill1_button"),
                contentAlignment = Alignment.Center
            ) {
                if (s1.currentCooldown > 0f) {
                    Text(
                        text = "%.1f".format(s1.currentCooldown),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5252)
                    )
                } else {
                    Text(text = s1.icon, fontSize = 20.sp)
                }
            }
        }

        // Skill 2 (Top-Left)
        if (s2 != null) {
            Box(
                modifier = Modifier
                    .offset(x = (-20).dp, y = (-50).dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xEE1E293B))
                    .border(1.5.dp, Color(s2.element.colorHex), CircleShape)
                    .clickable(enabled = s2.currentCooldown <= 0f) {
                        engine.castSkill(1)
                    }
                    .testTag("skill2_button"),
                contentAlignment = Alignment.Center
            ) {
                if (s2.currentCooldown > 0f) {
                    Text(
                        text = "%.1f".format(s2.currentCooldown),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5252)
                    )
                } else {
                    Text(text = s2.icon, fontSize = 20.sp)
                }
            }
        }

        // Ultimate Burst (Far-Left)
        if (ult != null) {
            val ultReady = hero.currentEnergy >= ult.energyCost && ult.currentCooldown <= 0f
            Box(
                modifier = Modifier
                    .offset(x = (-68).dp, y = (-5).dp)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (ultReady) Brush.radialGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF6D00)))
                        else Brush.radialGradient(listOf(Color(0xFF37474F), Color(0xFF212121)))
                    )
                    .border(
                        width = if (ultReady) 2.5.dp else 1.dp,
                        color = if (ultReady) Color.White else Color(0xFF546E7A),
                        shape = CircleShape
                    )
                    .clickable(enabled = ultReady) {
                        engine.castSkill(2)
                    }
                    .testTag("ultimate_button"),
                contentAlignment = Alignment.Center
            ) {
                if (ult.currentCooldown > 0f) {
                    Text(
                        text = "%.0f".format(ult.currentCooldown),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💥", fontSize = 18.sp)
                        Text(
                            text = "Tuyệt Kỹ",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Dodge / Lướt (Bottom-Left)
        Box(
            modifier = Modifier
                .offset(x = (-35).dp, y = 48.dp)
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xDD212A3E))
                .border(1.5.dp, Color(0xFF00E5FF), CircleShape)
                .clickable {
                    engine.triggerDodge()
                }
                .testTag("dodge_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💨", fontSize = 18.sp)
        }

        // Brotherhood Link Skill (Top-Center badge)
        Box(
            modifier = Modifier
                .offset(x = 10.dp, y = (-85).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFF5722), Color(0xFFFFD54F))
                    )
                )
                .clickable {
                    onLinkSkill()
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("link_skill_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🤝 ", fontSize = 11.sp)
                Text(
                    text = "ĐỒNG TÂM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
