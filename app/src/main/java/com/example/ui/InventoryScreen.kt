package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.engine.GameEngine
import com.example.engine.SoundSystem
import com.example.model.GameItem
import com.example.model.Inventory
import com.example.model.ItemType

@Composable
fun InventoryScreen(
    engine: GameEngine,
    inventory: Inventory,
    onClose: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<GameItem?>(inventory.items.firstOrNull()) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF00B0F19))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Currencies & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "🎒 HÀNH TRANG",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙 ${inventory.gold} Vàng", fontSize = 12.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "💎 ${inventory.spiritStones} Linh Thạch", fontSize = 12.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.testTag("close_inventory_screen")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                }
            }

            if (actionMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = actionMessage!!,
                    fontSize = 12.sp,
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two-pane layout: Item list on left / top, Details on right / bottom
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Item List
                LazyColumn(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inventory.items) { item ->
                        val isSelected = item.id == selectedItem?.id
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF1E2B45) else Color(0xFF161E2E)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(item.rarity.colorHex)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    SoundSystem.playButtonClick()
                                    selectedItem = item
                                }
                                .testTag("inv_item_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = item.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${item.name} ${if (item.enhanceLevel > 0) "+${item.enhanceLevel}" else ""}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${item.rarity.label} | SL: ${item.count}",
                                        fontSize = 10.sp,
                                        color = Color(item.rarity.colorHex)
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected Item Detail Card
                val item = selectedItem
                if (item != null) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3954)),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = item.icon, fontSize = 32.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = item.rarity.label,
                                            fontSize = 11.sp,
                                            color = Color(item.rarity.colorHex),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFFA0AEC0),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                if (item.atkBonus > 0f) {
                                    Text(text = "⚔️ Tấn Công: +${(item.atkBonus + item.enhanceLevel * 8).toInt()}", fontSize = 12.sp, color = Color(0xFF00E5FF))
                                }
                                if (item.defBonus > 0f) {
                                    Text(text = "🛡️ Phòng Ngự: +${(item.defBonus + item.enhanceLevel * 5).toInt()}", fontSize = 12.sp, color = Color(0xFF00E676))
                                }
                                if (item.hpBonus > 0f) {
                                    Text(text = "❤️ Sinh Lực: +${(item.hpBonus + item.enhanceLevel * 30).toInt()}", fontSize = 12.sp, color = Color(0xFFFF5252))
                                }
                            }

                            // Actions
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (item.type == ItemType.WEAPON || item.type == ItemType.ARMOR || item.type == ItemType.ACCESSORY) {
                                    val upgradeCostGold = 100 + item.enhanceLevel * 50
                                    val upgradeCostStones = 2 + item.enhanceLevel
                                    Button(
                                        onClick = {
                                            if (inventory.gold >= upgradeCostGold && inventory.spiritStones >= upgradeCostStones) {
                                                inventory.gold -= upgradeCostGold
                                                inventory.spiritStones -= upgradeCostStones
                                                item.enhanceLevel++
                                                SoundSystem.playLevelUp()
                                                actionMessage = "Đã cường hóa thành công ${item.name} lên +${item.enhanceLevel}!"
                                            } else {
                                                actionMessage = "Thiếu nguyên liệu! Cần ${upgradeCostGold} Vàng & ${upgradeCostStones} Linh Thạch."
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("upgrade_item_btn"),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(text = "⚡ Cường Hóa (+${item.enhanceLevel + 1})", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (item.type == ItemType.CONSUMABLE) {
                                    Button(
                                        onClick = {
                                            val heal = item.healAmount.coerceAtLeast(150f)
                                            engine.activeHero.currentHp = (engine.activeHero.currentHp + heal).coerceAtMost(engine.activeHero.totalMaxHp)
                                            SoundSystem.playHealChime()
                                            item.count--
                                            if (item.count <= 0) {
                                                inventory.items.remove(item)
                                                selectedItem = inventory.items.firstOrNull()
                                            }
                                            actionMessage = "Đã dùng đan dược hồi phục ${heal.toInt()} HP!"
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("use_item_btn"),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(text = "🧪 Sử Dụng Đan Dược", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
