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

package me.cephetir.skyskipped.features.impl.hacks

import gg.essential.universal.UChat
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiEditSign
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.Slot
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ItemSwap : Feature() {

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !Cache.onSkyblock || mc.currentScreen is GuiChat || mc.currentScreen is GuiEditSign) return

        var flag = false
        for (keybind in SkySkipped.keybinds) {
            val down = keybind.isDown(true)
            if (down == keybind.lastState) continue
            keybind.lastState = down
            if (!down) continue

            if (mc.currentScreen !is GuiInventory && !flag) {
                mc.displayGuiScreen(GuiInventory(mc.thePlayer))
                flag = true
            }

            val screen = (mc.currentScreen as GuiInventory).inventorySlots

            val names = keybind.message.split(":")
            if (names.size < 2) {
                UChat.chat("§cSkySkipped §f:: §4Invalid item name format!")
                continue
            }
            var first: Slot? = null
            var second: Slot? = null

            for (slot in screen.inventorySlots) {
                val item = slot.stack ?: continue
                if (item.displayName.contains(names[0], true)) first = slot
                else if (item.displayName.contains(names[1], true)) second = slot
            }

            if (first != null && second != null) {
                //mc.playerController.windowClick(screen.windowId, first.slotNumber, 0, 0, mc.thePlayer)
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(screen.windowId, first.slotNumber, 0, 0, first.stack, 0))
                mc.thePlayer.openContainer.slotClick(first.slotNumber, 0, 0, mc.thePlayer)

                //mc.playerController.windowClick(screen.windowId, second.slotNumber, 0, 0, mc.thePlayer)
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(screen.windowId, second.slotNumber, 0, 0, second.stack, 0))
                mc.thePlayer.openContainer.slotClick(second.slotNumber, 0, 0, mc.thePlayer)

                //mc.playerController.windowClick(screen.windowId, first.slotNumber, 0, 0, mc.thePlayer)
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(screen.windowId, first.slotNumber, 0, 0, first.stack, 0))
                mc.thePlayer.openContainer.slotClick(first.slotNumber, 0, 0, mc.thePlayer)
            } else UChat.chat("§cSkySkipped §f:: §4Can't find first or second item to swap!")
        }

        if (flag) mc.thePlayer.closeScreen()
    }

    private fun GuiItemSwap.Keybind.isDown(inGui: Boolean = false) =
        if (this.keyCode == Keyboard.KEY_NONE || (!inGui && (mc.currentScreen != null || mc.currentScreen is GuiChat))) false
        else Keyboard.isKeyDown(this.keyCode)
}