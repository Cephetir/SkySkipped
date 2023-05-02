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

package me.cephetir.skyskipped.features.impl.misc

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.player
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.ClickSlotEvent
import me.cephetir.skyskipped.features.Feature

class ZeroPingGui : Feature() {
    private val ignore = listOf(
        "Craft Item",
        "Storage",
        "Reforge Item (Advanced)",
        "Gemstone Grinder",
        "Anvil",
        "Enchant Item",
        "Personal",
        "Minion",
        "Pet Sitter",
        "Offer Pets",
        "Item Frame",
        "Brewing Stand",
        "Runic Pedestal",
        "Sack",
        "Present Item"
    )
    private val clicked = ArrayList<Int>()
    private var currentlyVisible = -1

    init {
        listener<ClickSlotEvent> {
            if (!Cache.onSkyblock || !Config.zeroPingGui.value || it.button != 0 || it.slot == null) return@listener
            val stack = it.gui.inventorySlots.getSlot(0).stack ?: return@listener
            if (stack.displayName != " " || stack.itemDamage != 15 || ignore.contains(SkyblockListener.lastOpenContainerName)) return@listener

            it.cancel()
            var window = it.gui.inventorySlots.windowId
            if (window != currentlyVisible) {
                clicked.clear()
                clicked.add(window)
                currentlyVisible = window
            } else {
                while (clicked.contains(window)) window++
                clicked.add(window)
            }
            mc.playerController.windowClick(window, it.slot.slotNumber, 2, 3, player)
        }
    }
}