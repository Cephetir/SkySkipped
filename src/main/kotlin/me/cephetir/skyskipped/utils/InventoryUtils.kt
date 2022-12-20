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

package me.cephetir.skyskipped.utils

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
    fun findItemInHotbar(name: String): Int {
        val inv = mc.thePlayer.inventory
        for (i in 0..8) {
            val curStack = inv.getStackInSlot(i)
            if (curStack != null && curStack.displayName.contains(name))
                return i
        }
        return -1
    }

    fun getInventoryName(gui: GuiScreen = mc.currentScreen): String? {
        if (gui is GuiChest) {
            val chest = gui.inventorySlots as ContainerChest
            val inv = chest.lowerChestInventory
            return if (inv.hasCustomName()) inv.displayName.unformattedText else null
        }
        return null
    }

    fun getStackInSlot(slot: Int): ItemStack? = mc.thePlayer.inventory.getStackInSlot(slot)

    fun getStackInOpenContainerSlot(slot: Int): ItemStack? = if (mc.thePlayer.openContainer?.inventorySlots?.get(slot)?.hasStack == true) mc.thePlayer.openContainer.inventorySlots[slot].stack else null
}