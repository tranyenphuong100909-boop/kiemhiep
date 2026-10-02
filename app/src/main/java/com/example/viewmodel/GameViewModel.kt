package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SaveManager
import com.example.engine.GameEngine
import com.example.engine.SoundSystem
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
    MAIN_MENU,
    COMBAT_EXPLORATION,
    CHARACTER,
    INVENTORY,
    BOND,
    WORLD_MAP,
    SETTINGS
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val engine = GameEngine()
    val inventory = Inventory()
    private val saveManager = SaveManager(application)

    private val _currentScreen = MutableStateFlow(AppScreen.MAIN_MENU)
    val currentScreen: StateFlow<AppScreen> = _currentScreen

    private val _currentChapter = MutableStateFlow(1)
    val currentChapter: StateFlow<Int> = _currentChapter

    private val _activeQuest = MutableStateFlow<Quest?>(null)
    val activeQuest: StateFlow<Quest?> = _activeQuest

    private val _activeDialogue = MutableStateFlow<DialogueNode?>(null)
    val activeDialogue: StateFlow<DialogueNode?> = _activeDialogue

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage

    private val _showTutorial = MutableStateFlow(false)
    val showTutorial: StateFlow<Boolean> = _showTutorial

    private val _showLore = MutableStateFlow(false)
    val showLore: StateFlow<Boolean> = _showLore

    val spiritPets = mutableListOf(
        SpiritPet(
            id = "pet_thanh_lan_long",
            name = "Thanh Lân Long",
            title = "Linh Thú Hộ Thần",
            description = "Rồng thần bảo hộ của Thanh Hoa Cốc được giải thoát ma khí.",
            element = Element.LIGHTNING,
            icon = "🐉",
            isUnlocked = false,
            atkBonusPct = 25,
            hpBonusPct = 20,
            assistSkillName = "Thanh Lân Lôi Giáng",
            assistSkillDesc = "Sấm sét giáng xuống làm choáng kẻ địch."
        ),
        SpiritPet(
            id = "pet_bach_tuyet_ho",
            name = "Bạch Tuyết Hồ",
            title = "Hồ Ly Tuyết Sơn",
            description = "Linh thú thông tuệ tăng bạo kích và tốc độ né tránh.",
            element = Element.ICE,
            icon = "🦊",
            isUnlocked = false,
            atkBonusPct = 15,
            hpBonusPct = 10,
            assistSkillName = "Tuyết Ẩn",
            assistSkillDesc = "Tàng hình và tăng 50% bạo kích."
        )
    )

    private var gameLoopJob: Job? = null

    init {
        setupInitialInventory()
        setupCallbacks()
    }

    private fun setupInitialInventory() {
        inventory.items.clear()
        inventory.items.add(
            GameItem(
                id = "sword_novice",
                name = "Thanh Trúc Kiếm",
                description = "Kiếm tre tôi luyện của đệ tử sơ nhập giang hồ.",
                type = ItemType.WEAPON,
                rarity = ItemRarity.COMMON,
                icon = "🗡️",
                atkBonus = 15f
            )
        )
        inventory.items.add(
            GameItem(
                id = "armor_novice",
                name = "Vân Sam Y",
                description = "Áo lụa thanh tao dệt từ tơ tằm Thanh Hoa Cốc.",
                type = ItemType.ARMOR,
                rarity = ItemRarity.COMMON,
                icon = "🥋",
                defBonus = 10f,
                hpBonus = 80f
            )
        )
        inventory.items.add(
            GameItem(
                id = "potion_hp",
                name = "Linh Chi Dược Đan",
                description = "Đan dược ngưng tụ từ linh chi ngàn năm, lập tức hồi 250 HP.",
                type = ItemType.CONSUMABLE,
                rarity = ItemRarity.RARE,
                icon = "🧪",
                healAmount = 250f,
                count = 5
            )
        )
        inventory.items.add(
            GameItem(
                id = "gift_tea",
                name = "Huynh Đệ Linh Trà",
                description = "Trà quý ủ từ sương sớm, trao tặng giúp tăng 60 điểm Huynh Đệ.",
                type = ItemType.GIFT,
                rarity = ItemRarity.EPIC,
                icon = "🍵",
                bondBonus = 60,
                count = 3
            )
        )
    }

    private fun setupCallbacks() {
        engine.onBossPurified = { pet ->
            spiritPets.find { it.id == pet.id }?.isUnlocked = true
            showBanner("🐉 ĐÃ TỊNH HÓA THANH LÂN LONG! Nhận Linh Thú Đồng Hành!")
            SoundSystem.playLevelUp()
            // Progress Quest
            _activeQuest.value?.let { q ->
                if (q.targetType == "PURIFY_BOSS") {
                    q.currentCount++
                    completeQuest(q)
                }
            }
            saveGame()
        }

        engine.onEnemyKilled = { enemy ->
            inventory.gold += enemy.type.goldReward
            _activeQuest.value?.let { q ->
                if (q.targetType == "KILL_ENEMIES" && q.status == QuestStatus.IN_PROGRESS) {
                    q.currentCount++
                    if (q.currentCount >= q.targetCount) {
                        completeQuest(q)
                    }
                }
            }
        }

        engine.onHeroLevelUp = { hero ->
            showBanner("✨ ${hero.name} ĐỘT PHÁ CẤP ĐỘ Lv.${hero.level}!")
            SoundSystem.playLevelUp()
            saveGame()
        }
    }

    fun hasSaveData(): Boolean = saveManager.hasSaveData()

    fun startNewGame() {
        _currentChapter.value = 1
        startChapter1()
        _currentScreen.value = AppScreen.COMBAT_EXPLORATION
        startGameLoop()
        SoundSystem.startBgm()
    }

    fun continueGame() {
        val (chapter, _) = saveManager.loadGame(engine, inventory, spiritPets)
        _currentChapter.value = chapter
        if (_activeQuest.value == null) {
            setupChapterQuest(chapter)
        }
        _currentScreen.value = AppScreen.COMBAT_EXPLORATION
        startGameLoop()
        SoundSystem.startBgm()
        showBanner("Đã tiếp tục hành trình Chương $chapter!")
    }

    private fun startChapter1() {
        val quest = Quest(
            id = "quest_ch1",
            chapter = 1,
            title = "Ngày Chúng Ta Gặp Nhau",
            description = "Trừ khử 3 Ma Thú quấy nhiễu thôn làng tại Thanh Hoa Cốc.",
            targetType = "KILL_ENEMIES",
            targetCount = 3,
            goldReward = 150,
            expReward = 200,
            spiritStonesReward = 5,
            status = QuestStatus.IN_PROGRESS
        )
        _activeQuest.value = quest

        // Opening Dialogue
        val introNode = DialogueNode(
            id = "dlg_intro",
            lines = listOf(
                DialogueLine("Lâm Vân", 0xFF00E5FF, "Thanh Hoa Cốc vốn thanh tịnh ngàn năm, tại sao ma khí từ Ma Vực lại tràn lan tới đây?"),
                DialogueLine("Xích Long", 0xFFFF5722, "Lâm Vân huynh! Có đệ ở đây thì dẫu có là rồng thiêng bị ma hóa cũng chẳng phải sợ! Chúng ta cùng dọn sạch lũ ma thú này!"),
                DialogueLine("Mộc Linh", 0xFF69F0AE, "Hai huynh nhớ cẩn thận, linh khí quanh đây đang bị vẩn đục, thần rồng Thanh Lân Long dường như đã thức tỉnh..."),
                DialogueLine("Bạch Nguyệt", 0xFF80D8FF, "Thanh Thiên Kiếm vỡ... bóng tối sẽ không dừng lại ở thung lũng này. Rút kiếm đi.")
            ),
            choices = listOf(
                DialogueChoice("Vì tình huynh đệ và bá tánh, ta quyết không lùi bước!", bondHeroId = "hero_xich_long", bondBonus = 30),
                DialogueChoice("Mọi người cùng kề vai sát cánh, tương trợ lẫn nhau!", bondHeroId = "hero_moc_linh", bondBonus = 30)
            )
        )
        _activeDialogue.value = introNode
    }

    private fun setupChapterQuest(chapter: Int) {
        val quest = when (chapter) {
            1 -> Quest("quest_ch1", 1, "Ngày Chúng Ta Gặp Nhau", "Trừ khử 3 Ma Thú quấy nhiễu thôn làng.", "KILL_ENEMIES", 3, 150, 200, 5, status = QuestStatus.IN_PROGRESS)
            2 -> Quest("quest_ch2", 2, "Bóng Tối Dưới Thanh Hoa Cốc", "Tiêu diệt 4 Ma Vực Kiếm Binh canh giữ đỉnh rồng.", "KILL_ENEMIES", 4, 250, 350, 8, status = QuestStatus.IN_PROGRESS)
            else -> Quest("quest_ch3", chapter, "Thanh Lân Long Thức Tỉnh", "Tiến tới Đỉnh Long và Tịnh Hóa Thần Long hộ mệnh!", "PURIFY_BOSS", 1, 500, 800, 20, status = QuestStatus.IN_PROGRESS)
        }
        _activeQuest.value = quest
    }

    private fun completeQuest(quest: Quest) {
        quest.status = QuestStatus.COMPLETED
        inventory.gold += quest.goldReward
        inventory.spiritStones += quest.spiritStonesReward
        engine.activeHero.gainExp(quest.expReward)

        SoundSystem.playLevelUp()
        showBanner("🎉 HOÀN THÀNH NHIỆM VỤ: ${quest.title} (+${quest.goldReward} Vàng, +${quest.spiritStonesReward} Linh Thạch)!")

        val nextChapter = quest.chapter + 1
        _currentChapter.value = nextChapter
        setupChapterQuest(nextChapter)
        saveGame()
    }

    fun openChest(chestId: String) {
        val chest = engine.currentArea.chests.find { it.id == chestId } ?: return
        if (chest.isOpened) return

        chest.isOpened = true
        inventory.gold += chest.rewardGold
        inventory.spiritStones += chest.rewardStones
        chest.rewardItem?.let { inventory.items.add(it) }

        SoundSystem.playChestOpen()
        showBanner("Đã mở Rương Kho Báu! (+${chest.rewardGold} Vàng, +${chest.rewardStones} Linh Thạch)!")
        saveGame()
    }

    fun talkToNpc(npcId: String) {
        SoundSystem.playButtonClick()
        when (npcId) {
            "npc_elder" -> {
                _activeDialogue.value = DialogueNode(
                    id = "dlg_elder",
                    lines = listOf(
                        DialogueLine("Trưởng Làng", 0xFF00E5FF, "Thiện tai! Lâm Vân, Xích Long, các con là niềm hy vọng của Thanh Hoa Cốc!"),
                        DialogueLine("Trưởng Làng", 0xFF00E5FF, "Thần thú Thanh Lân Long bị ma khí ăn mòn. Đừng giết người, hãy dùng sức mạnh huynh đệ chân chính để Tịnh Hóa rồng thần!")
                    ),
                    choices = listOf(
                        DialogueChoice("Chúng con xin thề sẽ giải cứu Thanh Lân Long!", bondHeroId = "hero_xich_long", bondBonus = 25)
                    )
                )
            }
            "npc_healer" -> {
                _activeDialogue.value = DialogueNode(
                    id = "dlg_healer",
                    lines = listOf(
                        DialogueLine("Bạch Tiên Cô", 0xFFFF69B4, "Linh dược của ta có thể chữa lành vết thương, nhưng chỉ có tình bằng hữu mới chữa lành được linh hồn rồng thần."),
                        DialogueLine("Mộc Linh", 0xFF69F0AE, "Tiên cô yên tâm, đan dược và linh lực Mộc của muội luôn sẵn sàng!")
                    )
                )
                // Heal team
                engine.heroes.forEach { it.currentHp = it.totalMaxHp }
                SoundSystem.playHealChime()
                showBanner("Bạch Tiên Cô đã hồi phục toàn bộ HP cho cả đội!")
            }
            "npc_blacksmith" -> {
                _activeDialogue.value = DialogueNode(
                    id = "dlg_blacksmith",
                    lines = listOf(
                        DialogueLine("Thiết Lão Bá", 0xFFFFB300, "Kiếm sắc bén đến mấy cũng cần tâm trí vững vàng. Hãy thu thập Linh Thạch để ta tôi luyện thần binh cho các ngươi!")
                    )
                )
            }
        }
    }

    fun dismissDialogue() {
        _activeDialogue.value = null
    }

    fun selectDialogueChoice(choice: DialogueChoice) {
        if (choice.bondHeroId != null && choice.bondBonus > 0) {
            val hero = engine.activeHero
            hero.bonds[choice.bondHeroId]?.let { it.points += choice.bondBonus }
            engine.heroes.find { it.id == choice.bondHeroId }?.bonds?.get(hero.id)?.let { it.points += choice.bondBonus }
            showBanner("❤️ Tăng ${choice.bondBonus} Điểm Huynh Đệ!")
        }
        _activeDialogue.value = null
    }

    fun triggerPurifyBoss() {
        val boss = engine.enemies.find { it.isBoss } ?: return
        engine.purifyBoss(boss)
    }

    fun setScreen(screen: AppScreen) {
        SoundSystem.playButtonClick()
        _currentScreen.value = screen
    }

    fun setShowTutorial(show: Boolean) {
        _showTutorial.value = show
    }

    fun setShowLore(show: Boolean) {
        _showLore.value = show
    }

    fun saveGame(): Boolean {
        return saveManager.saveGame(
            engine = engine,
            inventory = inventory,
            currentChapter = _currentChapter.value,
            activeQuest = _activeQuest.value,
            spiritPets = spiritPets
        )
    }

    fun loadGame(): Boolean {
        val (chapter, _) = saveManager.loadGame(engine, inventory, spiritPets)
        _currentChapter.value = chapter
        setupChapterQuest(chapter)
        return true
    }

    fun showBanner(message: String) {
        _bannerMessage.value = message
        viewModelScope.launch {
            delay(3500)
            if (_bannerMessage.value == message) {
                _bannerMessage.value = null
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastTime = System.currentTimeMillis()
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val dt = (currentTime - lastTime) / 1000f
                lastTime = currentTime
                engine.update(dt)
                delay(16) // ~60 FPS
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        SoundSystem.stopBgm()
    }
}
