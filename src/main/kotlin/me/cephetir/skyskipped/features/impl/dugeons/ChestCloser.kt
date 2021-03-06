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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ChestCloser : Feature() {

    @SubscribeEvent
    fun onDrawBackground(event: DrawScreenEvent.Pre) {
        if (event.gui !is GuiChest || !Cache.inSkyblock) return
        val chestName = ((event.gui as GuiChest).inventorySlots as ContainerChest).lowerChestInventory.displayName.unformattedText
        if (Cache.inDungeon && Config.chestCloser && chestName == "Chest") {
            mc.thePlayer.closeScreen()
            event.isCanceled = true
        }
    }
}