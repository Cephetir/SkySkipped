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

import gg.essential.api.EssentialAPI
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.event.events.PacketEvent
import me.cephetir.skyskipped.features.impl.dugeons.AdminRoomDetection
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.skyblock.ScoreboardUtils
import net.minecraft.client.Minecraft
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object Listener {
    private val areaRegex = Regex("§r§b§l(?<area>\\w+): §r§7(?<loc>[\\w ]+)§r")

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun update(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START &&
            Minecraft.getMinecraft().theWorld != null &&
            EssentialAPI.getMinecraftUtil().isHypixel()
        ) {
            var foundSkyblock = false
            var percentage = 0

            val scoreObjective = Minecraft.getMinecraft().thePlayer.worldScoreboard.getObjectiveInDisplaySlot(1)
            if (scoreObjective == null) {
                Cache.inSkyblock = false
                Cache.dungeonPercentage = 0
                return
            }
            val scores = ScoreboardUtils.sidebarLines

            if (scoreObjective.displayName.stripColor().startsWith("SKYBLOCK"))
                foundSkyblock = true

            if (foundSkyblock) {
                scores.find { it.stripColor().keepScoreboardCharacters().contains("Cleared: ") }?.let {
                    percentage = it.stripColor().keepScoreboardCharacters().substring(9).split(" ")[0].toInt()
                }
            }

            Cache.inSkyblock = foundSkyblock
            Cache.dungeonPercentage = percentage
        }
    }

    @SubscribeEvent
    fun onPacketReceive(event: PacketEvent.ReceiveEvent) {
        if (event.packet is S38PacketPlayerListItem &&
            (event.packet.action == S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME || event.packet.action == S38PacketPlayerListItem.Action.ADD_PLAYER) &&
            !Minecraft.getMinecraft().isSingleplayer &&
            Minecraft.getMinecraft().theWorld != null &&
            Minecraft.getMinecraft().netHandler != null &&
            EssentialAPI.getMinecraftUtil().isHypixel()
        ) {
            event.packet.entries.forEach { playerData ->
                val name = playerData?.displayName?.formattedText ?: playerData?.profile?.name ?: return@forEach
                areaRegex.matchEntire(name)?.let { result ->
                    Cache.softInDungeon = Cache.inSkyblock && result.groups["area"]?.value == "Dungeon"
                    return@forEach
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        Cache.inSkyblock = false
        Cache.softInDungeon = false
        Cache.dungeonPercentage = 0
        AdminRoomDetection.scanned = false
    }
}
