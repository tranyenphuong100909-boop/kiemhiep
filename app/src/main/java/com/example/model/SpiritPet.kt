package com.example.model

data class SpiritPet(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val element: Element,
    val icon: String,
    var isUnlocked: Boolean = false,
    val atkBonusPct: Int = 10,
    val hpBonusPct: Int = 10,
    val assistSkillName: String,
    val assistSkillDesc: String
)
