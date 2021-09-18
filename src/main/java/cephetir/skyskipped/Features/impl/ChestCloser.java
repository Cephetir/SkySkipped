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

package cephetir.skyskipped.Features.impl;

import cephetir.skyskipped.Features.Feature;
import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestCloser extends Feature {

    public ChestCloser() {
        super("Chest Closer", "Dungeons", "Chests in dungeon will close automatically.");
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (Config.chestCloser && Cache.isInDungeon && screen instanceof GuiChest) {
            ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
            if ("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName()))
                Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre mouseInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (Config.chestCloser && Cache.isInDungeon && screen instanceof GuiChest) {
            ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
            if ("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName()))
                Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }
}
