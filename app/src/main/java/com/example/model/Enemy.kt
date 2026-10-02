package com.example.model

enum class EnemyType(
    val title: String,
    val maxHp: Float,
    val atk: Float,
    val def: Float,
    val speed: Float,
    val expReward: Int,
    val goldReward: Int,
    val element: Element,
    val isBoss: Boolean = false
) {
    SHADOW_WOLF("Hắc Ảnh Lang", 280f, 25f, 10f, 160f, 35, 25, Element.DARK),
    PHANTOM_FLOWER("U Hồn Hoa", 220f, 30f, 5f, 80f, 40, 30, Element.WOOD),
    ABYSS_BLADE("Ma Vực Kiếm Sĩ", 450f, 40f, 25f, 130f, 75, 60, Element.DARK),
    THANH_LAN_LONG("Thanh Lân Long", 3200f, 70f, 40f, 110f, 600, 500, Element.LIGHTNING, isBoss = true)
}

enum class EnemyState {
    IDLE, CHASE, ATTACK, HURT, PURIFY_READY, PURIFIED, DEAD
}

data class Enemy(
    val id: Long,
    val type: EnemyType,
    var x: Float,
    var y: Float,
    var currentHp: Float = type.maxHp,
    var state: EnemyState = EnemyState.IDLE,
    var facingRight: Boolean = true,
    var currentElementAffliction: Element = Element.NONE,
    var afflictionTimer: Float = 0f,
    var isFrozen: Boolean = false,
    var frozenTimer: Float = 0f,
    var attackCooldown: Float = 0f,
    var stateTimer: Float = 0f,
    var bossPhase: Int = 1,
    var isPurified: Boolean = false
) {
    val maxHp: Float get() = type.maxHp
    val isBoss: Boolean get() = type.isBoss
    val atk: Float get() = type.atk
    val def: Float get() = type.def
}
