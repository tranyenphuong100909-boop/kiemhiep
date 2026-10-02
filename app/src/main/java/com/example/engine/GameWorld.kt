package com.example.engine

import com.example.model.GameItem
import com.example.model.ItemRarity
import com.example.model.ItemType

data class WorldNpc(
    val id: String,
    val name: String,
    val title: String,
    val x: Float,
    val y: Float,
    val dialogueId: String,
    val iconColor: Long
)

data class TreasureChest(
    val id: String,
    val x: Float,
    val y: Float,
    var isOpened: Boolean = false,
    val rewardGold: Int = 100,
    val rewardStones: Int = 3,
    val rewardItem: GameItem? = null
)

data class WorldObstacle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val type: String // "TREE", "ROCK", "SHRINE", "BRIDGE"
)

data class WorldArea(
    val id: String,
    val name: String,
    val themeDesc: String,
    val width: Float = 2400f,
    val height: Float = 1800f,
    val bgHex: Long = 0xFF121B24,
    val npcs: List<WorldNpc>,
    val chests: MutableList<TreasureChest>,
    val obstacles: List<WorldObstacle>
)

object GameWorldData {
    fun createThanhHoaCoc(): WorldArea {
        val npcs = listOf(
            WorldNpc(
                id = "npc_elder",
                name = "Trưởng Làng Lạc Phong",
                title = "Thanh Hoa Cốc Túc Lão",
                x = 350f,
                y = 400f,
                dialogueId = "dlg_elder_welcome",
                iconColor = 0xFF00E5FF
            ),
            WorldNpc(
                id = "npc_healer",
                name = "Bạch Tiên Cô",
                title = "Dược Sư Mộng Hoa",
                x = 750f,
                y = 350f,
                dialogueId = "dlg_healer_talk",
                iconColor = 0xFFFF69B4
            ),
            WorldNpc(
                id = "npc_blacksmith",
                name = "Thiết Lão Bá",
                title = "Thần Binh Đệ Nhất Kiếm",
                x = 420f,
                y = 650f,
                dialogueId = "dlg_blacksmith_talk",
                iconColor = 0xFFFFB300
            )
        )

        val chests = mutableListOf(
            TreasureChest(
                id = "chest_1",
                x = 220f,
                y = 250f,
                rewardGold = 120,
                rewardStones = 5,
                rewardItem = GameItem(
                    id = "sword_azure",
                    name = "Thanh Mộc Kiếm",
                    description = "Kiếm gỗ cổ đượm kiếm khí ngàn năm, tăng tốc độ và sát thương chém.",
                    type = ItemType.WEAPON,
                    rarity = ItemRarity.RARE,
                    icon = "🗡️",
                    atkBonus = 25f
                )
            ),
            TreasureChest(
                id = "chest_2",
                x = 1250f,
                y = 320f,
                rewardGold = 200,
                rewardStones = 8,
                rewardItem = GameItem(
                    id = "amulet_brotherhood",
                    name = "Tâm Hữu Linh Tê Ngọc",
                    description = "Bội ngọc phát sáng khi các huynh đệ kề vai sát cánh.",
                    type = ItemType.ACCESSORY,
                    rarity = ItemRarity.EPIC,
                    icon = "📿",
                    hpBonus = 150f,
                    atkBonus = 18f
                )
            ),
            TreasureChest(
                id = "chest_3",
                x = 1850f,
                y = 1200f,
                rewardGold = 350,
                rewardStones = 12,
                rewardItem = GameItem(
                    id = "armor_dragon_scale",
                    name = "Thanh Lân Giáp",
                    description = "Giáp chế tạo từ vảy rồng ngàn năm, giảm sát thương ma khí.",
                    type = ItemType.ARMOR,
                    rarity = ItemRarity.LEGENDARY,
                    icon = "🛡️",
                    defBonus = 35f,
                    hpBonus = 250f
                )
            )
        )

        val obstacles = mutableListOf<WorldObstacle>()
        // Village boundaries & decorative blossom trees
        obstacles.add(WorldObstacle(150f, 150f, 65f, "TREE"))
        obstacles.add(WorldObstacle(550f, 180f, 60f, "TREE"))
        obstacles.add(WorldObstacle(280f, 550f, 50f, "ROCK"))
        obstacles.add(WorldObstacle(600f, 520f, 55f, "TREE"))
        obstacles.add(WorldObstacle(850f, 600f, 70f, "SHRINE"))
        obstacles.add(WorldObstacle(1100f, 450f, 50f, "TREE"))
        obstacles.add(WorldObstacle(1300f, 750f, 60f, "TREE"))
        obstacles.add(WorldObstacle(950f, 1100f, 80f, "ROCK"))
        obstacles.add(WorldObstacle(1400f, 1300f, 65f, "TREE"))
        // Dragon peak altar
        obstacles.add(WorldObstacle(1900f, 750f, 90f, "SHRINE"))

        return WorldArea(
            id = "thanh_hoa_coc",
            name = "Thanh Hoa Cốc",
            themeDesc = "Thung lũng hoa anh đào ngập tràn linh khí và tiếng suối róc rách",
            width = 2400f,
            height = 1800f,
            bgHex = 0xFF141F2B,
            npcs = npcs,
            chests = chests,
            obstacles = obstacles
        )
    }
}
