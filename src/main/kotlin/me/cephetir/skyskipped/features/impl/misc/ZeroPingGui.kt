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