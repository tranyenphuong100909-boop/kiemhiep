package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.engine.GameEngine
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class SaveManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kiem_mong_save_prefs", Context.MODE_PRIVATE)

    fun hasSaveData(): Boolean {
        return prefs.contains("save_timestamp")
    }

    fun getSaveTimestamp(): Long {
        return prefs.getLong("save_timestamp", 0L)
    }

    fun saveGame(
        engine: GameEngine,
        inventory: Inventory,
        currentChapter: Int,
        activeQuest: Quest?,
        spiritPets: List<SpiritPet>
    ): Boolean {
        return try {
            val root = JSONObject()
            root.put("save_timestamp", System.currentTimeMillis())
            root.put("current_chapter", currentChapter)

            // Save Heroes
            val heroesArr = JSONArray()
            for (h in engine.heroes) {
                val hObj = JSONObject()
                hObj.put("id", h.id)
                hObj.put("level", h.level)
                hObj.put("exp", h.exp)
                hObj.put("maxExp", h.maxExp)
                hObj.put("currentHp", h.currentHp)
                hObj.put("skillPoints", h.skillPoints)
                hObj.put("upgradedSkill1", h.upgradedSkill1)
                hObj.put("upgradedSkill2", h.upgradedSkill2)
                hObj.put("upgradedUlt", h.upgradedUlt)

                // Bonds
                val bondsArr = JSONArray()
                for ((targetId, bond) in h.bonds) {
                    val bObj = JSONObject()
                    bObj.put("targetId", targetId)
                    bObj.put("points", bond.points)
                    bondsArr.put(bObj)
                }
                hObj.put("bonds", bondsArr)
                heroesArr.put(hObj)
            }
            root.put("heroes", heroesArr)

            // Save Inventory
            val invObj = JSONObject()
            invObj.put("gold", inventory.gold)
            invObj.put("spiritStones", inventory.spiritStones)
            val itemsArr = JSONArray()
            for (item in inventory.items) {
                val iObj = JSONObject()
                iObj.put("id", item.id)
                iObj.put("name", item.name)
                iObj.put("desc", item.description)
                iObj.put("type", item.type.name)
                iObj.put("rarity", item.rarity.name)
                iObj.put("icon", item.icon)
                iObj.put("atkBonus", item.atkBonus.toDouble())
                iObj.put("defBonus", item.defBonus.toDouble())
                iObj.put("hpBonus", item.hpBonus.toDouble())
                iObj.put("count", item.count)
                iObj.put("enhanceLevel", item.enhanceLevel)
                itemsArr.put(iObj)
            }
            invObj.put("items", itemsArr)
            root.put("inventory", invObj)

            // Save Quest
            if (activeQuest != null) {
                val qObj = JSONObject()
                qObj.put("id", activeQuest.id)
                qObj.put("currentCount", activeQuest.currentCount)
                qObj.put("status", activeQuest.status.name)
                root.put("quest", qObj)
            }

            // Save Pets
            val petsArr = JSONArray()
            for (pet in spiritPets) {
                val pObj = JSONObject()
                pObj.put("id", pet.id)
                pObj.put("unlocked", pet.isUnlocked)
                petsArr.put(pObj)
            }
            root.put("pets", petsArr)

            prefs.edit()
                .putString("game_data", root.toString())
                .putLong("save_timestamp", System.currentTimeMillis())
                .apply()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun loadGame(
        engine: GameEngine,
        inventory: Inventory,
        spiritPets: List<SpiritPet>
    ): Pair<Int, Quest?> {
        val raw = prefs.getString("game_data", null) ?: return Pair(1, null)
        return try {
            val root = JSONObject(raw)
            val chapter = root.optInt("current_chapter", 1)

            // Load Heroes
            val heroesArr = root.optJSONArray("heroes")
            if (heroesArr != null) {
                for (i in 0 until heroesArr.length()) {
                    val hObj = heroesArr.getJSONObject(i)
                    val id = hObj.getString("id")
                    val hero = engine.heroes.find { it.id == id }
                    if (hero != null) {
                        hero.level = hObj.optInt("level", 1)
                        hero.exp = hObj.optInt("exp", 0)
                        hero.maxExp = hObj.optInt("maxExp", 100)
                        hero.currentHp = hObj.optDouble("currentHp", hero.totalMaxHp.toDouble()).toFloat()
                        hero.skillPoints = hObj.optInt("skillPoints", 0)
                        hero.upgradedSkill1 = hObj.optInt("upgradedSkill1", 0)
                        hero.upgradedSkill2 = hObj.optInt("upgradedSkill2", 0)
                        hero.upgradedUlt = hObj.optInt("upgradedUlt", 0)

                        val bondsArr = hObj.optJSONArray("bonds")
                        if (bondsArr != null) {
                            for (j in 0 until bondsArr.length()) {
                                val bObj = bondsArr.getJSONObject(j)
                                val targetId = bObj.getString("targetId")
                                val points = bObj.getInt("points")
                                hero.bonds[targetId]?.points = points
                            }
                        }
                    }
                }
            }

            // Load Inventory
            val invObj = root.optJSONObject("inventory")
            if (invObj != null) {
                inventory.gold = invObj.optInt("gold", 300)
                inventory.spiritStones = invObj.optInt("spiritStones", 15)
                val itemsArr = invObj.optJSONArray("items")
                if (itemsArr != null && itemsArr.length() > 0) {
                    inventory.items.clear()
                    for (i in 0 until itemsArr.length()) {
                        val iObj = itemsArr.getJSONObject(i)
                        inventory.items.add(
                            GameItem(
                                id = iObj.getString("id"),
                                name = iObj.getString("name"),
                                description = iObj.getString("desc"),
                                type = ItemType.valueOf(iObj.getString("type")),
                                rarity = ItemRarity.valueOf(iObj.getString("rarity")),
                                icon = iObj.getString("icon"),
                                atkBonus = iObj.optDouble("atkBonus", 0.0).toFloat(),
                                defBonus = iObj.optDouble("defBonus", 0.0).toFloat(),
                                hpBonus = iObj.optDouble("hpBonus", 0.0).toFloat(),
                                count = iObj.optInt("count", 1),
                                enhanceLevel = iObj.optInt("enhanceLevel", 0)
                            )
                        )
                    }
                }
            }

            // Load Pets
            val petsArr = root.optJSONArray("pets")
            if (petsArr != null) {
                for (i in 0 until petsArr.length()) {
                    val pObj = petsArr.getJSONObject(i)
                    val id = pObj.getString("id")
                    val unlocked = pObj.getBoolean("unlocked")
                    spiritPets.find { it.id == id }?.isUnlocked = unlocked
                }
            }

            Pair(chapter, null)
        } catch (_: Exception) {
            Pair(1, null)
        }
    }

    fun clearSave() {
        prefs.edit().clear().apply()
    }
}
