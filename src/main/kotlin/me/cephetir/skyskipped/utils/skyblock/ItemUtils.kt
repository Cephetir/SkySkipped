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

package me.cephetir.skyskipped.utils.skyblock

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound


object ItemUtils {
    fun ItemStack.getExtraAttributes(): NBTTagCompound? = this.getSubCompound("ExtraAttributes", false)

    fun ItemStack.getSkyBlockID(): String {
        val extraAttributes = this.getExtraAttributes()
        return if (extraAttributes != null && extraAttributes.hasKey("id"))
            extraAttributes.getString("id")
        else ""
    }
}