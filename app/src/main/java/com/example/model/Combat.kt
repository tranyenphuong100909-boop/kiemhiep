package com.example.model

enum class ReactionType(val reactionName: String, val bonusDesc: String, val colorHex: Long) {
    FIRESTORM("Bão Lửa! (Hỏa + Phong)", "+75% Sát thương bão lửa diện rộng", 0xFFFF5722),
    FROZEN("Đóng Băng! (Băng + Thủy)", "Bất động mục tiêu & Tăng 100% bạo kích", 0xFF00E5FF),
    MELT("Tan Chảy! (Hỏa + Băng)", "+120% Sát thương bạo liệt", 0xFFFF9100),
    ELECTROCUTE("Điện Giật! (Lôi + Thủy)", "Phóng điện lan sang kẻ địch lân cận", 0xFFFFD700),
    BLOOM("Sinh Trưởng! (Mộc + Thủy)", "Tạo mầm sinh lực hồi máu cho đội", 0xFF69F0AE),
    BURNING("Thiêu Đốt! (Hỏa + Mộc)", "Sát thương đốt cháy liên tục", 0xFFFF3D00)
}

data class DamageNumber(
    val id: Long,
    val x: Float,
    val y: Float,
    val value: Int,
    val isCrit: Boolean,
    val element: Element,
    val reactionText: String? = null,
    var lifetime: Float = 1.0f // seconds
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Long,
    val size: Float,
    var alpha: Float = 1f,
    var lifetime: Float = 0.8f,
    val shape: ParticleShape = ParticleShape.CIRCLE
)

enum class ParticleShape {
    CIRCLE, SAKURA_PETAL, SPARK, SLASH_ARC, LEAF, ICE_CRYSTAL, EMBER
}

data class Projectile(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float,
    val damage: Float,
    val element: Element,
    val fromPlayer: Boolean,
    val pierces: Boolean = false,
    var lifetime: Float = 2.0f,
    val colorHex: Long = 0xFF00E5FF
)

data class SlashEffect(
    val x: Float,
    val y: Float,
    val angle: Float,
    val radius: Float,
    val element: Element,
    var progress: Float = 0f,
    var duration: Float = 0.25f
)
