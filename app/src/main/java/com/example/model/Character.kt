package com.example.model

enum class Element(val displayName: String, val colorHex: Long, val symbol: String) {
    NONE("Vô Thuộc Tính", 0xFFCCCCCC, "⚔️"),
    WIND("Phong", 0xFF64FFDA, "🌪️"),
    FIRE("Hỏa", 0xFFFF5252, "🔥"),
    WOOD("Mộc", 0xFF69F0AE, "🌿"),
    ICE("Băng", 0xFF80D8FF, "❄️"),
    WATER("Thủy", 0xFF40C4FF, "💧"),
    LIGHTNING("Lôi", 0xFFFFD700, "⚡"),
    DARK("Hắc Ám", 0xFF7C4DFF, "🌑")
}

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val cooldownSeconds: Float,
    val energyCost: Float,
    val damageMultiplier: Float,
    val element: Element,
    val icon: String,
    val isUltimate: Boolean = false,
    val isLinkSkill: Boolean = false,
    var currentCooldown: Float = 0f
)

enum class BondLevel(val level: Int, val title: String, val requiredPoints: Int, val statBonusPct: Int) {
    STRANGER(1, "Người Lạ", 0, 0),
    TEAMMATE(2, "Đồng Đội", 100, 5),
    FRIEND(3, "Bạn Bè", 250, 10),
    BROTHER(4, "Huynh Đệ", 500, 15),
    SWORN_BROTHER(5, "Sinh Tử Chi Giao", 1000, 25);

    companion object {
        fun fromPoints(points: Int): BondLevel {
            return when {
                points >= 1000 -> SWORN_BROTHER
                points >= 500 -> BROTHER
                points >= 250 -> FRIEND
                points >= 100 -> TEAMMATE
                else -> STRANGER
            }
        }
    }
}

data class CharacterBond(
    val targetHeroId: String,
    var points: Int = 120
) {
    val level: BondLevel get() = BondLevel.fromPoints(points)
}

data class Hero(
    val id: String,
    val name: String,
    val title: String,
    val age: Int,
    val role: String,
    val weaponType: String,
    val element: Element,
    val personality: String,
    var level: Int = 1,
    var exp: Int = 0,
    var maxExp: Int = 100,
    var maxHp: Float = 500f,
    var currentHp: Float = 500f,
    var maxEnergy: Float = 100f,
    var currentEnergy: Float = 50f,
    var baseAtk: Float = 45f,
    var baseDef: Float = 20f,
    var critRate: Float = 0.15f,
    var speed: Float = 220f,
    val skills: List<Skill>,
    val bonds: MutableMap<String, CharacterBond> = mutableMapOf(),
    var isUnlocked: Boolean = true,
    var avatarColor: Long = 0xFF00E5FF,
    // Skill tree points
    var skillPoints: Int = 2,
    var upgradedSkill1: Int = 0,
    var upgradedSkill2: Int = 0,
    var upgradedUlt: Int = 0
) {
    val atk: Float get() = baseAtk + (level - 1) * 8f + upgradedSkill1 * 5f
    val def: Float get() = baseDef + (level - 1) * 4f + upgradedSkill2 * 3f
    val totalMaxHp: Float get() = maxHp + (level - 1) * 50f + upgradedUlt * 40f

    fun gainExp(amount: Int): Boolean {
        exp += amount
        var leveledUp = false
        while (exp >= maxExp) {
            exp -= maxExp
            level++
            maxExp = (maxExp * 1.4f).toInt()
            skillPoints++
            currentHp = totalMaxHp
            leveledUp = true
        }
        return leveledUp
    }
}
