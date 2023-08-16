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

import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoAttribPickup : Feature() {
    private var ticks = 0

    init {
        safeListener<ClientTickEvent> {
            if (!Config.autoAttribPickup.value || mc.currentScreen == null || mc.currentScreen !is GuiChest || ++ticks % 5 != 0) return@safeListener

            val include = if (Config.autoAttribPickupInclude.value.contains(",")) Config.autoAttribPickupInclude.value.split(",").map { it.trim() }
            else Config.autoAttribPickupInclude.value
            val exclude = if (Config.autoAttribPickupExclude.value.contains(",")) Config.autoAttribPickupExclude.value.split(",").map { it.trim() }
            else Config.autoAttribPickupExclude.value

            val chest = (mc.currentScreen as GuiChest).inventorySlots as ContainerChest
            for (slot in chest.inventorySlots.take(chest.lowerChestInventory.sizeInventory)) {
                if (!slot.hasStack) continue

                val display = slot.stack.tagCompound?.getCompoundTag("display")
                if (display?.getTagId("Lore")?.toInt() != 9) continue
                val lore = display.getTagList("Lore", 8)
                if (lore.tagCount() <= 0) continue

                var hasNeeded = false
                var hasNotNeeded = false
                for (i in 0 until lore.tagCount()) {
                    val text = lore.getStringTagAt(i)

                    // THIS IS BEST CODE EVER, YOU DONT UNDERSTAND
                    if (if (include is String) text.contains(include) else if (include is List<*>) text.containsAny(include as List<String>) else false)
                        hasNeeded = true
                    else if (if (exclude is String) text.contains(exclude) else if (exclude is List<*>) text.containsAny(exclude as List<String>) else false)
                        hasNotNeeded = true
                    // THIS IS BEST CODE EVER, YOU DONT UNDERSTAND

                    if (hasNeeded && hasNotNeeded) break
                }

                if (hasNeeded && !hasNotNeeded) {
                    playerController.windowClick(chest.windowId, slot.slotNumber, 0, 1, player)
                    return@safeListener
                }
            }
        }
    }
}