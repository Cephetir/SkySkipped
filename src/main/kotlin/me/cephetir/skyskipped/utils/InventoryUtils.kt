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

package me.cephetir.skyskipped.utils

import me.cephetir.bladecore.utils.TextUtils.containsAny
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack


object InventoryUtils {
    @JvmStatic
    fun getInventory(): List<Slot> = mc.thePlayer.inventoryContainer.inventorySlots.subList(9, 44)

    @JvmStatic
    fun isFull(): Boolean {
        val inv = getInventory()
        val newInv = mutableListOf<Slot>()
        for (slot in inv) if (slot.hasStack) newInv.add(slot)
        return inv.size == newInv.size
    }

    @JvmStatic
    fun findItemInHotbar(vararg name: String): Int {
        val names = name.toList()
        val inv = mc.thePlayer.inventory
        for (i in 0..8) {
            val curStack = inv.getStackInSlot(i)
            if (curStack != null && curStack.displayName.containsAny(names))
                return i
        }
        return -1
    }

    @JvmStatic
    fun getInventoryName(gui: GuiScreen = mc.currentScreen): String? {
        if (gui is GuiChest) {
            val chest = gui.inventorySlots as ContainerChest
            val inv = chest.lowerChestInventory
            return inv.displayName.formattedText
        }
        return null
    }

    fun ContainerChest.getName(): String = lowerChestInventory.displayName.formattedText

    @JvmStatic
    fun getStackInSlot(slot: Int): ItemStack? = mc.thePlayer.inventory.getStackInSlot(slot)

    @JvmStatic
    fun getStackInOpenContainerSlot(slot: Int): ItemStack? =
        if (mc.thePlayer.openContainer?.inventorySlots?.get(slot)?.hasStack == true) mc.thePlayer.openContainer.inventorySlots[slot].stack else null
}