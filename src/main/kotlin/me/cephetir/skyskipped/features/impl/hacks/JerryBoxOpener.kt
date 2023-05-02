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
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class JerryBoxOpener : Feature() {
    private var toggled = false
    private var lastWindow = -1
    private var lastClick = -1L

    private var keybindLastState = false

    init {
        listener<ClientTickEvent> {
            if (player == null || world == null || mc.currentScreen != null) return@listener

            val down = Config.boxKeybind.isKeyDown()
            if (down == keybindLastState) return@listener
            keybindLastState = down
            if (!down) return@listener

            lastWindow = -1
            lastClick = -1L
            toggled = !toggled
            UChat.chat("§cSkySkipped §f:: §eJerry Box Opener ${if (toggled) "§aEnabled" else "§cDisabled"}!")
        }

        listener<ClientTickEvent> {
            if (!toggled) return@listener
            if (player == null || world == null) {
                toggled = false
                return@listener
            }
            if (mc.currentScreen != null || System.currentTimeMillis() - lastClick < Config.boxDelay.value) return@listener

            val box = InventoryUtils.findItemInHotbar("Jerry Box")
            if (box == -1) {
                UChat.chat("§cSkySkipped §f:: §cCannot find jerry boxes in hotbar!")
                toggled = !toggled
                return@listener
            }
            player!!.inventory.currentItem = box
            mc.playerController.sendUseItem(player, world, player!!.heldItem)
            lastClick = System.currentTimeMillis()
        }

        listener<GuiScreenEvent.DrawScreenEvent.Pre> {
            if (!toggled || it.gui !is GuiChest || InventoryUtils.getInventoryName(it.gui)?.contains("Open a Jerry Box") == false)
                return@listener

            val chest = it.gui as GuiChest
            val slot = chest.inventorySlots.getSlot(22)
            if (!slot.hasStack || !slot.stack.hasDisplayName() || (!slot.stack.displayName.contains("Jerry Box") && !slot.stack.displayName.contains("Unknown item")))
                return@listener

            lastWindow = chest.inventorySlots.windowId
            mc.playerController.windowClick(lastWindow, 22, 0, 0, player)
            player!!.closeScreen()
        }
    }
}