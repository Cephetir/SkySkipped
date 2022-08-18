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

package me.cephetir.skyskipped.event

import com.google.gson.Gson
import gg.essential.api.EssentialAPI
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.event.events.PacketEvent
import me.cephetir.skyskipped.event.events.SendChatMessageEvent
import me.cephetir.skyskipped.features.impl.dugeons.AdminRoomDetection
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.mc
import me.cephetir.skyskipped.utils.skyblock.ScoreboardUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.apache.commons.lang3.SerializationException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Listener {
    private val timePattern = ".+(am|pm)".toRegex()

    var location = ""
    var date = ""
    var time = ""
    var objective: String? = ""
    var island = SkyblockIsland.Unknown
    var currentTimeDate: Date? = null
    var jacobEvent = false

    @JvmField
    var lastOpenContainerName: String? = null
    private var lastLocRaw = -1L
    private var joinedWorld: Long = -1
    var locraw: LocrawObject? = null
    private val junkRegex = Regex("[^\u0020-\u0127û]")

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onGuiOpen(event: GuiOpenEvent) {
        if (!Cache.inSkyblock) return
        if (event.gui is GuiChest) {
            val chest = event.gui as GuiChest
            val container = chest.inventorySlots as ContainerChest
            val containerName = container.lowerChestInventory.displayName.unformattedText
            lastOpenContainerName = containerName
        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load?) {
        locraw = null
        island = SkyblockIsland.Unknown
        lastLocRaw = -1
        joinedWorld = System.currentTimeMillis()
        lastOpenContainerName = null
        Cache.inSkyblock = false
        AdminRoomDetection.scanned = false
    }

    @SubscribeEvent
    fun onSendChatMessage(event: SendChatMessageEvent) {
        val msg = event.message
        if (msg.trim().startsWith("/locraw"))
            lastLocRaw = System.currentTimeMillis()
    }

    private val gson = Gson()

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    fun onChatMessagePacket(event: ClientChatReceivedEvent) {
        val unformatted = event.message.unformattedText
        if (!unformatted.startsWith("{") || !unformatted.endsWith("}")) return
        try {
            val obj = gson.fromJson(unformatted, LocrawObject::class.java)
            event.isCanceled = true
            locraw = obj
            island = SkyblockIsland.values().find { it.mode == obj.mode } ?: SkyblockIsland.Unknown
        } catch (e: SerializationException) {
            e.printStackTrace()
        }
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.SendEvent) {
        if (EssentialAPI.getMinecraftUtil().isHypixel() && event.packet is C01PacketChatMessage)
            if (event.packet.message.startsWith("/locraw"))
                lastLocRaw = System.currentTimeMillis()
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null || !EssentialAPI.getMinecraftUtil().isHypixel()) return

        val currentTime = System.currentTimeMillis()
        if (((locraw == null && currentTime - lastLocRaw > 300_000) || ((locraw?.server ?: "") == "limbo" && currentTime - lastLocRaw > 5_000)) && currentTime - joinedWorld > 1300) {
            lastLocRaw = System.currentTimeMillis()
            mc.thePlayer.sendChatMessage("/locraw")
        }

        val scoreObjective = mc.thePlayer.worldScoreboard.getObjectiveInDisplaySlot(1) ?: return onWorldChange(null)
        Cache.inSkyblock = scoreObjective.displayName.stripColor().startsWith("SKYBLOCK")
        if (!Cache.inSkyblock) return

        try {
            val lines = ScoreboardUtils.sidebarLines.map { it.stripColor().keepScoreboardCharacters() }

            //§707/14/20
            date = lines[2].stripColor().trim()
            //§74:40am
            val matcher = timePattern.find(lines[3])
            if (matcher != null) {
                time = matcher.groupValues[0].stripColor().trim()
                try {
                    val timeSpace = time.replace("am", " am").replace("pm", " pm")
                    val parseFormat = SimpleDateFormat("hh:mm a")
                    currentTimeDate = parseFormat.parse(timeSpace)
                } catch (_: ParseException) {
                }
            }

            lines.find { it.contains('⏣') }?.replace(junkRegex, "")?.trim()?.let {
                location = it
            }

            objective = null
            for ((i, line) in lines.withIndex())
                if (line == "Objective") {
                    objective = lines.elementAt(i + 1)
                    break
                }

            val jacob = lines.find { it.contains("Jacob's Contest") }
            jacobEvent = jacob != null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    data class LocrawObject(
        val server: String,
        val gametype: String = "unknown",
        val mode: String = "unknown",
        val map: String = "unknown"
    )
}

enum class SkyblockIsland(val formattedName: String, val mode: String) {
    PrivateIsland("Private Island", "dynamic"),
    SpiderDen("Spider's Den", "combat_1"),
    CrimsonIsle("Crimson Isle", "crimson_isle"),
    TheEnd("The End", "combat_3"),
    GoldMine("Gold Mine", "mining_1"),
    DeepCaverns("Deep Caverns", "mining_2"),
    DwarvenMines("Dwarven Mines", "mining_3"),
    CrystalHollows("Crystal Hollows", "crystal_hollows"),
    FarmingIsland("The Farming Islands", "farming_1"),
    ThePark("The Park", "foraging_1"),
    Dungeon("Dungeon", "dungeon"),
    DungeonHub("Dungeon Hub", "dungeon_hub"),
    Hub("Hub", "hub"),
    DarkAuction("Dark Auction", "dark_auction"),
    JerryWorkshop("Jerry's Workshop", "winter"),
    Instanced("Instanced", "instanced"),
    Unknown("Unknown", "");

    override fun toString(): String = mode
}