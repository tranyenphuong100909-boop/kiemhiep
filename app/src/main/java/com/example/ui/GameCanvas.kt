package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import com.example.engine.*
import com.example.model.*
import kotlin.math.*

@Composable
fun GameCanvas(
    engine: GameEngine,
    modifier: Modifier = Modifier,
    onChestClick: (String) -> Unit = {},
    onNpcClick: (String) -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Optional tap detection in world coordinates
                    },
                    onDrag = { _, _ -> }
                )
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Camera center on player
        val shakeX = if (engine.screenShakeIntensity > 0) (Math.random() * engine.screenShakeIntensity - engine.screenShakeIntensity / 2).toFloat() else 0f
        val shakeY = if (engine.screenShakeIntensity > 0) (Math.random() * engine.screenShakeIntensity - engine.screenShakeIntensity / 2).toFloat() else 0f

        val cameraX = engine.playerX - canvasWidth / 2f + shakeX
        val cameraY = engine.playerY - canvasHeight / 2f + shakeY

        clipRect(0f, 0f, canvasWidth, canvasHeight) {
            // Draw background terrain
            drawRect(
                color = Color(engine.currentArea.bgHex),
                topLeft = Offset(0f, 0f),
                size = size
            )

            // Draw terrain details & grid pathways
            drawWorldTerrain(cameraX, cameraY, engine.currentArea.width, engine.currentArea.height)

            // Draw stream & stone bridge
            drawStreamAndBridge(cameraX, cameraY)

            // Draw obstacles & trees
            for (obs in engine.currentArea.obstacles) {
                drawObstacle(obs, cameraX, cameraY)
            }

            // Draw Treasure Chests
            for (chest in engine.currentArea.chests) {
                drawTreasureChest(chest, cameraX, cameraY)
            }

            // Draw NPCs
            for (npc in engine.currentArea.npcs) {
                drawWorldNpc(npc, cameraX, cameraY)
            }

            // Draw Enemies & Boss
            for (enemy in engine.enemies) {
                if (enemy.state != EnemyState.DEAD) {
                    drawEnemy(enemy, cameraX, cameraY)
                }
            }

            // Draw Player Hero
            drawPlayerHero(engine, cameraX, cameraY)

            // Draw Spirit Pet companion if active
            engine.activePet?.let { pet ->
                drawSpiritPet(pet, engine.playerX, engine.playerY, cameraX, cameraY)
            }

            // Draw Slash effects
            for (slash in engine.slashEffects) {
                drawSlashEffect(slash, cameraX, cameraY)
            }

            // Draw Projectiles
            for (proj in engine.projectiles) {
                drawProjectile(proj, cameraX, cameraY)
            }

            // Draw Particles (Sakura petals, sparks, embers)
            for (p in engine.particles) {
                drawGameParticle(p, cameraX, cameraY)
            }

            // Draw Floating Damage Numbers
            for (dmg in engine.damageNumbers) {
                drawDamageNumber(dmg, cameraX, cameraY)
            }
        }
    }
}

private fun DrawScope.drawWorldTerrain(camX: Float, camY: Float, mapW: Float, mapH: Float) {
    // Subtle mystical floor tiles pattern
    val tileSize = 200f
    val startX = ((-camX % tileSize) + tileSize) % tileSize
    val startY = ((-camY % tileSize) + tileSize) % tileSize

    var x = startX - tileSize
    while (x < size.width + tileSize) {
        drawLine(
            color = Color(0x153A506B),
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.5f
        )
        x += tileSize
    }

    var y = startY - tileSize
    while (y < size.height + tileSize) {
        drawLine(
            color = Color(0x153A506B),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.5f
        )
        y += tileSize
    }

    // World boundary outline
    val boundLeft = -camX
    val boundTop = -camY
    drawRect(
        color = Color(0x5500E5FF),
        topLeft = Offset(boundLeft, boundTop),
        size = Size(mapW, mapH),
        style = Stroke(width = 4f)
    )
}

private fun DrawScope.drawStreamAndBridge(camX: Float, camY: Float) {
    // Azure stream flowing diagonally through valley
    val streamPath = Path().apply {
        moveTo(600f - camX, 0f - camY)
        quadraticTo(750f - camX, 600f - camY, 950f - camX, 1200f - camY)
        quadraticTo(1150f - camX, 1500f - camY, 1300f - camX, 1800f - camY)
    }
    drawPath(
        path = streamPath,
        color = Color(0x4000B4D8),
        style = Stroke(width = 80f, cap = StrokeCap.Round)
    )

    // Stone bridge crossing
    val bridgeX = 750f - camX
    val bridgeY = 600f - camY
    drawRoundRect(
        color = Color(0xFF6272A4),
        topLeft = Offset(bridgeX - 45f, bridgeY - 30f),
        size = Size(90f, 60f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = Color(0xFFBD93F9),
        topLeft = Offset(bridgeX - 40f, bridgeY - 26f),
        size = Size(80f, 52f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawObstacle(obs: WorldObstacle, camX: Float, camY: Float) {
    val sx = obs.x - camX
    val sy = obs.y - camY
    if (sx < -150f || sx > size.width + 150f || sy < -150f || sy > size.height + 150f) return

    when (obs.type) {
        "TREE" -> { // Cherry Blossom Tree
            // Tree shadow
            drawOval(
                color = Color(0x55000000),
                topLeft = Offset(sx - obs.radius * 0.8f, sy + obs.radius * 0.4f),
                size = Size(obs.radius * 1.6f, obs.radius * 0.6f)
            )
            // Trunk
            drawRect(
                color = Color(0xFF5D4037),
                topLeft = Offset(sx - 10f, sy - 20f),
                size = Size(20f, 40f)
            )
            // Foliage Pink Blossom
            drawCircle(
                color = Color(0xFFFF80AB),
                radius = obs.radius,
                center = Offset(sx, sy - 30f)
            )
            drawCircle(
                color = Color(0xFFFFB2DD),
                radius = obs.radius * 0.65f,
                center = Offset(sx - 12f, sy - 40f)
            )
        }
        "ROCK" -> {
            drawOval(
                color = Color(0xFF455A64),
                topLeft = Offset(sx - obs.radius, sy - obs.radius * 0.7f),
                size = Size(obs.radius * 2f, obs.radius * 1.4f)
            )
            drawOval(
                color = Color(0xFF78909C),
                topLeft = Offset(sx - obs.radius * 0.7f, sy - obs.radius * 0.5f),
                size = Size(obs.radius * 1.4f, obs.radius * 0.9f)
            )
        }
        "SHRINE" -> {
            // Pagoda shrine altar
            drawCircle(
                color = Color(0x3300E5FF),
                radius = obs.radius * 1.3f,
                center = Offset(sx, sy)
            )
            drawRect(
                color = Color(0xFF263238),
                topLeft = Offset(sx - obs.radius * 0.7f, sy - obs.radius * 0.7f),
                size = Size(obs.radius * 1.4f, obs.radius * 1.4f)
            )
            drawRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(sx - obs.radius * 0.5f, sy - obs.radius * 0.5f),
                size = Size(obs.radius, obs.radius),
                style = Stroke(width = 3f)
            )
        }
    }
}

private fun DrawScope.drawTreasureChest(chest: TreasureChest, camX: Float, camY: Float) {
    val sx = chest.x - camX
    val sy = chest.y - camY
    if (sx < -60f || sx > size.width + 60f || sy < -60f || sy > size.height + 60f) return

    val chestColor = if (chest.isOpened) Color(0xFF78909C) else Color(0xFFFFB300)
    // Shadow
    drawOval(
        color = Color(0x55000000),
        topLeft = Offset(sx - 20f, sy + 10f),
        size = Size(40f, 15f)
    )
    // Box
    drawRoundRect(
        color = chestColor,
        topLeft = Offset(sx - 22f, sy - 16f),
        size = Size(44f, 32f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    // Gold trim
    drawRoundRect(
        color = Color(0xFFFFD54F),
        topLeft = Offset(sx - 22f, sy - 16f),
        size = Size(44f, 32f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 2.5f)
    )
    if (!chest.isOpened) {
        // Glowing gem on chest
        drawCircle(
            color = Color(0xFF00E5FF),
            radius = 5f,
            center = Offset(sx, sy)
        )
    }
}

private fun DrawScope.drawWorldNpc(npc: WorldNpc, camX: Float, camY: Float) {
    val sx = npc.x - camX
    val sy = npc.y - camY
    if (sx < -60f || sx > size.width + 60f || sy < -60f || sy > size.height + 60f) return

    // Shadow
    drawOval(
        color = Color(0x44000000),
        topLeft = Offset(sx - 18f, sy + 18f),
        size = Size(36f, 14f)
    )
    // Robe / Body
    drawRoundRect(
        color = Color(npc.iconColor),
        topLeft = Offset(sx - 16f, sy - 24f),
        size = Size(32f, 44f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    // Head
    drawCircle(
        color = Color(0xFFFFE0B2),
        radius = 14f,
        center = Offset(sx, sy - 34f)
    )
    // Hair
    drawCircle(
        color = Color(0xFF212121),
        radius = 12f,
        center = Offset(sx, sy - 39f)
    )
    // Exclamation / Quest indicator above head
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = 8f,
        center = Offset(sx, sy - 58f)
    )
    drawCircle(
        color = Color(0xFF000000),
        radius = 3f,
        center = Offset(sx, sy - 58f)
    )
}

private fun DrawScope.drawPlayerHero(engine: GameEngine, camX: Float, camY: Float) {
    val hero = engine.activeHero
    val sx = engine.playerX - camX
    val sy = engine.playerY - camY

    // Shadow
    drawOval(
        color = Color(0x66000000),
        topLeft = Offset(sx - 24f, sy + 22f),
        size = Size(48f, 18f)
    )

    // Elemental Aura pulse
    val auraColor = Color(hero.element.colorHex).copy(alpha = if (engine.isDodging) 0.8f else 0.25f)
    drawCircle(
        color = auraColor,
        radius = if (engine.isDodging) 42f else 32f,
        center = Offset(sx, sy)
    )

    // Body / Robe
    val bodyColor = Color(hero.avatarColor)
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(sx - 18f, sy - 25f),
        size = Size(36f, 50f),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Golden / Accent Belt
    drawRect(
        color = Color(0xFFFFD54F),
        topLeft = Offset(sx - 18f, sy + 2f),
        size = Size(36f, 6f)
    )

    // Head
    drawCircle(
        color = Color(0xFFFFE0B2),
        radius = 16f,
        center = Offset(sx, sy - 35f)
    )

    // Anime Hair (Styled per character)
    val hairColor = when (hero.id) {
        "hero_xich_long" -> Color(0xFFFF3D00)
        "hero_moc_linh" -> Color(0xFF66BB6A)
        "hero_bach_nguyet" -> Color(0xFFECEFF1)
        else -> Color(0xFF1E88E5) // Lam Van cyan-blue hair
    }
    drawCircle(
        color = hairColor,
        radius = 15f,
        center = Offset(sx, sy - 40f)
    )

    // Eyes
    val eyeOffset = if (engine.playerFacingRight) 4f else -4f
    drawCircle(
        color = Color(0xFF212121),
        radius = 3.5f,
        center = Offset(sx + eyeOffset, sy - 34f)
    )

    // Weapon rendering
    val weaponX = if (engine.playerFacingRight) sx + 22f else sx - 22f
    val weaponY = sy - 5f
    val bladeColor = Color(hero.element.colorHex)

    if (engine.isAttacking) {
        // Weapon in swing motion
        val swingOffset = if (engine.playerFacingRight) 30f else -30f
        drawLine(
            color = bladeColor,
            start = Offset(sx, sy - 5f),
            end = Offset(sx + swingOffset, sy + 15f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    } else {
        // Weapon sheathed or held at ready
        drawLine(
            color = bladeColor,
            start = Offset(weaponX, weaponY + 18f),
            end = Offset(weaponX, weaponY - 24f),
            strokeWidth = 4.5f,
            cap = StrokeCap.Round
        )
        // Guard / Hilt
        drawLine(
            color = Color(0xFFFFD54F),
            start = Offset(weaponX - 8f, weaponY + 8f),
            end = Offset(weaponX + 8f, weaponY + 8f),
            strokeWidth = 3f
        )
    }
}

private fun DrawScope.drawSpiritPet(pet: SpiritPet, px: Float, py: Float, camX: Float, camY: Float) {
    // Pet floats gently beside player
    val sx = px + 40f - camX
    val sy = py - 35f - camY

    drawCircle(
        color = Color(0x5500E5FF),
        radius = 20f,
        center = Offset(sx, sy)
    )
    drawCircle(
        color = Color(0xFF00E5FF),
        radius = 12f,
        center = Offset(sx, sy)
    )
    // Little wings/horns
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = 5f,
        center = Offset(sx - 10f, sy - 8f)
    )
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = 5f,
        center = Offset(sx + 10f, sy - 8f)
    )
}

private fun DrawScope.drawEnemy(enemy: Enemy, camX: Float, camY: Float) {
    val sx = enemy.x - camX
    val sy = enemy.y - camY
    if (sx < -150f || sx > size.width + 150f || sy < -150f || sy > size.height + 150f) return

    if (enemy.isBoss) {
        // THANH LÂN LONG (Azure Scaled Dragon)
        drawBossDragon(enemy, sx, sy)
    } else {
        // Standard Mobs
        // Shadow
        drawOval(
            color = Color(0x55000000),
            topLeft = Offset(sx - 20f, sy + 18f),
            size = Size(40f, 15f)
        )

        val mobColor = when (enemy.type) {
            EnemyType.SHADOW_WOLF -> Color(0xFF311B92)
            EnemyType.PHANTOM_FLOWER -> Color(0xFF1B5E20)
            EnemyType.ABYSS_BLADE -> Color(0xFF263238)
            else -> Color(0xFFB71C1C)
        }

        // Frozen ice crystal overlay
        if (enemy.isFrozen) {
            drawCircle(
                color = Color(0xAA80D8FF),
                radius = 32f,
                center = Offset(sx, sy)
            )
        }

        // Mob Body
        drawRoundRect(
            color = mobColor,
            topLeft = Offset(sx - 18f, sy - 20f),
            size = Size(36f, 40f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Glowing red / dark eyes
        val eyeOffset = if (enemy.facingRight) 6f else -6f
        drawCircle(
            color = Color(0xFFFF1744),
            radius = 3.5f,
            center = Offset(sx + eyeOffset, sy - 10f)
        )
    }

    // Health Bar above enemy
    val barW = if (enemy.isBoss) 160f else 46f
    val barH = if (enemy.isBoss) 10f else 5f
    val barY = if (enemy.isBoss) sy - 110f else sy - 36f

    drawRect(
        color = Color(0xFF000000),
        topLeft = Offset(sx - barW / 2f - 1f, barY - 1f),
        size = Size(barW + 2f, barH + 2f)
    )
    val hpPct = (enemy.currentHp / enemy.maxHp).coerceIn(0f, 1f)
    val hpColor = if (enemy.isBoss) Color(0xFFFF5252) else Color(0xFFFF1744)
    drawRect(
        color = hpColor,
        topLeft = Offset(sx - barW / 2f, barY),
        size = Size(barW * hpPct, barH)
    )

    // Element indicator
    if (enemy.currentElementAffliction != Element.NONE) {
        drawCircle(
            color = Color(enemy.currentElementAffliction.colorHex),
            radius = 6f,
            center = Offset(sx + barW / 2f + 10f, barY + barH / 2f)
        )
    }
}

private fun DrawScope.drawBossDragon(enemy: Enemy, sx: Float, sy: Float) {
    // Magnificent Oriental Dragon
    val isPurified = enemy.isPurified || enemy.state == EnemyState.PURIFIED
    val primaryColor = if (isPurified) Color(0xFF00E5FF) else Color(0xFF1A237E)
    val scaleColor = if (isPurified) Color(0xFFFFD54F) else Color(0xFF7C4DFF)

    // Dragon Shadow
    drawOval(
        color = Color(0x66000000),
        topLeft = Offset(sx - 75f, sy + 45f),
        size = Size(150f, 40f)
    )

    // Dragon Coiling Body (S-curve representation)
    val bodyPath = Path().apply {
        moveTo(sx - 70f, sy + 20f)
        cubicTo(sx - 40f, sy + 50f, sx + 40f, sy - 40f, sx + 60f, sy - 20f)
    }
    drawPath(
        path = bodyPath,
        color = primaryColor,
        style = Stroke(width = 46f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = bodyPath,
        color = scaleColor,
        style = Stroke(width = 16f, cap = StrokeCap.Round)
    )

    // Dragon Head
    drawRoundRect(
        color = primaryColor,
        topLeft = Offset(sx - 45f, sy - 75f),
        size = Size(90f, 65f),
        cornerRadius = CornerRadius(20f, 20f)
    )

    // Dragon Horns
    drawLine(
        color = Color(0xFFFFD54F),
        start = Offset(sx - 20f, sy - 75f),
        end = Offset(sx - 45f, sy - 110f),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFFFFD54F),
        start = Offset(sx + 20f, sy - 75f),
        end = Offset(sx + 45f, sy - 110f),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )

    // Glowing Dragon Eyes
    val eyeColor = if (isPurified) Color(0xFF00E5FF) else Color(0xFFFF1744)
    drawCircle(
        color = eyeColor,
        radius = 7f,
        center = Offset(sx - 18f, sy - 50f)
    )
    drawCircle(
        color = eyeColor,
        radius = 7f,
        center = Offset(sx + 18f, sy - 50f)
    )

    // Whiskers
    drawLine(
        color = Color(0xFF80D8FF),
        start = Offset(sx - 30f, sy - 30f),
        end = Offset(sx - 65f, sy - 15f),
        strokeWidth = 2.5f
    )
    drawLine(
        color = Color(0xFF80D8FF),
        start = Offset(sx + 30f, sy - 30f),
        end = Offset(sx + 65f, sy - 15f),
        strokeWidth = 2.5f
    )

    // Purification Ready Aura
    if (enemy.state == EnemyState.PURIFY_READY) {
        drawCircle(
            color = Color(0x6600E5FF),
            radius = 120f,
            center = Offset(sx, sy - 30f),
            style = Stroke(width = 4f)
        )
    }
}

private fun DrawScope.drawSlashEffect(slash: SlashEffect, camX: Float, camY: Float) {
    val sx = slash.x - camX
    val sy = slash.y - camY
    val radius = slash.radius * (0.5f + slash.progress * 0.5f)
    val color = Color(slash.element.colorHex).copy(alpha = 1f - slash.progress)

    drawArc(
        color = color,
        startAngle = Math.toDegrees(slash.angle.toDouble()).toFloat() - 50f,
        sweepAngle = 100f,
        useCenter = false,
        topLeft = Offset(sx - radius, sy - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = 12f * (1f - slash.progress), cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawProjectile(proj: Projectile, camX: Float, camY: Float) {
    val sx = proj.x - camX
    val sy = proj.y - camY
    if (sx < -40f || sx > size.width + 40f || sy < -40f || sy > size.height + 40f) return

    val projColor = Color(proj.colorHex)
    drawCircle(
        color = projColor.copy(alpha = 0.4f),
        radius = proj.radius * 1.6f,
        center = Offset(sx, sy)
    )
    drawCircle(
        color = projColor,
        radius = proj.radius,
        center = Offset(sx, sy)
    )
    drawCircle(
        color = Color.White,
        radius = proj.radius * 0.4f,
        center = Offset(sx, sy)
    )
}

private fun DrawScope.drawGameParticle(p: Particle, camX: Float, camY: Float) {
    val sx = p.x - camX
    val sy = p.y - camY
    if (sx < -20f || sx > size.width + 20f || sy < -20f || sy > size.height + 20f) return

    val pColor = Color(p.color).copy(alpha = p.alpha.coerceIn(0f, 1f))
    when (p.shape) {
        ParticleShape.SAKURA_PETAL -> {
            drawOval(
                color = pColor,
                topLeft = Offset(sx - p.size, sy - p.size * 0.6f),
                size = Size(p.size * 2f, p.size * 1.2f)
            )
        }
        ParticleShape.EMBER -> {
            drawCircle(
                color = pColor,
                radius = p.size,
                center = Offset(sx, sy)
            )
        }
        else -> {
            drawCircle(
                color = pColor,
                radius = p.size,
                center = Offset(sx, sy)
            )
        }
    }
}

private fun DrawScope.drawDamageNumber(d: DamageNumber, camX: Float, camY: Float) {
    val sx = d.x - camX
    val sy = d.y - camY - (1.0f - d.lifetime) * 45f // float up
    if (sx < -50f || sx > size.width + 50f || sy < -50f || sy > size.height + 50f) return

    val numColor = if (d.isCrit) Color(0xFFFFD54F) else Color(d.element.colorHex)
    // Draw visual damage badge circle with indicator
    drawCircle(
        color = Color(0xAA000000),
        radius = if (d.isCrit) 18f else 14f,
        center = Offset(sx, sy)
    )
    drawCircle(
        color = numColor,
        radius = if (d.isCrit) 16f else 12f,
        center = Offset(sx, sy),
        style = Stroke(width = 2.5f)
    )

    if (d.reactionText != null) {
        // Reaction banner halo
        drawCircle(
            color = Color(0xFFFF5722).copy(alpha = 0.6f),
            radius = 26f,
            center = Offset(sx, sy - 18f)
        )
    }
}
