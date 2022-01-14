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
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.client.event.GuiOpenEvent

class ChestCloser : Feature() {

    override fun onDrawBackground(event: GuiOpenEvent) {
        if (event.gui is GuiChest && Cache.inSkyblock) {
            val chestName =
                ((event.gui as GuiChest).inventorySlots as ContainerChest).lowerChestInventory.displayName.unformattedText
            if (Cache.isInDungeon && Config.chestCloser && chestName == "Chest") {
                Minecraft.getMinecraft().thePlayer.closeScreen()
            }
            if (Cache.inSkyblock && Config.chestCloserCH && (chestName.contains("Loot Chest") || chestName.contains("Treasure Chest"))) {
                Minecraft.getMinecraft().thePlayer.closeScreen()
            }
        }
    }

    override fun isEnabled(): Boolean {
        return Config.chestCloser || Config.chestCloserCH
    }
}