package com.example.model

enum class QuestStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED
}

data class DialogueLine(
    val speaker: String,
    val avatarColor: Long,
    val text: String,
    val emotion: String = "Bình tĩnh"
)

data class DialogueChoice(
    val text: String,
    val bondHeroId: String? = null,
    val bondBonus: Int = 0,
    val nextDialogueId: String? = null,
    val note: String = ""
)

data class DialogueNode(
    val id: String,
    val lines: List<DialogueLine>,
    val choices: List<DialogueChoice> = emptyList()
)

data class Quest(
    val id: String,
    val chapter: Int,
    val title: String,
    val description: String,
    val targetType: String, // "KILL_ENEMIES", "PURIFY_BOSS", "TALK_NPC", "COLLECT_CHEST"
    val targetCount: Int = 1,
    var currentCount: Int = 0,
    val goldReward: Int = 100,
    val expReward: Int = 150,
    val spiritStonesReward: Int = 5,
    val rewardItemId: String? = null,
    var status: QuestStatus = QuestStatus.NOT_STARTED,
    val startDialogueId: String? = null,
    val completeDialogueId: String? = null
)
