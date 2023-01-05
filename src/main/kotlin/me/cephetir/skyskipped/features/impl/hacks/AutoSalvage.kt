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

import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.TextUtils.equalsAny
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.minecraft.skyblock.ItemUtils.getExtraAttributes
import me.cephetir.bladecore.utils.minecraft.skyblock.ItemUtils.getSkyBlockID
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
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
        if (event.phase != TickEvent.Phase.START || !Cache.onSkyblock || !Config.autoSalvage || stop) return
        if (mc.currentScreen !is GuiChest || !(SkyblockListener.lastOpenContainerName?.stripColor()?.contains("Salvage Item") ?: return)) return
        if (tickTimer++ % 10 != 0) return

        val chest = mc.currentScreen as GuiChest
        val slotSalvage = chest.inventorySlots.getSlot(31)
        val slotItem = chest.inventorySlots.getSlot(22)
        if (!slotSalvage.hasStack) return

        if (slotItem.hasStack)
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 31, 0, 0, mc.thePlayer)
        else {
            val itemsToSalvage = mc.thePlayer.openContainer.inventorySlots.filter { it.hasStack && shouldSalvage(it.stack) }
            if (itemsToSalvage.isEmpty()) stop = true
            else mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, itemsToSalvage.first().slotNumber, 0, 1, mc.thePlayer)
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
        return !(!attributes.hasKey("baseStatBoostPercentage") || attributes.hasKey("dungeon_item_level") || item.getSkyBlockID() == "ICE_SPRAY_WAND") ||
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