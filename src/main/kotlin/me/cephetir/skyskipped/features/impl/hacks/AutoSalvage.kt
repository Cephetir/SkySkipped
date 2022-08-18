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

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.Listener
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.equalsAny
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.skyblock.ItemUtils.getExtraAttributes
import me.cephetir.skyskipped.utils.skyblock.ItemUtils.getSkyBlockID
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


class AutoSalvage : Feature() {
    private var tickTimer = 0
    private var stop = false

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END || !Cache.inSkyblock || !Config.autoSalvage || stop) return
        if (mc.currentScreen !is GuiChest || !(Listener.lastOpenContainerName?.stripColor()?.contains("Salvage Item") ?: return)) return
        if (tickTimer++ % 5 != 0) return

        val chest = mc.currentScreen as GuiChest
        val slotSalvage = chest.inventorySlots.getSlot(31)
        val slotItem = chest.inventorySlots.getSlot(22)
        if (!slotSalvage.hasStack) return

        if (slotItem.hasStack)
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 31, 2, 0, mc.thePlayer)
        else {
            val itemsToSalvage = mc.thePlayer.inventoryContainer.inventorySlots.filter { it.hasStack && shouldSalvage(it.stack) }
            if (itemsToSalvage.isEmpty()) stop = true
            else mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 45 + itemsToSalvage[0].slotNumber, 0, 1, mc.thePlayer)
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: GuiOpenEvent) {
        stop = false
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        stop = false
    }

    private fun shouldSalvage(item: ItemStack): Boolean {
        val attributes = item.getExtraAttributes() ?: return false
                // Dungeons
        return (attributes.hasKey("baseStatBoostPercentage") && !attributes.hasKey("dungeon_item_level") && item.getSkyBlockID() != "ICE_SPRAY_WAND") ||
                // Lava Fishing
                item.getSkyBlockID().equalsAny(
                    "BLADE_OF_THE_VOLCANO",
                    "MOOGMA_LEGGINGS",
                    "SLUG_BOOTS",
                    "FLAMING_CHESTPLATE",
                    "TAURUS_HELMET",
                    "STAFF_OF_THE_VOLCANO"
                )
    }
}