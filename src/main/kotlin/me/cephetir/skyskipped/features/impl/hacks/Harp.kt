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
import me.cephetir.bladecore.utils.player
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.Item
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Harp : Feature() {
    private val notes = listOf(Note(37), Note(38), Note(39), Note(40), Note(41), Note(42), Note(43))

    init {
        listener<ClientTickEvent> {
            if (!Config.harp.value || player == null || mc.currentScreen == null) return@listener
            if (InventoryUtils.getInventoryName()?.contains("Harp") != true) return@listener

            val chest = (mc.currentScreen as GuiChest).inventorySlots as ContainerChest
            val inv = chest.lowerChestInventory
            for (note in notes) {
                if (note.delay > 0)
                    note.delay--

                val stack = inv.getStackInSlot(note.slot) ?: continue
                val id = Item.getIdFromItem(stack.item)
                if (id == 159) {
                    note.clicked = false
                    note.delay = 0
                } else if (id == 155) {
                    if (note.clicked || note.delay != 0) continue
                    val stack2 = inv.getStackInSlot(note.slot - 9)
                    val id2 = Item.getIdFromItem(stack2.item)
                    if (id2 == 35) note.delay = 7
                    else note.clicked = true
                    mc.playerController.windowClick(chest.windowId, note.slot, 0, 0, player)
                }
            }
        }
    }

    private data class Note(var slot: Int, var clicked: Boolean = false, var delay: Int = 0)
}