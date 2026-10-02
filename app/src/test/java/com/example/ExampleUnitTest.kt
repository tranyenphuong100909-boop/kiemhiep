package com.example

import com.example.engine.GameEngine
import com.example.model.BondLevel
import com.example.model.EnemyType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testHeroInitializationAndParty() {
        val engine = GameEngine()
        assertEquals(4, engine.heroes.size)
        assertEquals("Lâm Vân", engine.heroes[0].name)
        assertEquals("Xích Long", engine.heroes[1].name)
        assertEquals("Mộc Linh", engine.heroes[2].name)
        assertEquals("Bạch Nguyệt", engine.heroes[3].name)
        assertEquals("hero_lam_van", engine.activeHero.id)
    }

    @Test
    fun testBondLevelCalculation() {
        assertEquals(BondLevel.STRANGER, BondLevel.fromPoints(50))
        assertEquals(BondLevel.TEAMMATE, BondLevel.fromPoints(100))
        assertEquals(BondLevel.FRIEND, BondLevel.fromPoints(250))
        assertEquals(BondLevel.BROTHER, BondLevel.fromPoints(500))
        assertEquals(BondLevel.SWORN_BROTHER, BondLevel.fromPoints(1000))
    }

    @Test
    fun testHeroLevelUp() {
        val engine = GameEngine()
        val hero = engine.activeHero
        val initialHp = hero.totalMaxHp
        val leveledUp = hero.gainExp(150)
        assertTrue(leveledUp)
        assertEquals(2, hero.level)
        assertTrue(hero.totalMaxHp > initialHp)
    }

    @Test
    fun testCharacterSwitch() {
        val engine = GameEngine()
        assertTrue(engine.switchHero(1))
        assertEquals("hero_xich_long", engine.activeHero.id)
    }

    @Test
    fun testBossPurification() {
        val engine = GameEngine()
        val boss = engine.enemies.first { it.isBoss }
        boss.currentHp = boss.maxHp * 0.2f
        val purified = engine.purifyBoss(boss)
        assertTrue(purified)
        assertTrue(boss.isPurified)
        assertNotNull(engine.activePet)
        assertEquals("Thanh Lân Long", engine.activePet?.name)
    }
}

