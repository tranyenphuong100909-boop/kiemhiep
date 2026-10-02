package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
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

data class RealmInfo(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val elements: String,
    val isUnlocked: Boolean,
    val bossName: String
)

@Composable
fun WorldMapScreen(
    engine: GameEngine,
    currentChapter: Int,
    onClose: () -> Unit
) {
    val realms = remember(currentChapter) {
        listOf(
            RealmInfo(
                "thanh_hoa_coc",
                "1. Thanh Hoa Cốc",
                "🌸",
                "Thung lũng hoa ngập tràn ánh nắng và suối ngọc. Nơi bắt đầu hành trình của huynh đệ.",
                "Mộc / Phong / Lôi",
                true,
                "Thanh Lân Long"
            ),
            RealmInfo(
                "van_thanh",
                "2. Vân Thành",
                "🏯",
                "Thành cổ kiếm hiệp với môn phái ngàn năm, chợ sầm uất và võ đài tranh tài.",
                "Kim / Hỏa",
                currentChapter >= 3,
                "Ma Kiếm Tướng"
            ),
            RealmInfo(
                "thuy_nguyet_ho",
                "3. Thủy Nguyệt Hồ",
                "🌊",
                "Hồ nước huyền bí phản chiếu ánh trăng, chứa di tích cổ ngầm dưới đáy nước.",
                "Thủy / Băng",
                currentChapter >= 4,
                "Bích Thủy Huyền Quy"
            ),
            RealmInfo(
                "bach_son",
                "4. Bạch Sơn",
                "❄️",
                "Đỉnh núi tuyết vĩnh cửu băng giá, nơi cất giữ một mảnh vỡ Thanh Thiên Kiếm.",
                "Băng / Phong",
                currentChapter >= 5,
                "Hàn Băng Thần Điểu"
            ),
            RealmInfo(
                "co_moc_lam",
                "5. Cổ Mộc Lâm",
                "🌲",
                "Rừng nguyên sinh ngàn tuổi với cây thần khổng lồ và linh thú cổ xưa.",
                "Mộc / Độc",
                currentChapter >= 6,
                "Cổ Thụ Thụ Ma"
            ),
            RealmInfo(
                "thien_van_dai",
                "6. Thiên Vân Đài",
                "☁️",
                "Cung điện nổi giữa tầng mây bồng bềnh, tương truyền là nơi tiên nhân truyền kiếm pháp.",
                "Quang / Lôi",
                currentChapter >= 7,
                "Vân Tiêu Thánh Kiếm"
            ),
            RealmInfo(
                "ma_vuc",
                "7. Ma Vực",
                "🌑",
                "Vùng đất hắc ám phong ấn tà ma đại chiến ngàn năm. Trận chiến cuối cùng bảo vệ thế giới.",
                "Hắc Ám / Viêm Diễm",
                currentChapter >= 9,
                "Ma Vực Tôn Chủ"
            )
        )
    }

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
                Column {
                    Text(
                        text = "🗺️ BẢN ĐỒ THẾ GIỚI - LỤC GIỚI MỘNG HOA",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Text(
                        text = "7 Vùng Đất Chứa 7 Mảnh Thanh Thiên Kiếm",
                        fontSize = 11.sp,
                        color = Color(0xFFA0AEC0)
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.testTag("close_map_screen")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(realms) { realm ->
                    val isCurrent = realm.id == "thanh_hoa_coc"
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (realm.isUnlocked) Color(0xFF162032) else Color(0xFF10141E)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isCurrent) Color(0xFF00E5FF) else if (realm.isUnlocked) Color(0xFF2C3954) else Color(0xFF1F2430)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = realm.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = realm.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (realm.isUnlocked) Color.White else Color(0xFF718096)
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "[Hiện tại]",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00E5FF)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = realm.description,
                                    fontSize = 11.sp,
                                    color = if (realm.isUnlocked) Color(0xFFA0AEC0) else Color(0xFF4A5568)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Thuộc tính: ${realm.elements} | Thủ hộ giả: ${realm.bossName}",
                                    fontSize = 10.sp,
                                    color = Color(0xFFFFD54F)
                                )
                            }

                            if (!realm.isUnlocked) {
                                Text(
                                    text = "🔒 Khóa",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF5252),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
