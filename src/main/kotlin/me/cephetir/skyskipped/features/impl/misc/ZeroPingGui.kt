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

    init {
        listener<ClickSlotEvent> {
            if (!Cache.onSkyblock || !Config.zeroPingGui || it.button != 0 || it.slot == null) return@listener
            val stack = it.gui.inventorySlots.getSlot(0).stack ?: return@listener
            if (stack.displayName != " " || stack.itemDamage != 15 || ignore.contains(SkyblockListener.lastOpenContainerName)) return@listener

            it.cancel()
            mc.playerController.windowClick(it.gui.inventorySlots.windowId, it.slot.slotNumber, 2, 3, player)
        }
    }
}