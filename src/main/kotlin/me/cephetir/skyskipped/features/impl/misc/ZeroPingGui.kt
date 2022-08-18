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

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.Listener
import me.cephetir.skyskipped.event.events.ClickSlotEvent
import me.cephetir.skyskipped.features.Feature
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

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

    @SubscribeEvent
    fun onGuiClick(event: ClickSlotEvent) {
        if (!Cache.inSkyblock || !Config.zeroPingGui || event.button != 0 || event.slot == null) return
        val stack = event.gui.inventorySlots.getSlot(0).stack ?: return
        if (stack.displayName != " " || stack.itemDamage != 15 || ignore.contains(Listener.lastOpenContainerName)) return

        event.isCanceled = true
        mc.playerController.windowClick(event.gui.inventorySlots.windowId, event.slot.slotNumber, 2, 3, mc.thePlayer)
    }
}