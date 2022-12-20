/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        if (!Config.autoMaddox || !Cache.onSkyblock || event.phase != TickEvent.Phase.START) return
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
        if (!Config.autoMaddox || !Cache.onSkyblock) return
        val message = event.message.unformattedText.stripColor()
        if (message.contains("[OPEN MENU]") && !message.contains(":")) {
            val command = event.message.siblings.find { it.unformattedText.contains("[OPEN MENU]") }?.chatStyle?.chatClickEvent?.value ?: return
            Queues.sendCommand(command)
        }
    }
}