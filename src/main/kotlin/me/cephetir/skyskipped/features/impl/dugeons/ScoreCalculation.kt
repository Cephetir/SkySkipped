/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2022 Cephetir
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 */

package me.cephetir.skyskipped.features.impl.dugeons

import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.PacketReceive
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.PingUtils
import me.cephetir.skyskipped.utils.TextUtils.containsAny
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraft.network.play.server.S3EPacketTeams
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import skytils.skytilsmod.features.impl.handlers.MayorInfo
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class ScoreCalculation : Feature() {
    var isSTLoaded = false

    var bloodCleared = false
    var bloodFound = false
    var hasBossSpawned = false
    var floor = "default"

    private val deathsTabPattern = Regex("§r§a§lDeaths: §r§f\\((?<deaths>\\d+)\\)§r")
    private val missingPuzzlePattern = Regex("§r§b§lPuzzles: §r§f\\((?<count>\\d)\\)§r")
    private val failedPuzzlePattern =
        Regex("§r (?<puzzle>.+): §r§7\\[§r§c§l✖§r§7] §.+")
    private val solvedPuzzlePattern =
        Regex("§r (?<puzzle>.+): §r§7\\[§r§a§l✔§r§7] §.+")
    private val secretsFoundPattern = Regex("§r Secrets Found: §r§b(?<secrets>\\d+)§r")
    private val secretsFoundPercentagePattern = Regex("§r Secrets Found: §r§[ae](?<percentage>[\\d.]+)%§r")
    private val cryptsPattern = Regex("§r Crypts: §r§6(?<crypts>\\d+)§r")
    private val dungeonClearedPattern = Regex("Dungeon Cleared: (?<percentage>\\d+)%")
    private val timeElapsedPattern =
        Regex(" Elapsed: (?:(?<hrs>\\d+)h )?(?:(?<min>\\d+)m )?(?:(?<sec>\\d+)s)?")
    private val roomCompletedPattern = Regex("§r Completed Rooms: §r§d(?<count>\\d+)§r")

    val floorRequirements = hashMapOf(
        "E" to FloorRequirement(.3),
        "F1" to FloorRequirement(.3),
        "F2" to FloorRequirement(.4),
        "F3" to FloorRequirement(.5),
        "F4" to FloorRequirement(.6),
        "F5" to FloorRequirement(.7, 12 * 60),
        "F6" to FloorRequirement(.85),
        "F7" to FloorRequirement(speed = 12 * 60),
        "M1" to FloorRequirement(speed = 8 * 60),
        "M2" to FloorRequirement(speed = 8 * 60),
        "M3" to FloorRequirement(speed = 8 * 60),
        "M4" to FloorRequirement(speed = 8 * 60),
        "M5" to FloorRequirement(speed = 8 * 60),
        "M6" to FloorRequirement(speed = 8 * 60),
        "M7" to FloorRequirement(speed = 8 * 60),
        "default" to FloorRequirement()
    )

    var completedRooms = BasicState(0)
    var clearedPercentage = BasicState(0)
    val totalRoomMap = mutableMapOf<Int, Int>()
    val totalRooms = (clearedPercentage.zip(completedRooms)).map { (clear, complete) ->
        val a = if (clear > 0 && complete > 0) {
            (100 * (complete / clear.toDouble())).roundToInt()
        } else 0
        if (a == 0) return@map 0
        totalRoomMap[a] = (totalRoomMap[a] ?: 0) + 1
        totalRoomMap.toList().maxByOrNull { it.second }!!.first
    }
    val calcingCompletedRooms = completedRooms.map {
        it + (!bloodFound).ifTrue(1) + (!hasBossSpawned).ifTrue(1)
    }
    val calcingClearPercentage = calcingCompletedRooms.map { complete ->
        val total = totalRooms.get()
        val a = if (total > 0) (complete / total.toDouble()).coerceAtMost(1.0) else 0.0
        a
    }
    val roomClearScore = calcingClearPercentage.map {
        (60 * it).coerceIn(0.0, 60.0)
    }

    var floorReq = BasicState(floorRequirements["default"]!!)
    var foundSecrets: State<Int> = BasicState(0)
    var totalSecrets = BasicState(0)
    var totalSecretsNeeded = (floorReq.zip(totalSecrets)).map { (req, total) ->
        if (total == 0) return@map 1
        ceil(total * req.secretPercentage).toInt()
    }
    val percentageOfNeededSecretsFound = (foundSecrets.zip(totalSecretsNeeded)).map { (found, totalNeeded) ->
        found / totalNeeded.toDouble()
    }
    val secretScore = (totalSecrets.zip(percentageOfNeededSecretsFound)).map { (total, percent) ->
        if (total <= 0)
            0.0
        else
            (40f * percent).coerceIn(0.0, 40.0)
    }

    val discoveryScore = (roomClearScore.zip(secretScore)).map { (clear, secret) ->
        (clear + secret).toInt()
    }

    var deaths = BasicState(0)
    var firstDeathHadSpirit = BasicState(false)
    val deathPenalty = (deaths.zip(firstDeathHadSpirit)).map { (deathCount, spirit) ->
        (2 * deathCount) - (if (spirit) 1 else 0)
    }

    var missingPuzzles = BasicState(0)
    var failedPuzzles = BasicState(0)
    val puzzlePenalty = (missingPuzzles.zip(failedPuzzles)).map { (missing, failed) ->
        10 * (missing + failed)
    }

    val skillScore = (calcingClearPercentage.zip(deathPenalty.zip(puzzlePenalty))).map { (clear, penalties) ->
        (20.0 + clear * 80.0 - penalties.first - penalties.second)
            .toInt()
    }

    var secondsElapsed = BasicState(0.0)
    val overtime = (secondsElapsed.zip(floorReq)).map { (seconds, req) ->
        seconds - req.speed
    }
    val totalElapsed = (secondsElapsed.zip(floorReq)).map { (seconds, req) ->
        seconds + 480 - req.speed
    }
    val speedScore = totalElapsed.map { time ->
        when {
            time <= 480.0 -> 100.0
            time <= 580.0 -> 148 - 0.1 * time
            time <= 980 -> 119 - 0.05 * time
            time < 3060 -> 102 - 1.0 / 30.0 * time
            else -> 0.0
        }.roundToInt()
    }

    var crypts = BasicState(0)
    var mimicKilled = BasicState(false)
    var isPaul =
        BasicState(if (isSTLoaded) (MayorInfo.currentMayor == "Paul" && MayorInfo.mayorPerks.contains("EZPZ")) || MayorInfo.jerryMayor?.name == "Paul" else false)
    val bonusScore = (crypts.zip(mimicKilled.zip(isPaul))).map { (crypts, bools) ->
        (if (bools.first) 2 else 0) + crypts.coerceAtMost(5) + if (bools.second) 10 else 0
    }

    val totalScore =
        ((skillScore.zip(discoveryScore)).zip(speedScore.zip(bonusScore))).map { (first, second) ->
            first.first.coerceIn(20, 100) + first.second + second.first + second.second
        }

    @SubscribeEvent
    fun onPacket(event: PacketReceive) {
        if (!Cache.isInDungeon) return
        if (event.packet is S3EPacketTeams) {
            val packet = event.packet as S3EPacketTeams
            if (packet.action != 2) return
            val line = packet.players.joinToString(
                " ",
                prefix = packet.prefix,
                postfix = packet.suffix
            ).stripColor()
            if (line.startsWith("Dungeon Cleared: ")) {
                val matcher = dungeonClearedPattern.find(line)
                if (matcher != null) {
                    clearedPercentage.set(matcher.groups["percentage"]?.value?.toIntOrNull() ?: 0)
                    return
                }
            } else if (line.startsWith("Time Elapsed:")) {
                val matcher = timeElapsedPattern.find(line)
                if (matcher != null) {
                    val hours = matcher.groups["hrs"]?.value?.toIntOrNull() ?: 0
                    val minutes = matcher.groups["min"]?.value?.toIntOrNull() ?: 0
                    val seconds = matcher.groups["sec"]?.value?.toIntOrNull() ?: 0
                    secondsElapsed.set((hours * 3600 + minutes * 60 + seconds).toDouble())
                    return
                }
            } else if (line.contains("The Catacombs (")) {
                floor = line.substringAfter("(").substringBefore(")")
                floorReq.set(floorRequirements[floor] ?: floorRequirements["default"]!!)
            }
        } else if (event.packet is S38PacketPlayerListItem && ((event.packet as S38PacketPlayerListItem).action == S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME || (event.packet as S38PacketPlayerListItem).action == S38PacketPlayerListItem.Action.ADD_PLAYER)) {
            val packet = event.packet as S38PacketPlayerListItem
            packet.entries.forEach { playerData ->
                val name = playerData?.displayName?.formattedText ?: playerData?.profile?.name ?: return@forEach
                when {
                    name.contains("Deaths:") -> {
                        val matcher = deathsTabPattern.find(name) ?: return@forEach
                        deaths.set(matcher.groups["deaths"]?.value?.toIntOrNull() ?: 0)
                    }
                    name.contains("Puzzles:") -> {
                        println(name)
                        val matcher = missingPuzzlePattern.find(name) ?: return@forEach
                        missingPuzzles.set(matcher.groups["count"]?.value?.toIntOrNull() ?: 0)
                    }
                    name.contains("✔") -> {
                        if (solvedPuzzlePattern.containsMatchIn(name)) {
                            missingPuzzles.set((missingPuzzles.get() - 1).coerceAtLeast(0))
                        }
                    }
                    name.contains("✖") -> {
                        if (failedPuzzlePattern.containsMatchIn(name)) {
                            missingPuzzles.set((missingPuzzles.get() - 1).coerceAtLeast(0))
                            failedPuzzles.set(failedPuzzles.get() + 1)
                        }
                    }
                    name.contains("Secrets Found:") -> {
                        if (name.contains("%")) {
                            val matcher = secretsFoundPercentagePattern.find(name) ?: return@forEach
                            val percentagePer = (matcher.groups["percentage"]?.value?.toDoubleOrNull()
                                ?: 0.0)
                            totalSecrets.set(
                                if (foundSecrets.get() > 0 && percentagePer > 0) floor(100f / percentagePer * foundSecrets.get() + 0.5).toInt() else 0
                            )
                        } else {
                            val matcher = secretsFoundPattern.find(name) ?: return@forEach
                            foundSecrets.set(matcher.groups["secrets"]?.value?.toIntOrNull() ?: 0)
                        }
                    }
                    name.contains("Crypts:") -> {
                        val matcher = cryptsPattern.find(name) ?: return@forEach
                        crypts.set(matcher.groups["crypts"]?.value?.toIntOrNull() ?: 0)
                    }
                    name.contains("Completed Rooms") -> {
                        val matcher = roomCompletedPattern.find(name) ?: return@forEach
                        completedRooms.set(matcher.groups["count"]?.value?.toIntOrNull() ?: return@forEach)
                    }
                }
            }
        }
    }

    fun onWorldUnload(event: WorldEvent.Unload) {
        mimicKilled.set(false)
        firstDeathHadSpirit.set(false)
        floorReq.set(floorRequirements["default"]!!)
        missingPuzzles.set(0)
        failedPuzzles.set(0)
        secondsElapsed.set(0.0)
        foundSecrets.set(0)
        totalSecrets.set(0)
        clearedPercentage.set(0)
        deaths.set(0)
        crypts.set(0)
        totalRoomMap.clear()
        bloodCleared = false
    }


    override fun isEnabled(): Boolean {
        return true
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Cache.isInDungeon) return
        val unformatted: String = event.message.unformattedText.stripColor()
        if (unformatted.startsWith("Party > ")) {
            if (unformatted.contains("SKYTILS-DUNGEON-SCORE-MIMIC$") || (unformatted.containsAny(
                    "Mimic dead!", "Mimic Killed!"
                ))
            ) {
                mimicKilled.set(true)
                return
            }
            if (unformatted.contains("SKYTILS-DUNGEON-SCORE-ROOM$")) {
                event.isCanceled = true
                return
            }
        } else if (unformatted.contains(":") && unformatted.containsAny(
                "Mimic dead!",
                "Mimic Killed!",
                "Mimic Dead!",
                "SKYTILS-DUNGEON-SCORE-MIMIC$"
            )
        ) {
            mimicKilled.set(true)
            return
        } else if (unformatted.contains("You have proven yourself. You may pass")) bloodCleared = true
        else if (unformatted.lowercase() == "the blood door has been opened!") bloodFound = true
        else if (unformatted.startsWith("[BOSS]") && unformatted.contains(":")) {
            val bossName = unformatted.substringAfter("[BOSS] ").substringBefore(":").trim()
            if (!hasBossSpawned && bossName != "The Watcher" && floor != "default" && checkBossName(floor, bossName))
                hasBossSpawned = true
        }
    }

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        if (!Cache.isInDungeon) return
        if (event.entity is EntityZombie) {
            val entity = event.entity as EntityZombie
            if (entity.isChild && entity.getCurrentArmor(0) == null && entity.getCurrentArmor(1) == null && entity.getCurrentArmor(
                    2
                ) == null && entity.getCurrentArmor(3) == null
            ) if (!mimicKilled.get()) mimicKilled.set(true)
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !Cache.isInDungeon) return

        if ((totalScore.get() >= 300) && !Cache.was && Config.scorePing) {
            println(totalScore.get())
            PingUtils(100, "300 score reached!")
            mc.thePlayer.sendChatMessage(Config.pingText)
            Cache.was = true
        }
        if (bloodCleared && !Cache.was2 && Config.rabbitPing) {
            PingUtils(100, "Rabbit Hat!")
            Cache.was2 = true
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_N)) { // TODO: FIX CALC IDK FUCK THIS SHIT
            println("Crypts ${crypts.get()} Mimic ${mimicKilled.get()} Paul ${isPaul.get()}")
            println("Total Score ${totalScore.get()} Skill ${skillScore.get()} Discovery ${discoveryScore.get()} Speed ${speedScore.get()} Bonus ${bonusScore.get()}")
        }
    }

    data class FloorRequirement(val secretPercentage: Double = 1.0, val speed: Int = 10 * 60)

    private fun Boolean.ifTrue(num: Int) = if (this) num else 0

    private fun checkBossName(floor: String, bossName: String): Boolean {
        val correctBoss = when (floor) {
            "E" -> "The Watcher"
            "F1", "M1" -> "Bonzo"
            "F2", "M2" -> "Scarf"
            "F3", "M3" -> "The Professor"
            "F4", "M4" -> "Thorn"
            "F5", "M5" -> "Livid"
            "F6", "M6" -> "Sadan"
            "F7", "M7" -> "Necron"
            else -> null
        } ?: return false

        return bossName.endsWith(correctBoss)
    }
}
