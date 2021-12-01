/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2021 Cephetir
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

package me.cephetir.skyskipped.features.impl;

import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestCloser extends Feature {

    public ChestCloser() {
        super("Chest Closer", "Dungeons", "Chests in dungeon will close automatically.");
    }

    @SubscribeEvent
    public void onDrawBackground(GuiOpenEvent event) {
        if (event.gui instanceof GuiChest && Cache.inSkyblock) {
            final String chestName = ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();
            if (Cache.isInDungeon && Config.chestCloser && chestName.equals("Chest")) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
            if (Cache.inSkyblock && Config.chestCloserCH && (chestName.contains("Loot Chest") || chestName.contains("Treasure Chest"))) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
    }
}
