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

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.player
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Rift : Feature() {
    private val clicked = Array(5) { false }

    init {
        listener<ClientTickEvent> {
            if (it.phase != TickEvent.Phase.START || !Config.hackingSolver.value || player == null || mc.currentScreen == null) return@listener
            val gui = mc.currentScreen
            if (gui !is GuiChest || !InventoryUtils.getInventoryName(gui)!!.contains("Hacking") || clicked.all { it }) return@listener

            val container = gui.inventorySlots as ContainerChest
            val slots = container.inventorySlots
            for (i in 2..6) {
                if (clicked[i - 2]) continue
                val checking = slots[i]
                if (!checking.hasStack || checking.stack.item != Items.skull) return@listener // gui not loaded
                val numberNeeded = getNumber(checking.stack) ?: return@listener printdev("Failed to get number of $i")

                val slotNum = 9 + 9 * (i - 2) + i
                val slot = slots[slotNum]
                if (!slot.hasStack || slot.stack.item != Items.skull) continue
                val number = getNumber(slot.stack) ?: return@listener printdev("Failed to get number of $i")
                if (numberNeeded == number) {
                    mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, 0, player)
                    clicked[i - 2] = true
                    printdev("Clicked $i")
                    break
                }
            }
        }

        listener<GuiOpenEvent> { clicked.fill(false) }
    }

    private fun getNumber(stack: ItemStack): Int? = stack.tagCompound.getCompoundTag("display")?.getString("Name")?.stripColor()?.toInt()
}