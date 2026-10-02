package com.example.engine

import com.example.model.*
import kotlin.math.*

class GameEngine {

    // Party heroes
    val heroes: MutableList<Hero> = mutableListOf()
    var activeHeroIndex: Int = 0
    val activeHero: Hero get() = heroes.getOrElse(activeHeroIndex) { heroes.first() }

    // World Area
    var currentArea: WorldArea = GameWorldData.createThanhHoaCoc()

    // Player Entity state
    var playerX: Float = 400f
    var playerY: Float = 400f
    var playerVx: Float = 0f
    var playerVy: Float = 0f
    var playerFacingRight: Boolean = true
    var isAttacking: Boolean = false
    var attackProgress: Float = 0f
    var comboStep: Int = 0
    var isDodging: Boolean = false
    var dodgeTimer: Float = 0f
    val isInvulnerable: Boolean get() = isDodging

    // Active Spirit Pet
    var activePet: SpiritPet? = null

    // Entities in world
    val enemies: MutableList<Enemy> = mutableListOf()
    val projectiles: MutableList<Projectile> = mutableListOf()
    val slashEffects: MutableList<SlashEffect> = mutableListOf()
    val particles: MutableList<Particle> = mutableListOf()
    val damageNumbers: MutableList<DamageNumber> = mutableListOf()

    // Screen Shake
    var screenShakeIntensity: Float = 0f

    // Combat stats / cooldowns
    var swapCooldown: Float = 0f
    var nextId: Long = 1L

    // Callbacks
    var onBossPurified: ((SpiritPet) -> Unit)? = null
    var onEnemyKilled: ((Enemy) -> Unit)? = null
    var onHeroLevelUp: ((Hero) -> Unit)? = null

    init {
        initHeroes()
        spawnInitialEnemies()
        initAmbientBlossoms()
    }

    private fun initHeroes() {
        heroes.clear()
        // 1. Lâm Vân - Cyan/Wind/Sword
        val lamVanSkills = listOf(
            Skill("lv_s1", "Thanh Phong Trảm", "Lướt kiếm thần tốc chém 3 nhát gió liên hoàn gây sát thương Phong.", 4.5f, 25f, 2.2f, Element.WIND, "🌪️"),
            Skill("lv_s2", "Lạc Diệp Kiếm Trận", "Dựng kiếm trận phong bế kẻ địch, tăng 30% né tránh và phản kích.", 7.0f, 35f, 2.8f, Element.WIND, "🗡️"),
            Skill("lv_ult", "Thiên Kiếm Trảm Nguyệt", "Kiếm khí ngập trời chém đứt không gian, gây sát thương cực lớn!", 16f, 100f, 6.0f, Element.WIND, "✨", isUltimate = true)
        )
        val lamVan = Hero(
            id = "hero_lam_van",
            name = "Lâm Vân",
            title = "Thanh Vân Kiếm Khách",
            age = 17,
            role = "Kiếm khách cận chiến",
            weaponType = "Kiếm đơn",
            element = Element.WIND,
            personality = "Chính trực, trầm tĩnh, vì đại nghĩa và huynh đệ",
            avatarColor = 0xFF00E5FF,
            skills = lamVanSkills
        )

        // 2. Xích Long - Fire/Brawler
        val xichLongSkills = listOf(
            Skill("xl_s1", "Liệt Diễm Quyền", "Quyền lửa phá tan phòng thủ đối phương, gây bỏng liên tục.", 4.0f, 20f, 2.4f, Element.FIRE, "🔥"),
            Skill("xl_s2", "Hỏa Long Toái Địa", "Nhảy lên không trung giáng mạnh nắm đấm dung nham nổ tung mặt đất.", 6.5f, 35f, 3.2f, Element.FIRE, "💥"),
            Skill("xl_ult", "Nộ Long Xuất Hải", "Hóa thân hỏa long cuồng bạo càn quét toàn bộ chiến trường!", 16f, 100f, 6.5f, Element.FIRE, "🐉", isUltimate = true)
        )
        val xichLong = Hero(
            id = "hero_xich_long",
            name = "Xích Long",
            title = "Viêm Ma Quyền Sư",
            age = 16,
            role = "Tiên phong cận chiến sát thương lớn",
            weaponType = "Song quyền & Côn",
            element = Element.FIRE,
            personality = "Nhiệt huyết, trượng nghĩa, thích chiến đấu và đồ ăn ngon",
            avatarColor = 0xFFFF5722,
            skills = xichLongSkills
        )

        // 3. Mộc Linh - Nature/Healer/Mage
        val mocLinhSkills = listOf(
            Skill("ml_s1", "Hoa Vũ Mộng Ảo", "Bắn ra những cánh hoa sắc bén truy đuổi kẻ địch tầm xa.", 3.5f, 20f, 1.8f, Element.WOOD, "🌸"),
            Skill("ml_s2", "Thánh Mộc Dưỡng Hồn", "Tạo trận địa hoa linh hồi phục HP liên tục cho toàn đội.", 8.0f, 40f, 0f, Element.WOOD, "🌿"),
            Skill("ml_ult", "Vạn Mộc Triều Bái", "Dây leo trói chặt mọi kẻ địch, thanh tẩy ma khí và hồi 50% HP!", 18f, 100f, 4.0f, Element.WOOD, "🌺", isUltimate = true)
        )
        val mocLinh = Hero(
            id = "hero_moc_linh",
            name = "Mộc Linh",
            title = "Linh Hoa Y Sĩ",
            age = 16,
            role = "Pháp sư hỗ trợ & Trị liệu",
            weaponType = "Linh trượng Mộc",
            element = Element.WOOD,
            personality = "Dịu dàng, chu đáo, thấu hiểu lòng người",
            avatarColor = 0xFF69F0AE,
            skills = mocLinhSkills
        )

        // 4. Bạch Nguyệt - Ice/Swordswoman
        val bachNguyetSkills = listOf(
            Skill("bn_s1", "Hàn Băng Kiếm Khí", "Phóng thích nhát kiếm băng giá làm chậm và đóng băng mục tiêu.", 4.0f, 25f, 2.0f, Element.ICE, "❄️"),
            Skill("bn_s2", "Băng Vực Tuyệt Đối", "Triệu hồi gai băng nhô lên từ lòng đất khống chế diện rộng.", 7.5f, 35f, 2.9f, Element.ICE, "🧊"),
            Skill("bn_ult", "Bạch Dạ Băng Phong", "Đóng băng thời gian và không gian, chém vỡ mọi hàn băng!", 17f, 100f, 5.8f, Element.ICE, "🌙", isUltimate = true)
        )
        val bachNguyet = Hero(
            id = "hero_bach_nguyet",
            name = "Bạch Nguyệt",
            title = "Nguyệt Hoa Kiếm Nữ",
            age = 18,
            role = "Kiếm sĩ khống chế & Băng vực",
            weaponType = "Trường kiếm Hàn Băng",
            element = Element.ICE,
            personality = "Lạnh lùng bên ngoài, ấm áp bên trong, bảo vệ đồng đội trong bóng tối",
            avatarColor = 0xFF80D8FF,
            skills = bachNguyetSkills
        )

        // Setup brotherhood bonds between them
        lamVan.bonds[xichLong.id] = CharacterBond(xichLong.id, 280) // Level 3: Friend -> growing to Brother
        lamVan.bonds[mocLinh.id] = CharacterBond(mocLinh.id, 260)
        lamVan.bonds[bachNguyet.id] = CharacterBond(bachNguyet.id, 210)

        xichLong.bonds[lamVan.id] = CharacterBond(lamVan.id, 280)
        xichLong.bonds[mocLinh.id] = CharacterBond(mocLinh.id, 180)
        xichLong.bonds[bachNguyet.id] = CharacterBond(bachNguyet.id, 140)

        mocLinh.bonds[lamVan.id] = CharacterBond(lamVan.id, 260)
        mocLinh.bonds[xichLong.id] = CharacterBond(xichLong.id, 180)
        mocLinh.bonds[bachNguyet.id] = CharacterBond(bachNguyet.id, 270) // Close bond with Bach Nguyet

        bachNguyet.bonds[lamVan.id] = CharacterBond(lamVan.id, 210)
        bachNguyet.bonds[xichLong.id] = CharacterBond(xichLong.id, 140)
        bachNguyet.bonds[mocLinh.id] = CharacterBond(mocLinh.id, 270)

        heroes.add(lamVan)
        heroes.add(xichLong)
        heroes.add(mocLinh)
        heroes.add(bachNguyet)
    }

    private fun spawnInitialEnemies() {
        enemies.clear()
        // Monsters around Valley
        enemies.add(Enemy(nextId++, EnemyType.SHADOW_WOLF, 800f, 650f))
        enemies.add(Enemy(nextId++, EnemyType.SHADOW_WOLF, 920f, 720f))
        enemies.add(Enemy(nextId++, EnemyType.PHANTOM_FLOWER, 1150f, 620f))
        enemies.add(Enemy(nextId++, EnemyType.PHANTOM_FLOWER, 1250f, 850f))
        enemies.add(Enemy(nextId++, EnemyType.ABYSS_BLADE, 1450f, 950f))
        enemies.add(Enemy(nextId++, EnemyType.ABYSS_BLADE, 1600f, 1100f))

        // First Boss: Thanh Lân Long at Dragon Peak (x=1950, y=750)
        enemies.add(Enemy(nextId++, EnemyType.THANH_LAN_LONG, 1950f, 750f))
    }

    private fun initAmbientBlossoms() {
        for (i in 0 until 40) {
            particles.add(
                Particle(
                    x = (Math.random() * currentArea.width).toFloat(),
                    y = (Math.random() * currentArea.height).toFloat(),
                    vx = (Math.random() * 30 - 10).toFloat(),
                    vy = (Math.random() * 40 + 20).toFloat(),
                    color = 0xFFFF80AB,
                    size = (Math.random() * 6 + 4).toFloat(),
                    alpha = (Math.random() * 0.7 + 0.3).toFloat(),
                    lifetime = 100f,
                    shape = ParticleShape.SAKURA_PETAL
                )
            )
        }
    }

    // Main 60fps game loop
    fun update(deltaTime: Float) {
        val dt = deltaTime.coerceIn(0.001f, 0.05f)

        // Screen shake decay
        if (screenShakeIntensity > 0f) {
            screenShakeIntensity = (screenShakeIntensity - dt * 25f).coerceAtLeast(0f)
        }

        // Swap cooldown
        if (swapCooldown > 0f) {
            swapCooldown = (swapCooldown - dt).coerceAtLeast(0f)
        }

        // Dodge logic
        if (isDodging) {
            dodgeTimer -= dt
            if (dodgeTimer <= 0f) {
                isDodging = false
            }
        }

        // Active hero energy and skills update
        for (hero in heroes) {
            // Energy passive regen
            hero.currentEnergy = (hero.currentEnergy + dt * 2.5f).coerceAtMost(hero.maxEnergy)
            for (skill in hero.skills) {
                if (skill.currentCooldown > 0f) {
                    skill.currentCooldown = (skill.currentCooldown - dt).coerceAtLeast(0f)
                }
            }
        }

        // Player Movement & Physics
        val currentSpeed = if (isDodging) activeHero.speed * 1.8f else activeHero.speed
        playerX += playerVx * currentSpeed * dt
        playerY += playerVy * currentSpeed * dt

        // Keep within map bounds
        playerX = playerX.coerceIn(50f, currentArea.width - 50f)
        playerY = playerY.coerceIn(50f, currentArea.height - 50f)

        // Obstacle Collisions
        for (obs in currentArea.obstacles) {
            val dx = playerX - obs.x
            val dy = playerY - obs.y
            val dist = sqrt(dx * dx + dy * dy)
            val minDist = obs.radius + 25f
            if (dist < minDist && dist > 0.001f) {
                val push = (minDist - dist)
                playerX += (dx / dist) * push
                playerY += (dy / dist) * push
            }
        }

        // Attack animation tick
        if (isAttacking) {
            attackProgress += dt * 4f
            if (attackProgress >= 1f) {
                isAttacking = false
                attackProgress = 0f
            }
        }

        // Update Projectiles
        val projIter = projectiles.iterator()
        while (projIter.hasNext()) {
            val proj = projIter.next()
            proj.x += proj.vx * dt
            proj.y += proj.vy * dt
            proj.lifetime -= dt

            var hit = false
            if (proj.fromPlayer) {
                for (enemy in enemies) {
                    if (enemy.state == EnemyState.DEAD || enemy.state == EnemyState.PURIFIED) continue
                    val dx = enemy.x - proj.x
                    val dy = enemy.y - proj.y
                    val hitRadius = if (enemy.isBoss) 90f else 45f
                    if (sqrt(dx * dx + dy * dy) < hitRadius) {
                        applyDamageToEnemy(enemy, proj.damage, proj.element)
                        hit = true
                        spawnHitSparks(proj.x, proj.y, proj.colorHex)
                        break
                    }
                }
            } else {
                // Enemy projectile hitting player
                val dx = playerX - proj.x
                val dy = playerY - proj.y
                if (sqrt(dx * dx + dy * dy) < 35f && !isInvulnerable) {
                    applyDamageToPlayer(proj.damage)
                    hit = true
                    spawnHitSparks(playerX, playerY, 0xFFFF0055)
                }
            }

            if (hit || proj.lifetime <= 0f) {
                projIter.remove()
            }
        }

        // Update Slash effects
        val slashIter = slashEffects.iterator()
        while (slashIter.hasNext()) {
            val slash = slashIter.next()
            slash.progress += dt / slash.duration
            if (slash.progress >= 1f) {
                slashIter.remove()
            }
        }

        // Update Enemies AI
        val enemyIter = enemies.iterator()
        while (enemyIter.hasNext()) {
            val enemy = enemyIter.next()
            if (enemy.state == EnemyState.DEAD) {
                continue
            }

            // Element affliction countdown
            if (enemy.afflictionTimer > 0f) {
                enemy.afflictionTimer -= dt
                if (enemy.afflictionTimer <= 0f) {
                    enemy.currentElementAffliction = Element.NONE
                }
            }

            // Frozen timer
            if (enemy.isFrozen) {
                enemy.frozenTimer -= dt
                if (enemy.frozenTimer <= 0f) {
                    enemy.isFrozen = false
                }
                continue // Can't move while frozen
            }

            // Check Boss Purification State
            if (enemy.isBoss && enemy.currentHp <= enemy.maxHp * 0.25f && !enemy.isPurified) {
                enemy.state = EnemyState.PURIFY_READY
            }

            val dx = playerX - enemy.x
            val dy = playerY - enemy.y
            val dist = sqrt(dx * dx + dy * dy)
            enemy.facingRight = dx > 0

            // AI State Machine
            val aggroRange = if (enemy.isBoss) 600f else 350f
            val attackRange = if (enemy.isBoss) 120f else 50f

            if (enemy.attackCooldown > 0f) {
                enemy.attackCooldown -= dt
            }

            when {
                dist < attackRange && enemy.attackCooldown <= 0f -> {
                    enemy.state = EnemyState.ATTACK
                    enemy.attackCooldown = if (enemy.isBoss) 2.2f else 1.8f
                    performEnemyAttack(enemy)
                }
                dist < aggroRange -> {
                    enemy.state = EnemyState.CHASE
                    val moveSpeed = enemy.type.speed * dt
                    enemy.x += (dx / dist) * moveSpeed
                    enemy.y += (dy / dist) * moveSpeed
                }
                else -> {
                    enemy.state = EnemyState.IDLE
                }
            }
        }

        // Update Particles
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.lifetime -= dt
            if (p.shape == ParticleShape.SAKURA_PETAL) {
                // Loop sakura petals across map
                if (p.y > currentArea.height) p.y = 0f
                if (p.x > currentArea.width) p.x = 0f
            } else {
                p.alpha = (p.lifetime / 0.8f).coerceIn(0f, 1f)
                if (p.lifetime <= 0f) {
                    pIter.remove()
                }
            }
        }

        // Update Damage Numbers
        val dIter = damageNumbers.iterator()
        while (dIter.hasNext()) {
            val d = dIter.next()
            d.lifetime -= dt
            if (d.lifetime <= 0f) {
                dIter.remove()
            }
        }
    }

    // Input handlers
    fun setPlayerMovement(vx: Float, vy: Float) {
        playerVx = vx
        playerVy = vy
        if (vx > 0.05f) playerFacingRight = true
        else if (vx < -0.05f) playerFacingRight = false
    }

    fun triggerDodge(): Boolean {
        if (isDodging) return false
        isDodging = true
        dodgeTimer = 0.35f
        SoundSystem.playSwordSlash()
        // Spawn dash ghost trail
        spawnHitSparks(playerX, playerY, activeHero.element.colorHex)
        return true
    }

    // Normal Attack Combo
    fun triggerNormalAttack() {
        if (isAttacking) return
        isAttacking = true
        attackProgress = 0f
        comboStep = (comboStep + 1) % 3

        val angle = if (playerFacingRight) 0f else PI.toFloat()
        val reach = 90f
        val damage = activeHero.atk * (1f + comboStep * 0.25f)

        // Spawn visual slash arc
        slashEffects.add(
            SlashEffect(
                x = playerX + (if (playerFacingRight) 40f else -40f),
                y = playerY,
                angle = angle,
                radius = reach,
                element = activeHero.element
            )
        )

        // Hero specific sound and projectile
        when (activeHero.element) {
            Element.FIRE -> SoundSystem.playFireBurst()
            Element.ICE -> SoundSystem.playIceShatter()
            Element.WOOD -> SoundSystem.playHealChime()
            else -> SoundSystem.playSwordSlash()
        }

        // Mộc Linh shoots ranged petals on attack
        if (activeHero.id == "hero_moc_linh") {
            val vx = if (playerFacingRight) 450f else -450f
            projectiles.add(
                Projectile(
                    id = nextId++,
                    x = playerX,
                    y = playerY - 10f,
                    vx = vx,
                    vy = (Math.random() * 60 - 30).toFloat(),
                    radius = 18f,
                    damage = damage * 0.9f,
                    element = Element.WOOD,
                    fromPlayer = true,
                    colorHex = 0xFF69F0AE
                )
            )
        }

        // Hit detection in front of player
        for (enemy in enemies) {
            if (enemy.state == EnemyState.DEAD || enemy.state == EnemyState.PURIFIED) continue
            val dx = enemy.x - playerX
            val dy = enemy.y - playerY
            val dist = sqrt(dx * dx + dy * dy)
            val isFacing = (playerFacingRight && dx >= -20f) || (!playerFacingRight && dx <= 20f)
            val hitThreshold = if (enemy.isBoss) 120f else 85f

            if (dist < hitThreshold && isFacing) {
                applyDamageToEnemy(enemy, damage, activeHero.element)
                spawnHitSparks(enemy.x, enemy.y, activeHero.element.colorHex)
                // Add energy on hit
                activeHero.currentEnergy = (activeHero.currentEnergy + 8f).coerceAtMost(activeHero.maxEnergy)
            }
        }
    }

    // Cast Skill
    fun castSkill(skillIndex: Int): Boolean {
        if (skillIndex !in activeHero.skills.indices) return false
        val skill = activeHero.skills[skillIndex]
        if (skill.currentCooldown > 0f) return false
        if (skill.isUltimate && activeHero.currentEnergy < skill.energyCost) return false

        skill.currentCooldown = skill.cooldownSeconds
        if (skill.isUltimate) {
            activeHero.currentEnergy = 0f
            screenShakeIntensity = 18f
        }

        when (skill.id) {
            "lv_s1" -> { // Lâm Vân: Thanh Phong Trảm
                SoundSystem.playSwordSlash()
                // Fast dash forward and 3 wind slashes
                val dashDir = if (playerFacingRight) 180f else -180f
                playerX += dashDir
                damageEnemiesInRadius(playerX, playerY, 140f, activeHero.atk * skill.damageMultiplier, Element.WIND)
            }
            "lv_s2" -> { // Lâm Vân: Lạc Diệp Kiếm Trận
                SoundSystem.playSwordSlash()
                damageEnemiesInRadius(playerX, playerY, 180f, activeHero.atk * skill.damageMultiplier, Element.WIND)
                triggerDodge()
            }
            "lv_ult" -> { // Lâm Vân: Thiên Kiếm Trảm Nguyệt
                SoundSystem.playSwordSlash()
                screenShakeIntensity = 25f
                damageEnemiesInRadius(playerX, playerY, 350f, activeHero.atk * skill.damageMultiplier, Element.WIND)
                for (i in 0 until 5) {
                    val angle = (i * 0.4f - 0.8f)
                    projectiles.add(
                        Projectile(
                            id = nextId++,
                            x = playerX,
                            y = playerY,
                            vx = cos(angle) * (if (playerFacingRight) 600f else -600f),
                            vy = sin(angle) * 300f,
                            radius = 28f,
                            damage = activeHero.atk * 2.5f,
                            element = Element.WIND,
                            fromPlayer = true,
                            colorHex = 0xFF00E5FF
                        )
                    )
                }
            }
            "xl_s1" -> { // Xích Long: Liệt Diễm Quyền
                SoundSystem.playFireBurst()
                screenShakeIntensity = 8f
                val fx = playerX + (if (playerFacingRight) 70f else -70f)
                damageEnemiesInRadius(fx, playerY, 130f, activeHero.atk * skill.damageMultiplier, Element.FIRE)
            }
            "xl_s2" -> { // Xích Long: Hỏa Long Toái Địa
                SoundSystem.playFireBurst()
                screenShakeIntensity = 15f
                damageEnemiesInRadius(playerX, playerY, 200f, activeHero.atk * skill.damageMultiplier, Element.FIRE)
                for (i in 0 until 12) {
                    particles.add(
                        Particle(
                            x = playerX,
                            y = playerY,
                            vx = (Math.random() * 200 - 100).toFloat(),
                            vy = (Math.random() * -200 - 50).toFloat(),
                            color = 0xFFFF5722,
                            size = 8f,
                            lifetime = 0.6f,
                            shape = ParticleShape.EMBER
                        )
                    )
                }
            }
            "xl_ult" -> { // Xích Long: Nộ Long Xuất Hải
                SoundSystem.playFireBurst()
                SoundSystem.playDragonRoar()
                screenShakeIntensity = 30f
                damageEnemiesInRadius(playerX, playerY, 380f, activeHero.atk * skill.damageMultiplier, Element.FIRE)
            }
            "ml_s1" -> { // Mộc Linh: Hoa Vũ
                SoundSystem.playHealChime()
                for (i in 0 until 4) {
                    val angle = (i * 0.5f - 0.75f)
                    projectiles.add(
                        Projectile(
                            id = nextId++,
                            x = playerX,
                            y = playerY,
                            vx = cos(angle) * (if (playerFacingRight) 450f else -450f),
                            vy = sin(angle) * 300f,
                            radius = 20f,
                            damage = activeHero.atk * skill.damageMultiplier,
                            element = Element.WOOD,
                            fromPlayer = true,
                            colorHex = 0xFF69F0AE
                        )
                    )
                }
            }
            "ml_s2" -> { // Mộc Linh: Hồi phục HP
                SoundSystem.playHealChime()
                val healAmt = activeHero.totalMaxHp * 0.45f
                activeHero.currentHp = (activeHero.currentHp + healAmt).coerceAtMost(activeHero.totalMaxHp)
                damageNumbers.add(
                    DamageNumber(nextId++, playerX, playerY - 40f, healAmt.toInt(), isCrit = false, element = Element.WOOD, reactionText = "+ HỒI PHỤC")
                )
            }
            "ml_ult" -> { // Mộc Linh: Vạn Mộc Triều Bái
                SoundSystem.playHealChime()
                screenShakeIntensity = 12f
                damageEnemiesInRadius(playerX, playerY, 320f, activeHero.atk * skill.damageMultiplier, Element.WOOD)
                for (hero in heroes) {
                    hero.currentHp = (hero.currentHp + hero.totalMaxHp * 0.5f).coerceAtMost(hero.totalMaxHp)
                }
            }
            "bn_s1" -> { // Bạch Nguyệt: Hàn Băng Kiếm Khí
                SoundSystem.playIceShatter()
                projectiles.add(
                    Projectile(
                        id = nextId++,
                        x = playerX,
                        y = playerY,
                        vx = if (playerFacingRight) 550f else -550f,
                        vy = 0f,
                        radius = 26f,
                        damage = activeHero.atk * skill.damageMultiplier,
                        element = Element.ICE,
                        fromPlayer = true,
                        colorHex = 0xFF80D8FF
                    )
                )
            }
            "bn_s2" -> { // Bạch Nguyệt: Băng Vực
                SoundSystem.playIceShatter()
                screenShakeIntensity = 10f
                damageEnemiesInRadius(playerX, playerY, 220f, activeHero.atk * skill.damageMultiplier, Element.ICE)
            }
            "bn_ult" -> { // Bạch Nguyệt: Bạch Dạ Băng Phong
                SoundSystem.playIceShatter()
                screenShakeIntensity = 28f
                damageEnemiesInRadius(playerX, playerY, 380f, activeHero.atk * skill.damageMultiplier, Element.ICE)
                for (enemy in enemies) {
                    enemy.isFrozen = true
                    enemy.frozenTimer = 3.5f
                }
            }
        }
        return true
    }

    // Brotherhood / Link Skill Activation
    fun triggerBrotherhoodSkill(): String? {
        val partner = heroes.find { it.id != activeHero.id && it.isUnlocked } ?: return null
        val bond = activeHero.bonds[partner.id] ?: return null
        if (bond.level.level < 2) return "Cần đạt cấp Đồng Đội (Lv.2) trở lên!"

        if (activeHero.currentEnergy < 50f) return "Không đủ Năng Lượng (cần 50)!"
        activeHero.currentEnergy -= 50f
        screenShakeIntensity = 25f

        val skillName = when {
            (activeHero.id == "hero_lam_van" && partner.id == "hero_xich_long") ||
            (activeHero.id == "hero_xich_long" && partner.id == "hero_lam_van") -> {
                SoundSystem.playFireBurst()
                SoundSystem.playSwordSlash()
                damageEnemiesInRadius(playerX, playerY, 320f, (activeHero.atk + partner.atk) * 2.8f, Element.FIRE)
                "HUYNH ĐỆ ĐỒNG TÂM!"
            }
            (activeHero.id == "hero_moc_linh" && partner.id == "hero_bach_nguyet") ||
            (activeHero.id == "hero_bach_nguyet" && partner.id == "hero_moc_linh") -> {
                SoundSystem.playIceShatter()
                SoundSystem.playHealChime()
                damageEnemiesInRadius(playerX, playerY, 320f, (activeHero.atk + partner.atk) * 2.5f, Element.ICE)
                activeHero.currentHp = (activeHero.currentHp + activeHero.totalMaxHp * 0.4f).coerceAtMost(activeHero.totalMaxHp)
                "BĂNG HOA VĨNH HẰNG!"
            }
            else -> {
                SoundSystem.playSwordSlash()
                damageEnemiesInRadius(playerX, playerY, 280f, (activeHero.atk + partner.atk) * 2.2f, activeHero.element)
                "HUYNH ĐỆ LIÊN KÍCH!"
            }
        }

        // Increase bond points
        bond.points += 20
        partner.bonds[activeHero.id]?.let { it.points += 20 }

        return skillName
    }

    // Switch Character
    fun switchHero(targetIndex: Int): Boolean {
        if (targetIndex !in heroes.indices || targetIndex == activeHeroIndex) return false
        if (swapCooldown > 0f) return false

        activeHeroIndex = targetIndex
        swapCooldown = 2.0f
        SoundSystem.playSwordSlash()

        // Swap impact: small shockwave and invulnerability
        screenShakeIntensity = 6f
        damageEnemiesInRadius(playerX, playerY, 90f, activeHero.atk * 0.8f, activeHero.element)
        triggerDodge()
        return true
    }

    // Boss Purification
    fun purifyBoss(boss: Enemy): Boolean {
        if (!boss.isBoss || boss.currentHp > boss.maxHp * 0.35f || boss.isPurified) return false
        boss.isPurified = true
        boss.state = EnemyState.PURIFIED
        SoundSystem.playLevelUp()
        SoundSystem.playDragonRoar()

        val companion = SpiritPet(
            id = "pet_thanh_lan_long",
            name = "Thanh Lân Long",
            title = "Linh Thú Hộ Thần",
            description = "Rồng thần hộ mệnh của Thanh Hoa Cốc được giải phóng khỏi ma khí hắc ám, cùng huynh đệ đồng hành bảo vệ Lục Giới.",
            element = Element.LIGHTNING,
            icon = "🐉",
            isUnlocked = true,
            atkBonusPct = 25,
            hpBonusPct = 20,
            assistSkillName = "Thanh Lân Lôi Giáng",
            assistSkillDesc = "Gọi sấm sét giáng xuống làm choáng kẻ địch xung quanh."
        )
        activePet = companion
        onBossPurified?.invoke(companion)
        return true
    }

    private fun damageEnemiesInRadius(x: Float, y: Float, radius: Float, rawDamage: Float, element: Element) {
        for (enemy in enemies) {
            if (enemy.state == EnemyState.DEAD || enemy.state == EnemyState.PURIFIED) continue
            val dx = enemy.x - x
            val dy = enemy.y - y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist <= radius) {
                applyDamageToEnemy(enemy, rawDamage, element)
                spawnHitSparks(enemy.x, enemy.y, element.colorHex)
            }
        }
    }

    private fun applyDamageToEnemy(enemy: Enemy, rawDamage: Float, incomingElement: Element) {
        // Elemental Reaction System
        var finalDamage = rawDamage
        var reactionLabel: String? = null

        if (enemy.currentElementAffliction != Element.NONE && enemy.currentElementAffliction != incomingElement) {
            val prev = enemy.currentElementAffliction
            when {
                // Fire + Wind -> Firestorm (+75%)
                (prev == Element.FIRE && incomingElement == Element.WIND) || (prev == Element.WIND && incomingElement == Element.FIRE) -> {
                    finalDamage *= 1.75f
                    reactionLabel = "BÃO LỬA!"
                    SoundSystem.playFireBurst()
                }
                // Ice + Water -> Frozen
                (prev == Element.ICE && incomingElement == Element.WATER) || (prev == Element.WATER && incomingElement == Element.ICE) -> {
                    enemy.isFrozen = true
                    enemy.frozenTimer = 3.0f
                    reactionLabel = "ĐÓNG BĂNG!"
                    SoundSystem.playIceShatter()
                }
                // Fire + Ice -> Melt (+120%)
                (prev == Element.FIRE && incomingElement == Element.ICE) || (prev == Element.ICE && incomingElement == Element.FIRE) -> {
                    finalDamage *= 2.2f
                    reactionLabel = "TAN CHẢY!"
                    SoundSystem.playFireBurst()
                }
                // Lightning + Water -> Electrocute
                (prev == Element.LIGHTNING && incomingElement == Element.WATER) || (prev == Element.WATER && incomingElement == Element.LIGHTNING) -> {
                    finalDamage *= 1.6f
                    reactionLabel = "ĐIỆN GIẬT!"
                }
                // Wood + Water -> Bloom
                (prev == Element.WOOD && incomingElement == Element.WATER) || (prev == Element.WATER && incomingElement == Element.WOOD) -> {
                    activeHero.currentHp = (activeHero.currentHp + 60f).coerceAtMost(activeHero.totalMaxHp)
                    reactionLabel = "SINH TRƯỞNG!"
                    SoundSystem.playHealChime()
                }
            }
            enemy.currentElementAffliction = Element.NONE
        } else if (incomingElement != Element.NONE) {
            enemy.currentElementAffliction = incomingElement
            enemy.afflictionTimer = 5.0f
        }

        // Crit roll
        val isCrit = Math.random() < activeHero.critRate
        if (isCrit) finalDamage *= 1.5f

        // Defense mitigation
        val effectiveDef = (enemy.def * 0.6f).coerceAtLeast(0f)
        val actualDmg = (finalDamage - effectiveDef).coerceAtLeast(10f)

        enemy.currentHp -= actualDmg

        // Show damage number
        damageNumbers.add(
            DamageNumber(
                id = nextId++,
                x = enemy.x + (Math.random() * 30 - 15).toFloat(),
                y = enemy.y - (if (enemy.isBoss) 60f else 30f),
                value = actualDmg.toInt(),
                isCrit = isCrit,
                element = incomingElement,
                reactionText = reactionLabel
            )
        )

        // Enemy killed
        if (enemy.currentHp <= 0f) {
            enemy.currentHp = 0f
            enemy.state = EnemyState.DEAD
            val leveledUp = activeHero.gainExp(enemy.type.expReward)
            if (leveledUp) {
                SoundSystem.playLevelUp()
                onHeroLevelUp?.invoke(activeHero)
            }
            onEnemyKilled?.invoke(enemy)
        }
    }

    private fun performEnemyAttack(enemy: Enemy) {
        if (enemy.isBoss) {
            SoundSystem.playDragonRoar()
            screenShakeIntensity = 14f
            // Boss shoots dark breath or lightning balls
            val angle = atan2(playerY - enemy.y, playerX - enemy.x)
            for (offset in listOf(-0.3f, 0f, 0.3f)) {
                projectiles.add(
                    Projectile(
                        id = nextId++,
                        x = enemy.x,
                        y = enemy.y,
                        vx = cos(angle + offset) * 320f,
                        vy = sin(angle + offset) * 320f,
                        radius = 22f,
                        damage = enemy.atk * 0.7f,
                        element = Element.DARK,
                        fromPlayer = false,
                        colorHex = 0xFF7C4DFF
                    )
                )
            }
        } else {
            // Melee mob bite or claw
            val dx = playerX - enemy.x
            val dy = playerY - enemy.y
            if (sqrt(dx * dx + dy * dy) < 65f && !isInvulnerable) {
                applyDamageToPlayer(enemy.atk)
            }
        }
    }

    private fun applyDamageToPlayer(rawDamage: Float) {
        val actualDamage = (rawDamage - activeHero.def * 0.5f).coerceAtLeast(5f)
        activeHero.currentHp = (activeHero.currentHp - actualDamage).coerceAtLeast(0f)
        screenShakeIntensity = 10f

        damageNumbers.add(
            DamageNumber(
                id = nextId++,
                x = playerX,
                y = playerY - 30f,
                value = actualDamage.toInt(),
                isCrit = false,
                element = Element.DARK
            )
        )
    }

    private fun spawnHitSparks(x: Float, y: Float, color: Long) {
        for (i in 0 until 8) {
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = (Math.random() * 160 - 80).toFloat(),
                    vy = (Math.random() * 160 - 80).toFloat(),
                    color = color,
                    size = (Math.random() * 5 + 3).toFloat(),
                    lifetime = 0.4f,
                    shape = ParticleShape.SPARK
                )
            )
        }
    }
}
