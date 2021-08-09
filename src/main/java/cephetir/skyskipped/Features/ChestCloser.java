/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
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

package cephetir.skyskipped.Features;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestCloser {
    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if(Config.chestCloser && Cache.isInDungeon && screen instanceof GuiChest) {
            ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
            if ("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName())) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre mouseInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if(Config.chestCloser && Cache.isInDungeon && screen instanceof GuiChest) {
            ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
            if ("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName())) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
    }
}
