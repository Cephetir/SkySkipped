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

package me.cephetir.skyskipped.features.impl.chat

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.ScoreboardUtils
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class AutoMaddoxPhone : Feature() {
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!Config.autoMaddox || !Cache.inSkyblock || event.phase != TickEvent.Phase.START) return
        val scoreboard: List<String> = ScoreboardUtils.sidebarLines

        for (line in scoreboard) {
            val strippedLine = line.stripColor().keepScoreboardCharacters().trim()
            if (!strippedLine.contains("Boss slain!")) return

            val maddox = findItemInHotbar("Batphone")
            if (maddox != -1) {
                val save = mc.thePlayer.inventory.currentItem

                mc.thePlayer.inventory.currentItem = maddox
                mc.playerController.sendUseItem(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(maddox)
                )

                mc.thePlayer.inventory.currentItem = save
                break
            }
        }
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Config.autoMaddox || !Cache.inSkyblock) return
        val message = event.message.unformattedText.stripColor()
        if (message.contains("[OPEN MENU]") && !message.contains(":")) {
            val command = event.message.siblings.find { it.unformattedText.contains("[OPEN MENU]") }?.chatStyle?.chatClickEvent?.value ?: return
            mc.thePlayer.sendChatMessage(command)
        }
    }

    private fun findItemInHotbar(name: String): Int {
        val inv = mc.thePlayer.inventory
        for (i in 0..8) {
            val curStack = inv.getStackInSlot(i)
            if (curStack != null && curStack.displayName.contains(name))
                return i
        }
        return -1
    }
}