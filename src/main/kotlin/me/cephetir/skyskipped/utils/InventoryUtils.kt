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

package me.cephetir.skyskipped.utils

import net.minecraft.inventory.Slot

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
}