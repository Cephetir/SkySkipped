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
