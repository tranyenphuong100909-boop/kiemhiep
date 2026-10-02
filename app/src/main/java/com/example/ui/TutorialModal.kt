package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TutorialModal(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xBB000000))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C2B)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "📖 HƯỚNG DẪN CHIẾN ĐẤU & KHÁM PHÁ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialRow("🕹️ Di Chuyển", "Dùng cần gạt ảo bên góc trái để di chuyển nhân vật trong thung lũng.")
                TutorialRow("⚔️ Đòn Đánh Thường", "Nhấn nút [Trảm] bên góc phải để thực hiện combo 3 đòn kiếm khí liên tục.")
                TutorialRow("💨 Lướt Né Tránh", "Nhấn nút lướt gió để né tránh đòn công kích và kích hoạt trạng thái bất tử tạm thời.")
                TutorialRow("🌪️ Kỹ Năng Nguyên Tố", "Mỗi nhân vật có 2 kỹ năng nguyên tố riêng biệt. Tạo phản ứng Bão Lửa (Hỏa+Phong), Đóng Băng (Băng+Thủy), Tan Chảy (Hỏa+Băng).")
                TutorialRow("💥 Tuyệt Kỹ", "Khi thanh Năng Lượng (vàng) đạt 100, kích hoạt chiêu thức chấn động màn hình.")
                TutorialRow("🤝 Kỹ Năng Huynh Đệ", "Nút [ĐỒNG TÂM] giải phóng chiêu thức kết hợp giữa các huynh đệ có mối quan hệ thân thiết!")
                TutorialRow("🔄 Đổi Nhân Vật", "Chạm vào biểu tượng 4 nhân vật bên cạnh phải để đổi tức thời giữa Lâm Vân, Xích Long, Mộc Linh, Bạch Nguyệt.")
                TutorialRow("🐉 Tịnh Hóa Boss", "Khi Boss Thanh Lân Long xuống dưới 35% HP, nhấn nút [TỊNH HÓA THẦN LONG] để giải thoát rồng thần biến thành Linh Thú!")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().testTag("close_tutorial_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Đã Hiểu, Bắt Đầu! 🗡️", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LoreModal(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xBB000000))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C2B)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD54F)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "📜 CỐT TRUYỆN: LỤC GIỚI MỘNG HOA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Hàng trăm năm trước, thần kiếm Thanh Thiên Kiếm đã trấn áp tà ma Ma Vực, bảo vệ Lục Giới. Sau đại chiến khốc liệt, thanh kiếm vỡ thành 7 mảnh thất lạc khắp cõi trần gian.\n\n" +
                            "Nay phong ấn suy yếu, bóng tối ma khí bắt đầu tràn vào Thanh Hoa Cốc, khiến thần thú hộ mệnh Thanh Lân Long bị tha hóa. \n\n" +
                            "Lâm Vân - kiếm khách thiếu niên chính trực cùng người huynh đệ nhiệt huyết Xích Long, y sư Mộc Linh dịu dàng và kiếm nữ Bạch Nguyệt thề cùng nhau tìm lại 7 mảnh kiếm, cứu lấy thế giới và bảo vệ tình huynh đệ keo sơn.",
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().testTag("close_lore_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Trở Về", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TutorialRow(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
        Text(text = desc, fontSize = 11.sp, color = Color(0xFFA0AEC0))
    }
}
