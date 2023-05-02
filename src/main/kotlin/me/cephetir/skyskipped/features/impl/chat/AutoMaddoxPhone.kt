/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cephetir.skyskipped.features.impl.chat

import me.cephetir.bladecore.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.minecraft.skyblock.ScoreboardUtils
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class AutoMaddoxPhone : Feature() {
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!Config.autoMaddox.value || !Cache.onSkyblock || event.phase != TickEvent.Phase.START) return
        val scoreboard = ScoreboardUtils.sidebarLines

        for (line in scoreboard) {
            val strippedLine = line.stripColor().keepScoreboardCharacters().trim()
            if (!strippedLine.contains("Boss slain!")) return

            val maddox = InventoryUtils.findItemInHotbar("Batphone")
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
        if (!Config.autoMaddox.value || !Cache.onSkyblock) return
        val message = event.message.unformattedText.stripColor()
        if (message.contains("[OPEN MENU]") && !message.contains(":")) {
            val command = event.message.siblings.find { it.unformattedText.contains("[OPEN MENU]") }?.chatStyle?.chatClickEvent?.value ?: return
            Queues.sendCommand(command)
        }
    }
}