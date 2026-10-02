package com.example.model

enum class ItemType {
    WEAPON, ARMOR, ACCESSORY, CONSUMABLE, MATERIAL, GIFT
}

enum class ItemRarity(val label: String, val colorHex: Long) {
    COMMON("Phổ Thông", 0xFFB0BEC5),
    RARE("Hiếm", 0xFF42A5F5),
    EPIC("Sử Thi", 0xFFAB47BC),
    LEGENDARY("Truyền Thuyết", 0xFFFFB300)
}

data class GameItem(
    val id: String,
    val name: String,
    val description: String,
    val type: ItemType,
    val rarity: ItemRarity,
    val icon: String,
    val hpBonus: Float = 0f,
    val atkBonus: Float = 0f,
    val defBonus: Float = 0f,
    val healAmount: Float = 0f,
    val bondBonus: Int = 0,
    var count: Int = 1,
    var enhanceLevel: Int = 0,
    val sellPrice: Int = 20
)

data class Inventory(
    var gold: Int = 300,
    var spiritStones: Int = 15,
    val items: MutableList<GameItem> = mutableListOf()
)
