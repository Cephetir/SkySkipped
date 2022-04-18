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
import me.cephetir.skyskipped.utils.ScoreboardUtils
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Listener {
    @SubscribeEvent(priority = EventPriority.HIGH)
    fun update(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START &&
            !Minecraft.getMinecraft().isSingleplayer &&
            Minecraft.getMinecraft().theWorld != null &&
            Minecraft.getMinecraft().netHandler != null &&
            EssentialAPI.getMinecraftUtil().isHypixel() &&
            Minecraft.getMinecraft().thePlayer.worldScoreboard != null
        ) {
            var foundDungeon = false
            var foundSkyblock = false
            var foundWorkshop = false
            var foundIsland = false
            var foundJacob = false
            var percentage = 0
            var dungeonName = ""
            var itemheld = "Nothing"

            val scoreObjective = Minecraft.getMinecraft().thePlayer.worldScoreboard.getObjectiveInDisplaySlot(1) ?: return
            val scores = ScoreboardUtils.fetchScoreboardLines()

            if (scoreObjective.displayName.stripColor().startsWith("SKYBLOCK")) foundSkyblock = true

            if (foundSkyblock) {
                for (text in scores) {
                    val strippedLine = text.stripColor().keepScoreboardCharacters().trim()
                    if (strippedLine.contains("Cleared: ")) {
                        foundDungeon = true
                        percentage = strippedLine.substring(9).split(" ")[0].toInt()
                    } else if (text.startsWith(" §7⏣")) {
                        if (foundDungeon) dungeonName = strippedLine.trim()
                        else if (strippedLine.contains("Jerry's Workshop")) foundWorkshop = true
                        else if (strippedLine.contains("Your Island")) foundIsland = true
                    } else if (strippedLine.contains("Jacob's Contest")) foundJacob = true
                }

                if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null)
                    itemheld = Minecraft.getMinecraft().thePlayer.heldItem.displayName
                        .stripColor().keepScoreboardCharacters().trim()
            }

            Cache.inSkyblock = foundSkyblock
            Cache.isInDungeon = foundDungeon
            Cache.inWorkshop = foundWorkshop
            Cache.onIsland = foundIsland
            Cache.isJacob = foundJacob
            Cache.dungeonPercentage = percentage
            Cache.dungeonName = dungeonName
            Cache.itemheld = itemheld
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        Cache.inSkyblock = false
        Cache.isInDungeon = false
    }
}
